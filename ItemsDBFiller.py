"""
seed_items.py
-------------
Pobla las siguientes tablas a partir de /v2/items/ (SRD 2024):

    item                   → fila base (nombre, peso, precio, tipo, magia, rareza)
    weapon                 → stats del arma  (si item.weapon != null)
    weapon_property        → catalogo de propiedades (Finesse, Versatile, …)
    mastery                → catalogo de masteries   (Topple, Vex, …)
    weapon_weapon_property → relacion arma-propiedad
    armor                  → stats de armadura (si item.armor != null)
    armor_type             → catalogo light / medium / heavy

La API de /v2/items/ devuelve precio, peso y descripcion para TODOS los items,
y embebe el objeto `weapon` o `armor` cuando aplica, por lo que un solo recorrido
es suficiente para poblar las tres tablas principales.

Ajusta DB_CONFIG con tus credenciales.
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

# Fuente unificada: items con weapon/armor embebidos
ITEMS_URL = "https://api.open5e.com/v2/items/?document__key__in=srd-2024&limit=100"

# Mapa de categorias de item de la API → item_type.name en la DB
CATEGORY_MAP = {
    "weapon":        "Weapon",
    "armor":         "Armor",
    "wondrous-item": "Item",
    "tools":         "Item",
    "adventuring-gear": "Item",
    "trade-goods":   "Item",
    "mounts-and-vehicles": "Item",
    "treasure":      "Item",
    # fallback: cualquier clave no listada → "Item"
}


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


def column_exists(cursor, table: str, column: str) -> bool:
    cursor.execute(
        "SELECT COUNT(*) FROM information_schema.COLUMNS "
        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = %s AND COLUMN_NAME = %s",
        (table, column),
    )
    return cursor.fetchone()[0] > 0


def table_exists(cursor, table: str) -> bool:
    cursor.execute(
        "SELECT COUNT(*) FROM information_schema.TABLES "
        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = %s",
        (table,),
    )
    return cursor.fetchone()[0] > 0


def cost_to_int(cost_str) -> int:
    """
    Convierte el campo 'cost' de la API (string decimal en GP) a entero de cobres.
    La API devuelve GP como float string, p.ej. "0.10" = 1 sp = 10 cp → guardamos
    centavos de GP*100 para evitar float. Si prefieres guardar GP redondeados,
    cambia el return por int(round(float(cost_str))).
    '25.00' → 2500 cp  |  '0.10' → 10 cp  |  '40000.00' → 4000000 cp
    """
    try:
        return int(round(float(cost_str) * 100))
    except (TypeError, ValueError):
        return 0


def parse_ac_display(ac_display: str):
    """
    Ejemplos:
      '14 + Dex modifier (max 2)' -> (14, 16)
      '16'                        -> (16, 16)
      '12 + Dex modifier'         -> (12, None)
    """

    ac_display = ac_display or ""

    # AC base
    m = re.match(r"(\d+)", ac_display)
    ac_base = int(m.group(1)) if m else 0
    # ¿Tiene Dex modifier?
    has_dex = "dex" in ac_display.lower()
    # ¿Tiene cap?
    cap = re.search(r"max\s+(\d+)", ac_display.lower())
    if cap:
        ac_max = ac_base + int(cap.group(1))
    elif has_dex:
        ac_max = None
    else:
        ac_max = ac_base
    return ac_base, ac_max

# ── Migraciones ────────────────────────────────────────────────────────────────
def migrate(cursor):
    print("\n── Migraciones ──────────────────────────────────────")

    # item: columnas nuevas
    item_cols = [
        ("attunement", "ALTER TABLE `item` ADD COLUMN `attunement` tinyint(1) NOT NULL DEFAULT 0"),
        ("rarity",     "ALTER TABLE `item` ADD COLUMN `rarity` varchar(20) DEFAULT NULL"),
    ]
    for col, sql in item_cols:
        if not column_exists(cursor, "item", col):
            cursor.execute(sql)
            print(f"   -> item.{col} añadida")
        else:
            print(f"   . item.{col} ya existe")

    # weapon: columnas nuevas
    weapon_cols = [
        ("weapon_category",
         "ALTER TABLE `weapon` ADD COLUMN `weapon_category` enum('Simple','Martial') NOT NULL DEFAULT 'Simple'"),
        ("weapon_type",
         "ALTER TABLE `weapon` ADD COLUMN `weapon_type` enum('Melee','Ranged') NOT NULL DEFAULT 'Melee'"),
    ]
    for col, sql in weapon_cols:
        if not column_exists(cursor, "weapon", col):
            cursor.execute(sql)
            print(f"   -> weapon.{col} añadida")
        else:
            print(f"   . weapon.{col} ya existe")

    # armor_type: asegurar filas light/medium/heavy
    for atype in ("light", "medium", "heavy"):
        cursor.execute(
            "INSERT IGNORE INTO armor_type (name) VALUES (%s)", (atype,)
        )

    print("   . armor_type seeded (light/medium/heavy)")


# ── Loaders / ensure helpers ───────────────────────────────────────────────────
def load_item_types(cursor) -> dict:
    cursor.execute("SELECT id, name FROM item_type")
    return {row[1]: row[0] for row in cursor.fetchall()}


def ensure_item_type(cursor, name: str, cache: dict) -> int:
    if name in cache:
        return cache[name]
    cursor.execute("INSERT IGNORE INTO item_type (name) VALUES (%s)", (name,))
    cursor.execute("SELECT id FROM item_type WHERE name = %s", (name,))
    row = cursor.fetchone()
    cache[name] = row[0]
    return row[0]


def load_damage_types(cursor) -> dict:
    cursor.execute("SELECT id, name FROM damage_type")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}


def ensure_damage_type(cursor, name: str, cache: dict) -> int:
    key = name.lower()
    if key in cache:
        return cache[key]
    cursor.execute("INSERT IGNORE INTO damage_type (name) VALUES (%s)", (name,))
    cursor.execute("SELECT id FROM damage_type WHERE name = %s", (name,))
    cache[key] = cursor.fetchone()[0]
    return cache[key]


def load_weapon_properties(cursor) -> dict:
    cursor.execute("SELECT id, name FROM weapon_property")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}


def ensure_weapon_property(cursor, name: str, desc: str, cache: dict) -> int:
    key = name.lower()
    if key in cache:
        return cache[key]
    cursor.execute(
        "INSERT IGNORE INTO weapon_property (name, description) VALUES (%s, %s)",
        (name, desc),
    )
    cursor.execute("SELECT id FROM weapon_property WHERE name = %s", (name,))
    cache[key] = cursor.fetchone()[0]
    return cache[key]


def load_masteries(cursor) -> dict:
    cursor.execute("SELECT id, name FROM mastery")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}


def ensure_mastery(cursor, name: str, desc: str, cache: dict) -> int | None:
    key = name.lower()
    if key in cache:
        return cache[key]
    cursor.execute(
        "INSERT IGNORE INTO mastery (name, description) VALUES (%s, %s)",
        (name, desc),
    )
    cursor.execute("SELECT id FROM mastery WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row:
        cache[key] = row[0]
        return row[0]
    return None


def load_armor_types(cursor) -> dict:
    cursor.execute("SELECT id, name FROM armor_type")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}


# ── Procesadores por tipo ──────────────────────────────────────────────────────
def process_weapon(cursor, item_id: int, weapon_data: dict,
                   damage_cache: dict, property_cache: dict, mastery_cache: dict):
    """Inserta/actualiza fila en `weapon` y sus propiedades."""

    damage_dice = (weapon_data.get("damage_dice") or "").strip()

    dmg_type_obj  = weapon_data.get("damage_type") or {}
    dmg_type_name = (dmg_type_obj.get("name") or "").strip()
    damage_type_id = (
        ensure_damage_type(cursor, dmg_type_name, damage_cache)
        if dmg_type_name else None
    )

    is_simple  = weapon_data.get("is_simple", False)
    # range > 0 en el objeto weapon embebido no siempre está; derivamos de propiedades
    # o de la presencia de "Ammunition" / "Thrown". Fallback: range=0 → Melee.
    weapon_category = "Simple" if is_simple else "Martial"

    # Separar mastery de propiedades normales
    mastery_id = None
    properties = []
    for p in weapon_data.get("properties") or []:
        prop  = p.get("property") or {}
        pname = (prop.get("name") or "").strip()
        pdesc = (prop.get("desc") or "").strip()
        ptype = prop.get("type")
        detail = p.get("detail")

        if ptype == "Mastery":
            mastery_id = ensure_mastery(cursor, pname, pdesc, mastery_cache)
        else:
            if pname:
                prop_id = ensure_weapon_property(cursor, pname, pdesc, property_cache)
                properties.append((prop_id, detail))

    # Inferir weapon_type desde propiedades (Ammunition o Thrown → Ranged)
    ranged_props = {"ammunition", "thrown"}
    prop_names   = {p.get("property", {}).get("name", "").lower()
                    for p in weapon_data.get("properties") or []}
    weapon_type  = "Ranged" if prop_names & ranged_props else "Melee"

    if damage_type_id is None:
        print(f"     ! sin damage_type, weapon item_id={item_id} saltado")
        return False

    if mastery_id is None:
        print(f"     ! sin mastery, weapon item_id={item_id} saltado")
        return False

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
         0, 0, weapon_category, weapon_type),
    )

    for prop_id, detail in properties:
        cursor.execute(
            "INSERT IGNORE INTO weapon_weapon_property (weapon_id, property_id, value) "
            "VALUES (%s, %s, %s)",
            (item_id, prop_id, detail),
        )

    return True


def process_armor(cursor, item_id: int, armor_data: dict, armor_type_cache: dict):
    """Inserta/actualiza fila en `armor`."""

    category = (armor_data.get("category") or "").lower()
    armor_type_id = armor_type_cache.get(category)

    if not armor_type_id:
        print(f"     ! armor_type '{category}' no reconocido, item_id={item_id} saltado")
        return False

    # Parsear AC desde el texto display
    ac_display = armor_data.get("armor_class") or ""
    ac_base, parsed_ac_max = parse_ac_display(ac_display)

    # LOGICA QUE QUIERES:
    #
    # light  -> solo ac_base, ac_max NULL
    # medium -> ac_base y ac_max
    # heavy  -> ac_base y ac_max iguales

    if category == "light":
        ac_max = None

    elif category == "medium":
        ac_max = parsed_ac_max

    elif category == "heavy":
        ac_max = ac_base

    else:
        ac_max = None

    str_min = armor_data.get("strength_score_required") or 0
    stealth = 1 if armor_data.get("grants_stealth_disadvantage") else 0

    cursor.execute(
        """
        INSERT INTO `armor`
            (item_id, ac_base, ac_max, str_min, stealth_dis, armor_type_id)
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

