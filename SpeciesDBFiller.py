"""
seed_species.py
---------------
Pobla las siguientes tablas a partir de la API open5e v2 (SRD 2024):

    species         → especies (subspecies_of == null)
    feature         → racial traits (tipo racial, feature_type.id = 1)
    species_feature → relación especie-feature

Las subespecies se saltan (no hay tabla para ellas aún).

Uso:
    pip install requests mysql-connector-python
    python seed_species.py

Ajusta DB_CONFIG con tus credenciales.
"""

import re
import requests
import mysql.connector

# ── Configuración ──────────────────────────────────────────────────────────────
DB_CONFIG = {
    "host": "127.0.0.1",
    "database": "DnDB",
    "user": "root",
    "password": "dev",
}

API_URL = "https://api.open5e.com/v2/species/?document__key__in=srd-2024&limit=50"

# feature_type.id según tu dump: 1 = 'racial'
FEATURE_TYPE_RACIAL_ID = 1

# Traits que se usan solo para rellenar columnas de species, no como features
SKIP_TRAIT_TYPES = {"SIZE", "SPEED"}


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


def parse_speed(desc: str) -> int:
    """Extrae el primer número de un string como '30 feet' o '25 feet'."""
    match = re.search(r"\d+", desc or "")
    return int(match.group()) if match else 30


def parse_size(desc: str) -> str:
    """
    Extrae la categoría de tamaño:
    'Medium (about 5-7 feet tall)' -> 'Medium'
    'Small (about 3-4 feet tall)'  -> 'Small'
    """
    match = re.match(r"(Tiny|Small|Medium|Large|Huge|Gargantuan)", desc or "", re.IGNORECASE)
    return match.group(1).capitalize() if match else "Medium"


def upsert_feature(cursor, name: str, desc: str, feature_type_id: int) -> int | None:
    """Inserta o actualiza un feature y devuelve su id."""
    if len(name) > 45:
        name = name[:45]
    cursor.execute(
        """
        INSERT INTO `feature` (name, description, type)
        VALUES (%s, %s, %s)
        ON DUPLICATE KEY UPDATE description = VALUES(description)
        """,
        (name, desc, feature_type_id),
    )
    cursor.execute("SELECT id FROM `feature` WHERE name = %s", (name,))
    row = cursor.fetchone()
    return row[0] if row else None


# ── Seeders ────────────────────────────────────────────────────────────────────
def seed_species(cursor, entries: list) -> dict:
    """
    Inserta o actualiza especies principales.
    Devuelve {species_name: species_id}.
    """
    print("\n── Species ──────────────────────────────────────────")
    id_map = {}

    for e in entries:
        if e.get("subspecies_of") is not None:
            continue

        name = (e.get("name") or "").strip()
        desc = (e.get("desc") or "").strip()
        if not name:
            continue

        traits = e.get("traits") or []

        # Extraer speed y size de los traits especiales
        walk_speed = 30
        size = "Medium"
        for t in traits:
            if t.get("type") == "SPEED":
                walk_speed = parse_speed(t.get("desc", ""))
            elif t.get("type") == "SIZE":
                size = parse_size(t.get("desc", ""))

        cursor.execute(
            """
            INSERT INTO `species` (name, size, walk_speed, description, fly_speed)
            VALUES (%s, %s, %s, %s, 0)
            ON DUPLICATE KEY UPDATE
                size        = VALUES(size),
                walk_speed  = VALUES(walk_speed),
                description = VALUES(description)
            """,
            (name, size, walk_speed, desc),
        )
        cursor.execute("SELECT id FROM `species` WHERE name = %s", (name,))
        row = cursor.fetchone()
        if row:
            id_map[name] = row[0]
            print(f"   v {name} — {size}, {walk_speed}ft (id={row[0]})")

    return id_map


def seed_species_features(cursor, entries: list, species_id_map: dict):
    """
    Por cada especie, inserta sus traits como features raciales
    y crea la relación en species_feature.
    """
    print("\n── Species features ─────────────────────────────────")

    for e in entries:
        if e.get("subspecies_of") is not None:
            continue

        species_name = (e.get("name") or "").strip()
        species_id = species_id_map.get(species_name)
        if not species_id:
            print(f"   ? especie '{species_name}' no encontrada en BD")
            continue

        for trait in e.get("traits") or []:
            # Size y Speed ya están en columnas de species
            if trait.get("type") in SKIP_TRAIT_TYPES:
                continue

            trait_name = (trait.get("name") or "").strip()
            trait_desc = (trait.get("desc") or "").strip()
            if not trait_name:
                continue

            feature_id = upsert_feature(cursor, trait_name, trait_desc, FEATURE_TYPE_RACIAL_ID)
            if not feature_id:
                print(f"      ? no se pudo obtener id para trait '{trait_name}'")
                continue

            cursor.execute(
                "INSERT IGNORE INTO `species_feature` (species_id, feature_id) VALUES (%s, %s)",
                (species_id, feature_id),
            )
            action = "v" if cursor.rowcount else "."
            print(f"   {action} [{species_name}] {trait_name}")


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching species from open5e API...")
    entries = fetch_all(API_URL)
    main_species = [e for e in entries if e.get("subspecies_of") is None]
    subspecies   = [e for e in entries if e.get("subspecies_of") is not None]
    print(f"  -> {len(main_species)} especies, {len(subspecies)} subespecies (se saltaran)")

    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        species_id_map = seed_species(cursor, entries)
        seed_species_features(cursor, entries, species_id_map)
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