"""
seed_magic_items.py
-------------------
Pobla las siguientes tablas a partir de /v2/magicitems/ (SRD 2024):

    item         → todos los magic items (magic=1, rarity, attunement)
    weapon       → si el item tiene weapon != null
    armor        → si el item tiene armor != null
    feature      → "Weapon +N" / "Armor +N" para items con bonus
    item_feature → relacion item-feature
    bonus_feature_item → feature +N aplicado al item magico, value=N

Ajusta DB_CONFIG con tus credenciales.
"""

import re
import requests
import mysql.connector

DB_CONFIG = {
    "host": "127.0.0.1",
    "database": "DnDB",
    "user": "root",
    "password": "dev",
}

API_URL = "https://api.open5e.com/v2/magicitems/?document__key__in=srd-2024&limit=100"

# feature_type.id segun tu dump: 6 = 'item'
FEATURE_TYPE_ITEM_ID = 6

CATEGORY_MAP = {
    "armor":         "Armor",
    "weapon":        "Weapon",
    "potion":        "Potion",
    "tools":         "Tool",
    "ammunition":    "Ammunition",
}

ARMOR_TYPE_MAP = {
    "light":  "light",
    "medium": "medium",
    "heavy":  "heavy",
}

BONUS_WEAPON_RE = re.compile(r'\+(\d)\s+bonus to attack rolls and damage rolls', re.IGNORECASE)
BONUS_ARMOR_RE  = re.compile(r'\+(\d)\s+bonus to Armor Class', re.IGNORECASE)
BONUS_NAME_RE   = re.compile(r'\(\+(\d)\)$')


# ── Helpers ────────────────────────────────────────────────────────────────────
def fetch_all(url: str) -> list:
    results = []
    while url:
        resp = requests.get(url, timeout=15)
        resp.raise_for_status()
        data = resp.json()
        results.extend(data.get("results", []))
        url = data.get("next")
    return results


def detect_bonus(name: str, desc: str, is_armor: bool) -> int | None:
    # Primero intentar desde el nombre: "Dagger (+1)"
    m = BONUS_NAME_RE.search(name)
    if m:
        return int(m.group(1))
    # Luego desde el desc
    pattern = BONUS_ARMOR_RE if is_armor else BONUS_WEAPON_RE
    m = pattern.search(desc)
    if m:
        return int(m.group(1))
    return None


def get_item_type_id(cursor, name: str, cache: dict) -> int:
    if name in cache:
        return cache[name]
    cursor.execute("SELECT id FROM item_type WHERE name = %s", (name,))
    row = cursor.fetchone()
    if not row:
        cursor.execute("INSERT INTO item_type (name) VALUES (%s)", (name,))
        cursor.execute("SELECT id FROM item_type WHERE name = %s", (name,))
        row = cursor.fetchone()
    cache[name] = row[0]
    return row[0]


def get_armor_type_id(cursor, category: str, cache: dict) -> int | None:
    if category in cache:
        return cache[category]
    cursor.execute("SELECT id FROM armor_type WHERE name = %s", (category,))
    row = cursor.fetchone()
    if row:
        cache[category] = row[0]
        return row[0]
    return None


