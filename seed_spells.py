"""
seed_spells.py
--------------
Pobla las siguientes tablas a partir de la API open5e v2 (SRD 2024):

    spell          → hechizos
    spell_upcast   → escalado por slot o nivel de personaje (cantrips)
    class_spell    → relacion clase-hechizo
    subclass_spell → relacion subclase-hechizo

Tambien aplica los ALTER TABLE necesarios si las columnas/tablas no existen aun.

Ajusta DB_CONFIG con tus credenciales.
"""

import requests
import mysql.connector

# ── Configuracion ──────────────────────────────────────────────────────────────
DB_CONFIG = {
    "host": "127.0.0.1",
    "database": "DnDB",
    "user": "root",
    "password": "dev",
}

API_URL = "https://api.open5e.com/v2/spells/?document__key__in=srd-2024&limit=100"

SAVING_THROW_MAP = {
    "strength":     "STR",
    "dexterity":    "DEX",
    "constitution": "CON",
    "intelligence": "INT",
    "wisdom":       "WIS",
    "charisma":     "CHA",
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


def build_components(verbal: bool, somatic: bool, material: bool) -> str:
    parts = []
    if verbal:   parts.append("V")
    if somatic:  parts.append("S")
    if material: parts.append("M")
    return ",".join(parts)


def parse_casting_option(opt: dict) -> tuple | None:
    """
    Devuelve (type_enum, level) o None si no reconoce el formato.
    'slot_level_3'   -> ('SLOT',    3)
    'player_level_5' -> ('CANTRIP', 5)
    """
    t = opt.get("type", "")
    if t.startswith("slot_level_"):
        return ("SLOT", int(t.split("_")[-1]))
    elif t.startswith("player_level_"):
        return ("CANTRIP", int(t.split("_")[-1]))
    return None


def column_exists(cursor, table: str, column: str) -> bool:
    cursor.execute(
        """
        SELECT COUNT(*) FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = %s AND COLUMN_NAME = %s
        """,
        (table, column),
    )
    return cursor.fetchone()[0] > 0


def table_exists(cursor, table: str) -> bool:
    cursor.execute(
        """
        SELECT COUNT(*) FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = %s
        """,
        (table,),
    )
    return cursor.fetchone()[0] > 0


# ── Migraciones ────────────────────────────────────────────────────────────────
def migrate(cursor):
    print("\n── Migraciones ──────────────────────────────────────")

    # Quitar higher_levels de spell
    if column_exists(cursor, "spell", "higher_levels"):
        cursor.execute("ALTER TABLE `spell` DROP COLUMN `higher_levels`")
        print("   -> higher_levels eliminado de spell")

    # Nuevas columnas en spell
    new_columns = [
        ("attack_roll",          "ALTER TABLE `spell` ADD COLUMN `attack_roll` tinyint(1) NOT NULL DEFAULT 0"),
        ("saving_throw_stat_id", "ALTER TABLE `spell` ADD COLUMN `saving_throw_stat_id` int DEFAULT NULL"),
        ("damage_roll",          "ALTER TABLE `spell` ADD COLUMN `damage_roll` varchar(20) DEFAULT NULL"),
        ("damage_type_id",       "ALTER TABLE `spell` ADD COLUMN `damage_type_id` int DEFAULT NULL"),
    ]
    for col, sql in new_columns:
        if not column_exists(cursor, "spell", col):
            cursor.execute(sql)
            print(f"   -> columna spell.{col} añadida")
        else:
            print(f"   . spell.{col} ya existe")

    # Foreign keys (ignorar si ya existen)
    fks = [
        ("spell_saving_throw_stat",
         "ALTER TABLE `spell` ADD CONSTRAINT `spell_saving_throw_stat` FOREIGN KEY (`saving_throw_stat_id`) REFERENCES `stat` (`id`)"),
        ("spell_damage_type",
         "ALTER TABLE `spell` ADD CONSTRAINT `spell_damage_type` FOREIGN KEY (`damage_type_id`) REFERENCES `damage_type` (`id`)"),
    ]
    for name, sql in fks:
        try:
            cursor.execute(sql)
            print(f"   -> FK {name} añadida")
        except mysql.connector.errors.DatabaseError:
            print(f"   . FK {name} ya existe")

    # Tabla spell_upcast
    if not table_exists(cursor, "spell_upcast"):
        cursor.execute("""
            CREATE TABLE `spell_upcast` (
              `spell_id`    int         NOT NULL,
              `level`       int         NOT NULL,
              `type`        enum('SLOT','CANTRIP') NOT NULL DEFAULT 'SLOT',
              `damage_roll` varchar(20) DEFAULT NULL,
              `desc`        text        DEFAULT NULL,
              PRIMARY KEY (`spell_id`, `level`, `type`),
              CONSTRAINT `spell_upcast_spell` FOREIGN KEY (`spell_id`) REFERENCES `spell` (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """)
        print("   -> tabla spell_upcast creada")
    else:
        print("   . spell_upcast ya existe")

    # Tabla subclass_spell
    if not table_exists(cursor, "subclass_spell"):
        cursor.execute("""
            CREATE TABLE `subclass_spell` (
              `subclass_id` int NOT NULL,
              `spell_id`    int NOT NULL,
              PRIMARY KEY (`subclass_id`, `spell_id`),
              KEY `subclass_spell_spell` (`spell_id`),
              CONSTRAINT `subclass_spell_subclass` FOREIGN KEY (`subclass_id`) REFERENCES `subclass` (`id`),
              CONSTRAINT `subclass_spell_spell`    FOREIGN KEY (`spell_id`)    REFERENCES `spell` (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """)
        print("   -> tabla subclass_spell creada")
    else:
        print("   . subclass_spell ya existe")


# ── Loaders ────────────────────────────────────────────────────────────────────
def load_spell_schools(cursor) -> dict:
    cursor.execute("SELECT id, name FROM spell_school")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}

