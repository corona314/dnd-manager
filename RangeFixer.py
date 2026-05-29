"""
seed_items.py
-------------
Pobla las siguientes tablas a partir de /v2/items/ (SRD 2024):

    item                   → fila base
    weapon                 → stats del arma
    armor                  → stats de armadura
    ...

Incluye FIX de range_normal / range_long ya insertados a 0.
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

ITEMS_URL = "https://api.open5e.com/v2/items/?document__key__in=srd-2024&limit=100"


# ── Fetch API ──────────────────────────────────────────────────────────────────
def fetch_all(url: str) -> list:
    results = []
    while url:
        resp = requests.get(url, timeout=15)
        resp.raise_for_status()
        data = resp.json()
        results.extend(data.get("results", []))
        url = data.get("next")
    return results


def parse_range_from_properties(properties: list):
    """
    Extrae range_normal / range_long desde Ammunition detail:
    'Range 100/400; Bolt' → (100, 400)
    """

    for p in properties or []:
        prop = p.get("property") or {}
        if (prop.get("name") or "").lower() != "ammunition":
            continue

        detail = p.get("detail") or ""
        m = re.search(r"range\s*(\d+)\s*/\s*(\d+)", detail.lower())

        if m:
            return int(m.group(1)), int(m.group(2))

    return 0, 0

# ── FIX RANGES ────────────────────────────────────────────────────────────────
def fix_weapon_ranges(cursor, items: list):
    print("\n── Fix weapon ranges ───────────────────────────────")

    updated = 0

    for raw in items:
        weapon = raw.get("weapon")
        if not weapon:
            continue

        name = raw.get("name")
        if not name:
            continue

        cursor.execute("SELECT id FROM item WHERE name = %s", (name,))
        row = cursor.fetchone()
        if not row:
            continue

        item_id = row[0]

        range_normal, range_long = parse_range_from_properties(
            weapon.get("properties")
        )

        cursor.execute("""
            UPDATE weapon
            SET range_normal = %s,
                range_long = %s
            WHERE item_id = %s
        """, (range_normal, range_long, item_id))

        updated += 1
        print(f"   ✔ {name}: {range_normal}/{range_long}")

    print(f"\n   -> {updated} weapons actualizadas")

# ── MAIN ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching items...")

    items = fetch_all(ITEMS_URL)
    print(f"  -> {len(items)} items recibidos")

    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        # aquí asumo que el seed YA lo ejecutaste antes
        # y solo quieres reparar datos

        fix_weapon_ranges(cursor, items)

        conn.commit()
        print("\nSeed fix completado y commiteado.")

    except Exception as e:
        conn.rollback()
        print(f"\nError — rollback: {e}")
        raise

    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()