"""
seed_items.py
-------------
Pobla item, weapon, weapon_damage, weapon_weapon_property,
armor, shield, item_feature, bonus_feature_item
desde /v2/items/ y /v2/magicitems/ (SRD 2024).

Ejecutar: python seed_items.py
"""

import re
import requests
import mysql.connector

# ── Configuracion ──────────────────────────────────────────────────────────────
DB_CONFIG = {
    "host":     "127.0.0.1",
    "database": "DnDB",
    "user":     "root",
    "password": "dev",
}

ITEMS_URL       = "https://api.open5e.com/v2/items/?document__key__in=srd-2024&limit=100"
MAGIC_ITEMS_URL = "https://api.open5e.com/v2/magicitems/?document__key__in=srd-2024&limit=100"

FEATURE_TYPE_ITEM_ID = 6  # feature_type.id donde name = 'item'

CATEGORY_MAP = {
    "weapon":             "Weapon",
    "armor":              "Armor",
    "shield":             "Shield",
    "potion":             "Potion",
    "tools":              "Tool",
    "ammunition":         "Ammunition",
    "spellcasting-focus": "Wondrous",
    "wondrous-item":      "Wondrous",
    "ring":               "Wondrous",
    "staff":              "Wondrous",
    "wand":               "Wondrous",
    "mount":              "Vehicle",
    "waterborne-vehicle": "Vehicle",
    "land-vehicle":       "Vehicle",
    "rod":                "Gear",
    "adventuring-gear":   "Gear",
    "equipment-pack":     "Gear",
    "trade-goods":        "Gear",
    "treasure":           "Gear",
    # fallback -> "Gear"
}

BONUS_WEAPON_RE = re.compile(r'\+(\d)\s+bonus to attack rolls and damage rolls', re.IGNORECASE)
BONUS_ARMOR_RE  = re.compile(r'\+(\d)\s+bonus to Armor Class', re.IGNORECASE)
BONUS_NAME_RE   = re.compile(r'\(\+(\d)\)\s*$')


# ── Fetch ──────────────────────────────────────────────────────────────────────
def fetch_all(url: str) -> list:
    results = []
    while url:
        resp = requests.get(url, timeout=15)
        resp.raise_for_status()
        data = resp.json()
        results.extend(data.get("results", []))
        url = data.get("next")
    return results


# ── Migraciones ────────────────────────────────────────────────────────────────
def migrate(cursor):
    print("\n── Migraciones ──────────────────────────────────────")

    for t in ("Weapon", "Armor", "Shield", "Potion", "Tool",
              "Ammunition", "Wondrous", "Vehicle", "Gear"):
        cursor.execute("INSERT IGNORE INTO item_type (name) VALUES (%s)", (t,))

    for t in ("light", "medium", "heavy"):
        cursor.execute("INSERT IGNORE INTO armor_type (name) VALUES (%s)", (t,))

    print("   . item_type y armor_type seeded")


# ── Cache helpers ──────────────────────────────────────────────────────────────
def load_cache(cursor, table: str, key_col: str,
               lower_key: bool = False) -> dict:
    cursor.execute(f"SELECT id, `{key_col}` FROM `{table}`")
    if lower_key:
        return {row[1].lower(): row[0] for row in cursor.fetchall()}
    return {row[1]: row[0] for row in cursor.fetchall()}


def ensure(cursor, table: str, key_col: str, key_val: str,
           extra_cols: dict | None = None, cache: dict | None = None,
           lower_key: bool = False) -> int | None:
    """Inserta si no existe y devuelve el id."""
    cache_key = key_val.lower() if lower_key else key_val
    if cache is not None and cache_key in cache:
        return cache[cache_key]

    cols    = [key_col] + list(extra_cols.keys() if extra_cols else [])
    vals    = [key_val] + list(extra_cols.values() if extra_cols else [])
    ph      = ", ".join(["%s"] * len(vals))
    col_str = ", ".join(f"`{c}`" for c in cols)

    cursor.execute(
        f"INSERT IGNORE INTO `{table}` ({col_str}) VALUES ({ph})", vals
    )
    cursor.execute(f"SELECT id FROM `{table}` WHERE `{key_col}` = %s", (key_val,))
    row = cursor.fetchone()
    if row and cache is not None:
        cache[cache_key] = row[0]
    return row[0] if row else None


