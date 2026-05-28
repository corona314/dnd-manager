"""
seed_dnd.py — Poblar tablas `class` y `species` desde Open5e API
Uso: python seed_dnd.py

Requisitos:
    pip install requests mysql-connector-python
"""

import requests
import mysql.connector

# ── Configuración ──────────────────────────────────────────────
DB_CONFIG = {
    "host":     "127.0.0.1",
    "port":     3306,
    "user":     "root",
    "password": "dev",
    "database": "DnDB",
}

# ── Helpers ────────────────────────────────────────────────────

def fetch_all(url: str) -> list:
    """Pagina automáticamente y devuelve todos los resultados."""
    results = []
    while url:
        r = requests.get(url, timeout=10)
        r.raise_for_status()
        data = r.json()
        results.extend(data.get("results", []))
        url = data.get("next")
    return results


# ── Seeders ────────────────────────────────────────────────────

def seed_classes(cursor):
    """
    Endpoint v2 — devuelve las clases del SRD con nombre y subclases.
    Tu tabla `class` solo necesita el name, así que es directo.
    """
    print("→ Fetching classes...")
    classes = fetch_all("https://api.open5e.com/v2/classes/?limit=50")

    inserted = 0
    for c in classes:

        name = c.get("key", "").split("_")[-1].replace("-", " ").title()
        if not name:
            continue
        cursor.execute(
            "INSERT IGNORE INTO `class` (name) VALUES (%s)",
            (name,)
        )
        if cursor.rowcount:
            inserted += 1
            print(f"   ✓ {name}")
        else:
            print(f"   · {name} (ya existe)")

    print(f"   Classes insertadas: {inserted}/{len(classes)}\n")


def seed_species(cursor):
    """
    Endpoint v2 con filtro srd-2024 — devuelve species/races.
    Mapeo: name, size, walk_speed, description, fly_speed.
    """
    print("→ Fetching species...")
    species_list = fetch_all(
        "https://api.open5e.com/v2/species/?document__key__in=srd-2024&limit=50"
    )

    # Fallback a v1 si v2 no devuelve nada (el endpoint puede variar)
    if not species_list:
        print("   (v2 vacío, probando v1...)")
        species_list = fetch_all(
            "https://api.open5e.com/v1/races/?limit=50"
        )

    inserted = 0
    for s in species_list:
        name        = s.get("name")
        description = s.get("desc") or s.get("description") or ""
        size        = s.get("size") or ""

        # La velocidad puede venir como int, string o dict según la versión
        speed_raw  = s.get("speed") or s.get("walk_speed") or 30
        if isinstance(speed_raw, dict):
            walk_speed = int(speed_raw.get("walk", 30) or 30)
            fly_speed  = int(speed_raw.get("fly",  0)  or 0)
        else:
            walk_speed = int(speed_raw) if speed_raw else 30
            fly_speed  = 0

        if not name:
            continue

        cursor.execute("""
            INSERT IGNORE INTO species (name, size, walk_speed, description, fly_speed)
            VALUES (%s, %s, %s, %s, %s)
        """, (name, size, walk_speed, description, fly_speed))

        if cursor.rowcount:
            inserted += 1
            print(f"   ✓ {name}")
        else:
            print(f"   · {name} (ya existe)")

    print(f"   Species insertadas: {inserted}/{len(species_list)}\n")


# ── Main ───────────────────────────────────────────────────────

def main():
    print("Conectando a MySQL...")
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        seed_classes(cursor)
        seed_species(cursor)
        conn.commit()
        print("✅ Seed completado.")
    except Exception as e:
        conn.rollback()
        print(f"❌ Error: {e}")
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()