def load_classes(cursor) -> dict:
    cursor.execute("SELECT id, name FROM `class`")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}

def load_subclasses(cursor) -> dict:
    cursor.execute("SELECT id, name FROM `subclass`")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}

def load_stats(cursor) -> dict:
    cursor.execute("SELECT id, code FROM stat")
    return {row[1]: row[0] for row in cursor.fetchall()}

def load_damage_types(cursor) -> dict:
    cursor.execute("SELECT id, name FROM damage_type")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}


# ── Seeders ────────────────────────────────────────────────────────────────────
def seed_spells(cursor, spells: list, school_map: dict, stat_map: dict, damage_type_map: dict) -> dict:
    print("\n── Spells ───────────────────────────────────────────")
    id_map  = {}
    skipped = 0

    for s in spells:
        name = (s.get("name") or "").strip()
        if not name:
            continue

        school_name = (s.get("school") or {}).get("name", "").lower()
        school_id   = school_map.get(school_name)
        if not school_id:
            print(f"   ? escuela '{school_name}' no encontrada, saltando '{name}'")
            skipped += 1
            continue

        # Saving throw stat
        st_raw  = (s.get("saving_throw_ability") or "").lower()
        st_code = SAVING_THROW_MAP.get(st_raw)
        saving_throw_stat_id = stat_map.get(st_code) if st_code else None

        # Damage type (primer elemento del array)
        dmg_types   = s.get("damage_types") or []
        dmg_type_id = damage_type_map.get(dmg_types[0].lower()) if dmg_types else None

        cursor.execute(
            """
            INSERT INTO `spell`
                (name, level, school_id, casting_time, `range`, duration,
                 components, material, concentration, ritual, description,
                 attack_roll, saving_throw_stat_id, damage_roll, damage_type_id)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                level                = VALUES(level),
                school_id            = VALUES(school_id),
                casting_time         = VALUES(casting_time),
                `range`              = VALUES(`range`),
                duration             = VALUES(duration),
                components           = VALUES(components),
                material             = VALUES(material),
                concentration        = VALUES(concentration),
                ritual               = VALUES(ritual),
                description          = VALUES(description),
                attack_roll          = VALUES(attack_roll),
                saving_throw_stat_id = VALUES(saving_throw_stat_id),
                damage_roll          = VALUES(damage_roll),
                damage_type_id       = VALUES(damage_type_id)
            """,
            (
                name,
                s.get("level", 0),
                school_id,
                (s.get("casting_time") or "")[:60],
                (s.get("range_text") or "")[:40],
                (s.get("duration") or "")[:60],
                build_components(s.get("verbal", False), s.get("somatic", False), s.get("material", False)),
                (s.get("material_specified") or "")[:255],
                1 if s.get("concentration") else 0,
                1 if s.get("ritual") else 0,
                s.get("desc") or "",
                1 if s.get("attack_roll") else 0,
                saving_throw_stat_id,
                (s.get("damage_roll") or None),
                dmg_type_id,
            ),
        )

        cursor.execute("SELECT id FROM `spell` WHERE name = %s", (name,))
        row = cursor.fetchone()
        if row:
            id_map[name] = row[0]

    print(f"   -> {len(id_map)} hechizos procesados, {skipped} saltados")
    return id_map