def get_damage_type_id(cursor, name: str, cache: dict) -> int | None:
    key = name.lower()
    if key in cache:
        return cache[key]
    cursor.execute("SELECT id FROM damage_type WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row:
        cache[key] = row[0]
        return row[0]
    # Crear si no existe
    cursor.execute("INSERT IGNORE INTO damage_type (name) VALUES (%s)", (name,))
    cursor.execute("SELECT id FROM damage_type WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row:
        cache[key] = row[0]
        return row[0]
    return None


def get_mastery_id(cursor, name: str, desc: str, cache: dict) -> int | None:
    key = name.lower()
    if key in cache:
        return cache[key]
    cursor.execute("SELECT id FROM mastery WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row:
        cache[key] = row[0]
        return row[0]
    cursor.execute("INSERT IGNORE INTO mastery (name, description) VALUES (%s, %s)", (name, desc))
    cursor.execute("SELECT id FROM mastery WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row:
        cache[key] = row[0]
        return row[0]
    return None


def get_weapon_property_id(cursor, name: str, desc: str, cache: dict) -> int | None:
    key = name.lower()
    if key in cache:
        return cache[key]
    cursor.execute("SELECT id FROM weapon_property WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row:
        cache[key] = row[0]
        return row[0]
    cursor.execute("INSERT IGNORE INTO weapon_property (name, description) VALUES (%s, %s)", (name, desc))
    cursor.execute("SELECT id FROM weapon_property WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row:
        cache[key] = row[0]
        return row[0]
    return None


def ensure_bonus_feature(cursor, label: str, cache: dict) -> int:
    """Crea o reutiliza feature 'Weapon +1', 'Armor +2', etc."""
    if label in cache:
        return cache[label]
    cursor.execute(
        "INSERT IGNORE INTO `feature` (name, type) VALUES (%s, %s)",
        (label, FEATURE_TYPE_ITEM_ID),
    )
    cursor.execute("SELECT id FROM `feature` WHERE name = %s", (label,))
    row = cursor.fetchone()
    cache[label] = row[0]
    return row[0]


# ── Seeder principal ───────────────────────────────────────────────────────────
def seed_magic_items(cursor, items: list):
    print("\n── Magic Items ──────────────────────────────────────")

    item_type_cache    = {}
    armor_type_cache   = {}
    damage_type_cache  = {}
    mastery_cache      = {}
    wp_cache           = {}
    bonus_feat_cache   = {}

    # Pre-cargar caches
    cursor.execute("SELECT id, name FROM item_type")
    for r in cursor.fetchall(): item_type_cache[r[1]] = r[0]

    cursor.execute("SELECT id, name FROM armor_type")
    for r in cursor.fetchall(): armor_type_cache[r[1].lower()] = r[0]

    cursor.execute("SELECT id, name FROM damage_type")
    for r in cursor.fetchall(): damage_type_cache[r[1].lower()] = r[0]

    cursor.execute("SELECT id, name FROM mastery")
    for r in cursor.fetchall(): mastery_cache[r[1].lower()] = r[0]

    cursor.execute("SELECT id, name FROM weapon_property")
    for r in cursor.fetchall(): wp_cache[r[1].lower()] = r[0]

    cursor.execute("SELECT id, name FROM `feature` WHERE type = %s", (FEATURE_TYPE_ITEM_ID,))
    for r in cursor.fetchall(): bonus_feat_cache[r[1]] = r[0]

    inserted = 0
    skipped  = 0

    for mi in items:
        name     = (mi.get("name") or "").strip()
        desc     = (mi.get("desc") or "").strip()
        rarity   = (mi.get("rarity") or {}).get("name") or None
        attune   = 1 if mi.get("requires_attunement") else 0
        weight   = float(mi.get("weight") or 0)
        price    = int(float(mi.get("cost") or 0))
        cat_key  = (mi.get("category") or {}).get("key", "")
        w_data   = mi.get("weapon")
        a_data   = mi.get("armor")

        if not name:
            continue

        # Determinar item_type
        if a_data:
            type_name = "Armor"
        elif w_data:
            type_name = "Weapon"
        else:
            type_name = CATEGORY_MAP.get(cat_key, "Item")

        item_type_id = get_item_type_id(cursor, type_name, item_type_cache)

        # Insertar en item
        cursor.execute(
            """
            INSERT INTO `item` (name, weight, price, item_type_id, magic, attunement, rarity)
            VALUES (%s, %s, %s, %s, 1, %s, %s)
            ON DUPLICATE KEY UPDATE
                magic      = 1,
                attunement = VALUES(attunement),
                rarity     = VALUES(rarity)
            """,
            (name, weight, price, item_type_id, attune, rarity),
        )
        cursor.execute("SELECT id FROM `item` WHERE name = %s", (name,))
        row = cursor.fetchone()
        if not row:
            skipped += 1
            continue
        item_id = row[0]

        # ── Armor ──────────────────────────────────────────────────────────────
        if a_data:
            category    = (a_data.get("category") or "").lower()
            armor_type_id = get_armor_type_id(cursor, category, armor_type_cache)
            if not armor_type_id:
                print(f"   ? armor_type '{category}' no encontrado para '{name}'")
            else:
                ac_base   = a_data.get("ac_base") or 0
                cap_dex   = a_data.get("ac_cap_dexmod")
                ac_max    = (ac_base + cap_dex) if cap_dex is not None else None
                str_min   = a_data.get("strength_score_required") or 0
                stealth   = 1 if a_data.get("grants_stealth_disadvantage") else 0

                cursor.execute(
                    """
                    INSERT INTO `armor` (item_id, ac_base, ac_max, str_min, stealth_dis, armor_type_id)
                    VALUES (%s, %s, %s, %s, %s, %s)
                    ON DUPLICATE KEY UPDATE
                        ac_base      = VALUES(ac_base),
                        ac_max       = VALUES(ac_max),
                        str_min      = VALUES(str_min),
                        stealth_dis  = VALUES(stealth_dis),
                        armor_type_id = VALUES(armor_type_id)
                    """,
                    (item_id, ac_base, ac_max, str_min, stealth, armor_type_id),
                )

            # Detectar bonus de armor
            bonus = detect_bonus(name, desc, is_armor=True)
            if bonus:
                label      = f"Armor +{bonus}"
                feat_id    = ensure_bonus_feature(cursor, label, bonus_feat_cache)
                cursor.execute(
                    "INSERT IGNORE INTO `item_feature` (item_id, feature_id) VALUES (%s, %s)",
                    (item_id, feat_id),
                )
                cursor.execute(
                    """
                    INSERT INTO `bonus_feature_item` (feature_id, item_id, value)
                    VALUES (%s, %s, %s)
                    ON DUPLICATE KEY UPDATE value = VALUES(value)
                    """,
                    (feat_id, item_id, bonus),
                )

        # ── Weapon ─────────────────────────────────────────────────────────────
        elif w_data:
            dmg_type_name = (w_data.get("damage_type") or {}).get("name", "")
            damage_type_id = get_damage_type_id(cursor, dmg_type_name, damage_type_cache) if dmg_type_name else None
            damage_dice    = (w_data.get("damage_dice") or "").strip()
            range_normal   = w_data.get("range") or 0
            range_long     = w_data.get("long_range") or 0
            is_simple      = w_data.get("is_simple", False)
            weapon_category = "Simple" if is_simple else "Martial"
            weapon_type     = "Ranged" if range_normal > 0 else "Melee"

            # Mastery desde properties
            mastery_id = None
            properties = []
            for p in w_data.get("properties") or []:
                prop  = p.get("property") or {}
                pname = (prop.get("name") or "").strip()
                pdesc = (prop.get("desc") or "").strip()
                ptype = prop.get("type")
                detail = p.get("detail")
                if ptype == "Mastery":
                    mastery_id = get_mastery_id(cursor, pname, pdesc, mastery_cache)
                elif pname:
                    prop_id = get_weapon_property_id(cursor, pname, pdesc, wp_cache)
                    if prop_id:
                        properties.append((prop_id, detail))

            if damage_type_id and mastery_id:
                cursor.execute(
                    """
                    INSERT INTO `weapon`
                        (item_id, damage_dice, damage_type_id, mastery_id,
                         range_normal, range_long, weapon_category, weapon_type)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                    ON DUPLICATE KEY UPDATE
                        damage_dice     = VALUES(damage_dice),
                        damage_type_id  = VALUES(damage_type_id),
                        mastery_id      = VALUES(mastery_id),
                        range_normal    = VALUES(range_normal),
                        range_long      = VALUES(range_long),
                        weapon_category = VALUES(weapon_category),
                        weapon_type     = VALUES(weapon_type)
                    """,
                    (item_id, damage_dice, damage_type_id, mastery_id,
                     range_normal, range_long, weapon_category, weapon_type),
                )
                for prop_id, detail in properties:
                    cursor.execute(
                        "INSERT IGNORE INTO weapon_weapon_property (weapon_id, property_id, value) VALUES (%s, %s, %s)",
                        (item_id, prop_id, detail),
                    )

            # Detectar bonus de weapon
            bonus = detect_bonus(name, desc, is_armor=False)
            if bonus:
                label   = f"Weapon +{bonus}"
                feat_id = ensure_bonus_feature(cursor, label, bonus_feat_cache)
                cursor.execute(
                    "INSERT IGNORE INTO `item_feature` (item_id, feature_id) VALUES (%s, %s)",
                    (item_id, feat_id),
                )
                cursor.execute(
                    """
                    INSERT INTO `bonus_feature_item` (feature_id, item_id, value)
                    VALUES (%s, %s, %s)
                    ON DUPLICATE KEY UPDATE value = VALUES(value)
                    """,
                    (feat_id, item_id, bonus),
                )

        # ── Items sin weapon ni armor: detectar bonus igualmente ───────────────
        else:
            bonus = detect_bonus(name, desc, is_armor=False)
            if not bonus:
                bonus = detect_bonus(name, desc, is_armor=True)
            if bonus:
                # Determinar si es weapon o armor por categoria
                label = f"Armor +{bonus}" if cat_key == "armor" else f"Weapon +{bonus}"
                feat_id = ensure_bonus_feature(cursor, label, bonus_feat_cache)
                cursor.execute(
                    "INSERT IGNORE INTO `item_feature` (item_id, feature_id) VALUES (%s, %s)",
                    (item_id, feat_id),
                )
                cursor.execute(
                    """
                    INSERT INTO `bonus_feature_item` (feature_id, item_id, value)
                    VALUES (%s, %s, %s)
                    ON DUPLICATE KEY UPDATE value = VALUES(value)
                    """,
                    (feat_id, item_id, bonus),
                )

        inserted += 1
        bonus_str = f" +{bonus}" if (bonus := detect_bonus(name, desc, a_data is not None)) else ""
        print(f"   v {name} [{type_name}] {rarity or ''}{bonus_str}")

    print(f"\n   -> {inserted} insertados, {skipped} saltados de {len(items)} magic items")


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching magic items from open5e API...")
    items = fetch_all(API_URL)
    print(f"  -> {len(items)} magic items recibidos")

    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        seed_magic_items(cursor, items)
        conn.commit()
        print("\nSeed completado y commiteado.")
    except Exception as e:
        conn.rollback()
        print(f"\nError — rollback: {e}")
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()