def cost_to_int(cost_str) -> int:
    """GP x 100 -> entero. '25.00' -> 2500, '0.10' -> 10."""
    try:
        return int(round(float(cost_str) * 100))
    except (TypeError, ValueError):
        return 0


def detect_bonus(name: str, desc: str, is_armor: bool) -> int | None:
    m = BONUS_NAME_RE.search(name)
    if m:
        return int(m.group(1))
    pattern = BONUS_ARMOR_RE if is_armor else BONUS_WEAPON_RE
    m = pattern.search(desc or "")
    return int(m.group(1)) if m else None


# ── Procesadores ───────────────────────────────────────────────────────────────
def process_weapon(cursor, item_id: int, weapon_data: dict, caches: dict) -> bool:
    """Inserta en weapon + weapon_damage + weapon_weapon_property."""

    damage_dice   = (weapon_data.get("damage_dice") or "").strip()
    dmg_type_name = (weapon_data.get("damage_type") or {}).get("name", "").strip()

    if not dmg_type_name:
        print(f"     ! sin damage_type, item_id={item_id} saltado")
        return False

    damage_type_id = ensure(cursor, "damage_type", "name", dmg_type_name,
                            cache=caches["damage"], lower_key=True)

    weapon_category = "Simple" if weapon_data.get("is_simple") else "Martial"

    mastery_id = None
    properties = []
    prop_names = set()

    for p in weapon_data.get("properties") or []:
        prop   = p.get("property") or {}
        pname  = (prop.get("name") or "").strip()
        pdesc  = (prop.get("desc") or "").strip()
        ptype  = prop.get("type")
        detail = p.get("detail")
        prop_names.add(pname.lower())

        if ptype == "Mastery":
            mastery_id = ensure(cursor, "mastery", "name", pname,
                                extra_cols={"description": pdesc},
                                cache=caches["mastery"], lower_key=True)
        elif pname:
            prop_id = ensure(cursor, "weapon_property", "name", pname,
                             extra_cols={"description": pdesc},
                             cache=caches["property"], lower_key=True)
            properties.append((prop_id, detail))

    if mastery_id is None:
        print(f"     ! sin mastery, item_id={item_id} saltado")
        return False

    weapon_type = "Ranged" if {"ammunition", "thrown"} & prop_names else "Melee"

    # weapon
    cursor.execute(
        """
        INSERT INTO `weapon`
            (id, mastery_id, range_normal, range_long, weapon_category, weapon_type)
        VALUES (%s, %s, %s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE
            mastery_id      = VALUES(mastery_id),
            range_normal    = VALUES(range_normal),
            range_long      = VALUES(range_long),
            weapon_category = VALUES(weapon_category),
            weapon_type     = VALUES(weapon_type)
        """,
        (item_id, mastery_id, 0, 0, weapon_category, weapon_type),
    )

    # weapon_damage — PK compuesta (weapon_id, damage_type_id)
    # ON DUPLICATE KEY solo actualiza damage_roll (el tipo no cambia)
    cursor.execute(
        """
        INSERT INTO `weapon_damage`
            (weapon_id, damage_roll, damage_type_id, always)
        VALUES (%s, %s, %s, 1)
        ON DUPLICATE KEY UPDATE
            damage_roll = VALUES(damage_roll)
        """,
        (item_id, damage_dice or None, damage_type_id),
    )

    for prop_id, detail in properties:
        cursor.execute(
            "INSERT IGNORE INTO weapon_weapon_property "
            "(weapon_id, property_id, value) VALUES (%s, %s, %s)",
            (item_id, prop_id, detail),
        )

    return True


def process_armor(cursor, item_id: int, armor_data: dict, caches: dict) -> bool:
    category      = (armor_data.get("category") or "").lower()
    armor_type_id = caches["armor_type"].get(category)

    if not armor_type_id:
        print(f"     ! armor_type '{category}' no reconocido, item_id={item_id} saltado")
        return False

    ac_base = armor_data.get("ac_base") or 0
    dex_cap = armor_data.get("ac_cap_dexmod")

    # light  -> ac_max NULL  (sin techo, el back suma DEX libremente)
    # medium -> ac_base + cap  (ej. 14 + 2 = 16)
    # heavy  -> ac_max = ac_base  (sin DEX)
    if category == "light":
        ac_max = None
    elif category == "medium":
        ac_max = (ac_base + dex_cap) if dex_cap is not None else None
    else:
        ac_max = ac_base

    str_min = armor_data.get("strength_score_required") or 0
    stealth = 1 if armor_data.get("grants_stealth_disadvantage") else 0

    cursor.execute(
        """
        INSERT INTO `armor`
            (id, ac_base, ac_max, str_min, stealth_dis, armor_type_id)
        VALUES (%s, %s, %s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE
            ac_base       = VALUES(ac_base),
            ac_max        = VALUES(ac_max),
            str_min       = VALUES(str_min),
            stealth_dis   = VALUES(stealth_dis),
            armor_type_id = VALUES(armor_type_id)
        """,
        (item_id, ac_base, ac_max, str_min, stealth, armor_type_id),
    )
    return True