def seed_spell_upcasts(cursor, spells: list, spell_id_map: dict):
    print("\n── Spell upcasts ────────────────────────────────────")
    inserted = 0

    for s in spells:
        spell_name = (s.get("name") or "").strip()
        spell_id   = spell_id_map.get(spell_name)
        if not spell_id:
            continue

        for opt in s.get("casting_options") or []:
            parsed = parse_casting_option(opt)
            if not parsed:
                continue
            upcast_type, level = parsed
            damage_roll = opt.get("damage_roll") or None
            desc        = opt.get("desc") or None

            # Solo insertar si hay algo util
            if damage_roll is None and desc is None:
                continue

            cursor.execute(
                """
                INSERT INTO `spell_upcast` (spell_id, level, type, damage_roll, `desc`)
                VALUES (%s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                    damage_roll = VALUES(damage_roll),
                    `desc`      = VALUES(`desc`)
                """,
                (spell_id, level, upcast_type, damage_roll, desc),
            )
            if cursor.rowcount:
                inserted += 1

    print(f"   -> {inserted} filas de upcast insertadas")


def seed_class_spells(cursor, spells: list, spell_id_map: dict, class_map: dict, subclass_map: dict):
    print("\n── Class & subclass spells ──────────────────────────")
    class_ins    = 0
    subclass_ins = 0
    skipped      = 0

    for s in spells:
        spell_id = spell_id_map.get((s.get("name") or "").strip())
        if not spell_id:
            continue

        for cls in s.get("classes") or []:
            cls_name = (cls.get("name") or "").lower()
            class_id = class_map.get(cls_name)
            if class_id:
                cursor.execute(
                    "INSERT IGNORE INTO `class_spell` (class_id, spell_id) VALUES (%s, %s)",
                    (class_id, spell_id),
                )
                if cursor.rowcount:
                    class_ins += 1
            else:
                subclass_id = subclass_map.get(cls_name)
                if subclass_id:
                    cursor.execute(
                        "INSERT IGNORE INTO `subclass_spell` (subclass_id, spell_id) VALUES (%s, %s)",
                        (subclass_id, spell_id),
                    )
                    if cursor.rowcount:
                        subclass_ins += 1
                else:
                    skipped += 1

    print(f"   -> {class_ins} class_spell, {subclass_ins} subclass_spell insertadas")
    print(f"   -> {skipped} saltadas (ni clase ni subclase encontrada)")


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching spells from open5e API...")
    spells = fetch_all(API_URL)
    print(f"  -> {len(spells)} hechizos recibidos")

    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        migrate(cursor)
        conn.commit()  # commit migraciones antes de los inserts

        school_map      = load_spell_schools(cursor)
        class_map       = load_classes(cursor)
        subclass_map    = load_subclasses(cursor)
        stat_map        = load_stats(cursor)
        damage_type_map = load_damage_types(cursor)
        print(f"\n   -> {len(school_map)} escuelas, {len(class_map)} clases, "
              f"{len(subclass_map)} subclases, {len(stat_map)} stats, "
              f"{len(damage_type_map)} damage types cargados")

        spell_id_map = seed_spells(cursor, spells, school_map, stat_map, damage_type_map)
        seed_spell_upcasts(cursor, spells, spell_id_map)
        seed_class_spells(cursor, spells, spell_id_map, class_map, subclass_map)

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