# ── Seeder principal ───────────────────────────────────────────────────────────
def seed_items(cursor, items: list,
               item_type_cache: dict, armor_type_cache: dict,
               damage_cache: dict, property_cache: dict, mastery_cache: dict):

    print("\n── Items ────────────────────────────────────────────")

    counts = {"item": 0, "weapon": 0, "armor": 0, "generic": 0, "skipped": 0}

    for raw in items:
        name = (raw.get("name") or "").strip()
        if not name:
            continue

        # Determinar item_type
        cat_key  = (raw.get("category") or {}).get("key", "")
        type_str = CATEGORY_MAP.get(cat_key, "Item")

        # Si tiene sub-objeto weapon/armor, forzar el tipo correcto
        has_weapon = raw.get("weapon") is not None
        has_armor  = raw.get("armor")  is not None
        if has_weapon:
            type_str = "Weapon"
        elif has_armor:
            type_str = "Armor"

        item_type_id = ensure_item_type(cursor, type_str, item_type_cache)
        weight       = float(raw.get("weight") or 0)
        price        = cost_to_int(raw.get("cost"))

        # Insertar fila base en `item`
        cursor.execute(
            """
            INSERT INTO `item`
                (name, weight, price, item_type_id, magic, attunement, rarity)
            VALUES (%s, %s, %s, %s, 0, 0, NULL)
            ON DUPLICATE KEY UPDATE
                weight       = VALUES(weight),
                price        = VALUES(price),
                item_type_id = VALUES(item_type_id)
            """,
            (name, weight, price, item_type_id),
        )
        cursor.execute("SELECT id FROM `item` WHERE name = %s", (name,))
        row = cursor.fetchone()
        if not row:
            counts["skipped"] += 1
            continue
        item_id = row[0]
        counts["item"] += 1

        # Sub-proceso weapon
        if has_weapon:
            ok = process_weapon(
                cursor, item_id, raw["weapon"],
                damage_cache, property_cache, mastery_cache,
            )
            if ok:
                counts["weapon"] += 1
                print(f"   ⚔  {name}")
            else:
                counts["skipped"] += 1

        # Sub-proceso armor
        elif has_armor:
            ok = process_armor(cursor, item_id, raw["armor"], armor_type_cache)
            if ok:
                counts["armor"] += 1
                print(f"   🛡  {name}")
            else:
                counts["skipped"] += 1

        else:
            counts["generic"] += 1
            print(f"   📦  {name}")

    print(
        f"\n   -> {counts['item']} items base  |  "
        f"{counts['weapon']} weapons  |  "
        f"{counts['armor']} armors  |  "
        f"{counts['generic']} genericos  |  "
        f"{counts['skipped']} saltados"
    )


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching items from open5e API...")
    items = fetch_all(ITEMS_URL)
    print(f"  -> {len(items)} items recibidos")

    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        migrate(cursor)
        conn.commit()

        # Cargar caches
        item_type_cache  = load_item_types(cursor)
        armor_type_cache = load_armor_types(cursor)
        damage_cache     = load_damage_types(cursor)
        property_cache   = load_weapon_properties(cursor)
        mastery_cache    = load_masteries(cursor)

        print(
            f"\n   -> {len(item_type_cache)} item_types  |  "
            f"{len(armor_type_cache)} armor_types  |  "
            f"{len(damage_cache)} damage_types  |  "
            f"{len(property_cache)} weapon_properties  |  "
            f"{len(mastery_cache)} masteries"
        )

        seed_items(
            cursor, items,
            item_type_cache, armor_type_cache,
            damage_cache, property_cache, mastery_cache,
        )

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