def process_shield(cursor, item_id: int, ac_bonus: int = 2):
    cursor.execute(
        """
        INSERT INTO `shield` (id, ac_bonus)
        VALUES (%s, %s)
        ON DUPLICATE KEY UPDATE ac_bonus = VALUES(ac_bonus)
        """,
        (item_id, ac_bonus),
    )


def process_bonus_feature(cursor, item_id: int, name: str, desc: str,
                           is_armor: bool, caches: dict):
    """Detecta +N en nombre/desc y graba feature -> item_feature -> bonus_feature_item."""
    bonus = detect_bonus(name, desc, is_armor)
    if bonus is None:
        return

    label   = f"{'Armor' if is_armor else 'Weapon'} +{bonus}"
    feat_id = ensure(cursor, "feature", "name", label,
                     extra_cols={"type": FEATURE_TYPE_ITEM_ID},
                     cache=caches["bonus_feat"])

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


# ── Upsert item base ───────────────────────────────────────────────────────────
def upsert_item(cursor, name, weight, price, item_type_id,
                magic, attunement, rarity, description=None):
    cursor.execute(
        """
        INSERT INTO `item`
            (name, weight, price, item_type_id, magic, attunement, rarity, description)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE
            weight       = VALUES(weight),
            price        = VALUES(price),
            item_type_id = VALUES(item_type_id),
            magic        = VALUES(magic),
            attunement   = VALUES(attunement),
            rarity       = VALUES(rarity),
            description  = VALUES(description)
        """,
        (name, weight, price, item_type_id, magic, attunement, rarity, description),
    )
    cursor.execute("SELECT id FROM `item` WHERE name = %s", (name,))
    row = cursor.fetchone()
    return row[0] if row else None

# ── Seeder items normales ──────────────────────────────────────────────────────
def seed_items(cursor, items: list, caches: dict):
    print("\n── Items ────────────────────────────────────────────")
    counts = {"item": 0, "weapon": 0, "armor": 0, "shield": 0,
              "generic": 0, "skipped": 0}

    for raw in items:
        name = (raw.get("name") or "").strip()
        if not name:
            continue

        cat_key    = (raw.get("category") or {}).get("key", "")
        has_weapon = raw.get("weapon") is not None
        has_armor  = raw.get("armor")  is not None
        is_shield  = cat_key == "shield"

        if has_weapon:
            type_str = "Weapon"
        elif has_armor:
            type_str = "Armor"
        elif is_shield:
            type_str = "Shield"
        else:
            type_str = CATEGORY_MAP.get(cat_key, "Gear")

        item_type_id = ensure(cursor, "item_type", "name", type_str,
                              cache=caches["item_type"])
        weight = float(raw.get("weight") or 0)
        price  = cost_to_int(raw.get("cost"))
        rarity = raw.get("rarity") or "Common"
        desc = (raw.get("desc") or "").strip() or None
        item_id = upsert_item(cursor, name, weight, price,
                              item_type_id, 0, 0, rarity, desc)
        if not item_id:
            counts["skipped"] += 1
            continue
        counts["item"] += 1

        if has_weapon:
            ok = process_weapon(cursor, item_id, raw["weapon"], caches)
            if ok:
                counts["weapon"] += 1
                print(f"   ⚔  {name}")
            else:
                counts["skipped"] += 1

        elif has_armor:
            ok = process_armor(cursor, item_id, raw["armor"], caches)
            if ok:
                counts["armor"] += 1
                print(f"   🛡  {name}")
            else:
                counts["skipped"] += 1

        elif is_shield:
            process_shield(cursor, item_id)
            counts["shield"] += 1
            print(f"   🛡  {name} [Shield]")

        else:
            counts["generic"] += 1
            print(f"   📦  {name}")

    print(
        f"\n   -> {counts['item']} items  |  {counts['weapon']} weapons  |  "
        f"{counts['armor']} armors  |  {counts['shield']} shields  |  "
        f"{counts['generic']} genericos  |  {counts['skipped']} saltados"
    )


