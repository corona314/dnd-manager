"""
seed_backgrounds.py
-------------------
Pobla las siguientes tablas a partir de /v2/backgrounds/ (SRD 2024):

    background       → nombre y descripcion
    background_ability  → los 3 abilities disponibles
    background_skill → las 2 skill proficiencies
    background_feat  → la dote (crea en feat si no existe)
    background_tool  → la tool proficiency (crea en tool si no existe)

Tambien crea las tablas nuevas si no existen.

Ajusta DB_CONFIG con tus credenciales.
"""

import requests
import mysql.connector
import re

DB_CONFIG = {
    "host": "127.0.0.1",
    "database": "DnDB",
    "user": "root",
    "password": "dev",
}

API_URL = "https://api.open5e.com/v2/backgrounds/?document__key__in=srd-2024&limit=50"

# feature_type.id para feats = 4
FEATURE_TYPE_FEAT_ID = 4

STAT_NAME_MAP = {
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


def table_exists(cursor, table: str) -> bool:
    cursor.execute(
        "SELECT COUNT(*) FROM information_schema.TABLES "
        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = %s",
        (table,),
    )
    return cursor.fetchone()[0] > 0

def clean_feat_name(name: str) -> str:
    """
    'Magic Initiate (Cleric)' -> 'Magic Initiate'
    'Alert'                   -> 'Alert'
    """
    return re.sub(r'\s*\(.*?\)', '', name).strip()


def parse_list(text: str) -> list[str]:
    """
    'Insight and Religion'          -> ['Insight', 'Religion']
    'Intelligence, Wisdom, Charisma'-> ['Intelligence', 'Wisdom', 'Charisma']
    'Sleight of Hand and Stealth'   -> ['Sleight of Hand', 'Stealth']
    """
    if " and " in text:
        parts = text.split(" and ")
    else:
        parts = text.split(", ")
    return [p.strip() for p in parts if p.strip()]


# ── Migraciones ────────────────────────────────────────────────────────────────
def migrate(cursor):
    print("\n── Migraciones ──────────────────────────────────────")

    if not table_exists(cursor, "background_ability"):
        cursor.execute("""
            CREATE TABLE `background_ability` (
              `background_id` int NOT NULL,
              `ability_id`       int NOT NULL,
              PRIMARY KEY (`background_id`, `ability_id`),
              CONSTRAINT `background_ability_background` FOREIGN KEY (`background_id`) REFERENCES `background` (`id`),
              CONSTRAINT `background_ability_ability`       FOREIGN KEY (`ability_id`)       REFERENCES `ability` (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """)
        print("   -> background_ability creada")
    else:
        print("   . background_ability ya existe")

    if not table_exists(cursor, "background_feat"):
        cursor.execute("""
            CREATE TABLE `background_feat` (
              `background_id` int NOT NULL,
              `feat_id`       int NOT NULL,
              PRIMARY KEY (`background_id`, `feat_id`),
              CONSTRAINT `background_feat_background` FOREIGN KEY (`background_id`) REFERENCES `background` (`id`),
              CONSTRAINT `background_feat_feat`       FOREIGN KEY (`feat_id`)       REFERENCES `feat` (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """)
        print("   -> background_feat creada")
    else:
        print("   . background_feat ya existe")

    if not table_exists(cursor, "tool"):
        cursor.execute("""
            CREATE TABLE `tool` (
              `id`          int NOT NULL AUTO_INCREMENT,
              `name`        varchar(60) NOT NULL,
              `description` text,
              PRIMARY KEY (`id`),
              UNIQUE KEY `tool_name_UNIQUE` (`name`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """)
        print("   -> tool creada")
    else:
        print("   . tool ya existe")

    if not table_exists(cursor, "background_tool"):
        cursor.execute("""
            CREATE TABLE `background_tool` (
              `background_id` int NOT NULL,
              `tool_id`       int NOT NULL,
              PRIMARY KEY (`background_id`, `tool_id`),
              CONSTRAINT `background_tool_background` FOREIGN KEY (`background_id`) REFERENCES `background` (`id`),
              CONSTRAINT `background_tool_tool`       FOREIGN KEY (`tool_id`)       REFERENCES `tool` (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """)
        print("   -> background_tool creada")
    else:
        print("   . background_tool ya existe")

    if not table_exists(cursor, "character_tool"):
        cursor.execute("""
            CREATE TABLE `character_tool` (
              `character_id` int NOT NULL,
              `tool_id`      int NOT NULL,
              `proficient`   tinyint(1) NOT NULL DEFAULT 0,
              PRIMARY KEY (`character_id`, `tool_id`),
              CONSTRAINT `character_tool_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
              CONSTRAINT `character_tool_tool`      FOREIGN KEY (`tool_id`)      REFERENCES `tool` (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """)
        print("   -> character_tool creada")
    else:
        print("   . character_tool ya existe")


# ── Loaders ────────────────────────────────────────────────────────────────────
def load_abilities(cursor) -> dict:
    """Devuelve {ability_code: ability_id} y {ability_name_lower: ability_id}"""
    cursor.execute("SELECT id, code FROM ability")
    by_code = {row[1]: row[0] for row in cursor.fetchall()}
    # tambien por nombre completo
    by_name = {v.lower(): k for k, v in STAT_NAME_MAP.items()}
    result = {}
    for name, code in STAT_NAME_MAP.items():
        if code in by_code:
            result[name] = by_code[code]
    return result  # {name_lower: ability_id}


def load_skills(cursor) -> dict:
    cursor.execute("SELECT id, name FROM skill")
    return {row[1].lower(): row[0] for row in cursor.fetchall()}


def ensure_feat(cursor, name: str) -> int:
    cursor.execute("INSERT IGNORE INTO `feat` (name, repeatable) VALUES (%s, 0)", (name,))
    cursor.execute("SELECT id FROM `feat` WHERE name = %s", (name,))
    return cursor.fetchone()[0]


def ensure_tool(cursor, name: str) -> int:
    cursor.execute("INSERT IGNORE INTO `tool` (name) VALUES (%s)", (name,))
    cursor.execute("SELECT id FROM `tool` WHERE name = %s", (name,))
    return cursor.fetchone()[0]


# ── Seeder ─────────────────────────────────────────────────────────────────────
def seed_backgrounds(cursor, backgrounds: list):
    print("\n── Backgrounds ──────────────────────────────────────")

    ability_map  = load_abilities(cursor)
    skill_map = load_skills(cursor)

    for b in backgrounds:
        name = (b.get("name") or "").strip()
        desc = (b.get("desc") or "").strip()
        if not name:
            continue

        # Insertar background
        cursor.execute(
            "INSERT INTO `background` (name, description) VALUES (%s, %s) "
            "ON DUPLICATE KEY UPDATE description = VALUES(description)",
            (name, desc),
        )
        cursor.execute("SELECT id FROM `background` WHERE name = %s", (name,))
        bg_id = cursor.fetchone()[0]
        print(f"\n   v {name} (id={bg_id})")

        for benefit in b.get("benefits") or []:
            btype = benefit.get("type")
            bdesc = (benefit.get("desc") or "").strip()

            # ── Stats ──────────────────────────────────────────
            if btype == "ability_score":
                for ability_name in parse_list(bdesc):
                    ability_id = ability_map.get(ability_name.lower())
                    if ability_id:
                        cursor.execute(
                            "INSERT IGNORE INTO `background_ability` (background_id, ability_id) VALUES (%s, %s)",
                            (bg_id, ability_id),
                        )
                        print(f"     ability: {ability_name}")
                    else:
                        print(f"     ? ability '{ability_name}' no encontrado")

            # ── Skills ─────────────────────────────────────────
            elif btype == "skill_proficiency":
                for skill_name in parse_list(bdesc):
                    skill_id = skill_map.get(skill_name.lower())
                    if skill_id:
                        cursor.execute(
                            "INSERT IGNORE INTO `background_skill` (background_id, skill_id) VALUES (%s, %s)",
                            (bg_id, skill_id),
                        )
                        print(f"     skill: {skill_name}")
                    else:
                        print(f"     ? skill '{skill_name}' no encontrada")

            # ── Feat ───────────────────────────────────────────
            elif btype == "feat":
                feat_name = clean_feat_name(bdesc.strip())
                if feat_name:
                    feat_id = ensure_feat(cursor, feat_name)
                    cursor.execute(
                        "INSERT IGNORE INTO `background_feat` (background_id, feat_id) VALUES (%s, %s)",
                        (bg_id, feat_id),
                    )
                    print(f"     feat: {feat_name}")

            # ── Tool ───────────────────────────────────────────
            elif btype == "tool_proficiency":
                tool_name = bdesc.strip()
                if tool_name:
                    tool_id = ensure_tool(cursor, tool_name)
                    cursor.execute(
                        "INSERT IGNORE INTO `background_tool` (background_id, tool_id) VALUES (%s, %s)",
                        (bg_id, tool_id),
                    )
                    print(f"     tool: {tool_name}")

            # equipment se ignora (texto libre)

    print(f"\n   -> {len(backgrounds)} backgrounds procesados")


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching backgrounds from open5e API...")
    backgrounds = fetch_all(API_URL)
    print(f"  -> {len(backgrounds)} backgrounds recibidos")

    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        migrate(cursor)
        conn.commit()

        seed_backgrounds(cursor, backgrounds)
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