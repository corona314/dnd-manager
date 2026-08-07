"""
seed_feature_choices.py
-----------------------
    feature        → cada opción individual
    feature_choice → relación padre-hijo
"""

from pyexpat import features
import re
import requests
import mysql.connector

DB_CONFIG = {
    "host":     "127.0.0.1",
    "database": "DnDB",
    "user":     "root",
    "password": "dev",
}

# Clases que tienen features de tipo OPTION_LIST
CLASS_URLS = [
    "https://api.open5e.com/v2/classes/?name__contains=warlock&document__key__in=srd-2024",
    "https://api.open5e.com/v2/classes/?name__contains=sorcerer&document__key__in=srd-2024",
]

# Mapa de feature padre por clase
PARENT_FEATURE_MAP = {
    "Eldritch Invocation Options": "Eldritch Invocations",
    "Metamagic Options":           "Metamagic",
}

FEATURE_TYPE_CLASS_ID = 2  # feature_type.id donde name = 'class'


def fetch(url: str) -> dict:
    resp = requests.get(url, timeout=15)
    resp.raise_for_status()
    return resp.json()


def parse_options(desc: str) -> list[dict]:
    """
    Parsea un bloque markdown con ### Nombre\n\n*Prerequisite: ...*\n\nDesc
    Devuelve lista de {name, prerequisite, description}
    """
    options = []
    # Partir por ### 
    sections = re.split(r'\n###\s+', desc)
    
    for section in sections[1:]:  # saltar el primero (intro)
        lines = section.strip().split('\n')
        name = lines[0].strip()
        rest = '\n'.join(lines[1:]).strip()
        
        # Extraer prerequisite si existe
        prereq_match = re.match(r'\*Prerequisite:\s*([^*]+)\*', rest)
        prerequisite = prereq_match.group(1).strip() if prereq_match else None
        
        # Limpiar descripción
        desc_clean = re.sub(r'\*Prerequisite:[^*]+\*\n*', '', rest).strip()
        
        options.append({
            "name":          name,
            "prerequisite":  prerequisite,
            "description":   desc_clean,
        })
    
    return options


def ensure_feature(cursor, name: str, desc: str, cache: dict) -> int:
    if name in cache:
        return cache[name]
    cursor.execute(
        "INSERT IGNORE INTO feature (name, description, feature_type_id) VALUES (%s, %s, %s)",
        (name[:45], desc, FEATURE_TYPE_CLASS_ID)
    )
    cursor.execute("SELECT id FROM feature WHERE name = %s", (name[:45],))
    row = cursor.fetchone()
    cache[name] = row[0]
    return row[0]


def parse_level_req(prerequisite: str | None) -> int | None:
    if not prerequisite:
        return None
    m = re.search(r'Level\s+(\d+)\+', prerequisite, re.IGNORECASE)
    return int(m.group(1)) if m else None


def main():
    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        # Cargar cache de features existentes
        cursor.execute("SELECT id, name FROM feature")
        cache = {row[1]: row[0] for row in cursor.fetchall()}

        for url in CLASS_URLS:
            data = fetch(url)
            features = data["results"][0]["features"]

            for f in features:
                ftype = f.get("feature_type", "")
                name  = f.get("name", "").strip()
                desc  = f.get("desc", "").strip()

                if ftype != "CLASS_FEATURE_OPTION_LIST":
                    continue

                print(f"\n── {name} ───────────────────────────────────")

                # Buscar feature padre
                parent_name = PARENT_FEATURE_MAP.get(name)
                if not parent_name:
                    print(f"   ! Sin padre mapeado para '{name}', saltando")
                    continue

                parent_id = cache.get(parent_name)
                if not parent_id:
                    print(f"   ! Feature padre '{parent_name}' no encontrada en DB")
                    continue

                # Parsear opciones del markdown
                options = parse_options(desc)
                print(f"   -> {len(options)} opciones encontradas")

                for opt in options:
                    choice_id = ensure_feature(
                        cursor, opt["name"], opt["description"], cache
                    )
                    level_req = parse_level_req(opt["prerequisite"])

                    cursor.execute(
                        """
                        INSERT INTO feature_choice 
                            (feature_id, choice_id, level, prerequisite)
                        VALUES (%s, %s, %s, %s)
                        ON DUPLICATE KEY UPDATE
                            level        = VALUES(level),
                            prerequisite = VALUES(prerequisite)
                        """,
                        (parent_id, choice_id, level_req, opt["prerequisite"])
                    )
                    print(f"   v {opt['name']}" + (f" [lvl {level_req}+]" if level_req else ""))

        conn.commit()
        print("\nSeed completado.")

    except Exception as e:
        conn.rollback()
        print(f"Error: {e}")
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()