# ── Seeder magic items ─────────────────────────────────────────────────────────
def seed_magic_items(cursor, items: list, caches: dict):
    print("\n── Magic Items ──────────────────────────────────────")
    inserted = 0
    skipped  = 0

    for mi in items:
        name    = (mi.get("name") or "").strip()
        desc    = (mi.get("desc") or "").strip()
        cat_key = (mi.get("category") or {}).get("key", "")
        w_data  = mi.get("weapon")
        a_data  = mi.get("armor")

        if not name:
            continue

        # rarity: magic items devuelven {"name": "Rare"} en vez de string plano
        rarity_raw = mi.get("rarity")
        rarity = (rarity_raw.get("name") if isinstance(rarity_raw, dict)
                  else rarity_raw) or "Common"

        attune    = 1 if mi.get("requires_attunement") else 0
        weight    = float(mi.get("weight") or 0)
        price     = cost_to_int(mi.get("cost"))
        is_shield = cat_key == "shield"

        if a_data:
            type_str = "Armor"
        elif w_data:
            type_str = "Weapon"
        elif is_shield:
            type_str = "Shield"
        else:
            type_str = CATEGORY_MAP.get(cat_key, "Gear")

        item_type_id = ensure(cursor, "item_type", "name", type_str,
                              cache=caches["item_type"])

        item_id = upsert_item(cursor, name, weight, price,
                              item_type_id, 1, attune, rarity, desc)
        if not item_id:
            skipped += 1
            continue

        if is_shield:
            process_shield(cursor, item_id)

        if a_data:
            process_armor(cursor, item_id, a_data, caches)
            process_bonus_feature(cursor, item_id, name, desc,
                                  is_armor=True, caches=caches)

        elif w_data:
            ok = process_weapon(cursor, item_id, w_data, caches)
            if ok:
                process_bonus_feature(cursor, item_id, name, desc,
                                      is_armor=False, caches=caches)

        else:
            # Generico: intentar detectar bonus por descripcion
            if detect_bonus(name, desc, is_armor=True):
                process_bonus_feature(cursor, item_id, name, desc,
                                      is_armor=True, caches=caches)
            elif detect_bonus(name, desc, is_armor=False):
                process_bonus_feature(cursor, item_id, name, desc,
                                      is_armor=False, caches=caches)

        inserted += 1
        print(f"   ✨  {name} [{type_str}] {rarity}")

    print(f"\n   -> {inserted} insertados, {skipped} saltados de {len(items)} magic items")


# ── Caches ─────────────────────────────────────────────────────────────────────
def build_caches(cursor) -> dict:
    return {
        "item_type":  load_cache(cursor, "item_type",      "name"),
        "armor_type": load_cache(cursor, "armor_type",     "name", lower_key=True),
        "damage":     load_cache(cursor, "damage_type",    "name", lower_key=True),
        "property":   load_cache(cursor, "weapon_property","name", lower_key=True),
        "mastery":    load_cache(cursor, "mastery",        "name", lower_key=True),
        "bonus_feat": load_cache(cursor, "feature",        "name"),
    }


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        migrate(cursor)
        conn.commit()

        caches = build_caches(cursor)
        print(
            f"\n   caches -> item_types:{len(caches['item_type'])}  "
            f"armor_types:{len(caches['armor_type'])}  "
            f"damage_types:{len(caches['damage'])}  "
            f"properties:{len(caches['property'])}  "
            f"masteries:{len(caches['mastery'])}"
        )

        print("\nFetching items...")
        items = fetch_all(ITEMS_URL)
        print(f"  -> {len(items)} items recibidos")
        seed_items(cursor, items, caches)
        conn.commit()

        print("\nFetching magic items...")
        magic = fetch_all(MAGIC_ITEMS_URL)
        print(f"  -> {len(magic)} magic items recibidos")
        seed_magic_items(cursor, magic, caches)
        conn.commit()

        print("\nSeed completado.")

    except Exception as e:
        conn.rollback()
        print(f"\nError — rollback: {e}")
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()