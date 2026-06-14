"""
seed_feats.py
-------------
Pobla la tabla `feat` a partir de /v2/feats/ (SRD 2024).

    feat → name, description, prerequisite, repeatable, feat_category

Los benefits se concatenan en description. bonus_feat_ability queda
para poblar manualmente (los benefits son texto libre en la API).

Ajusta DB_CONFIG con tus credenciales.
"""

import requests
import mysql.connector

DB_CONFIG = {
    "host": "127.0.0.1",
    "database": "DnDB",
    "user": "root",
    "password": "dev",
}

API_URL = "https://api.open5e.com/v2/feats/?document__key__in=srd-2024&limit=100"

REPEATABLE_HINT = "take this feat more than once"


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


def build_description(desc: str, benefits: list) -> str:
    """
    Combina el desc general con los bullets de benefits.
    Si desc esta vacio, usa solo los benefits.
    """
    parts = []
    if desc and desc.strip():
        parts.append(desc.strip())
    for b in benefits:
        b_desc = (b.get("desc") or "").strip()
        if b_desc:
            parts.append(f"• {b_desc}")
    return "\n".join(parts)


def is_repeatable(benefits: list) -> bool:
    return any(
        REPEATABLE_HINT in (b.get("desc") or "").lower()
        for b in benefits
    )


# ── Seeder ─────────────────────────────────────────────────────────────────────
def seed_feats(cursor, feats: list):
    print("\n── Feats ────────────────────────────────────────────")
    inserted = 0
    updated  = 0

    for f in feats:
        name = (f.get("name") or "").strip()
        if not name:
            continue

        benefits     = f.get("benefits") or []
        desc         = build_description(f.get("desc") or "", benefits)
        prerequisite = (f.get("prerequisite") or "").strip() or None
        feat_category = (f.get("type") or "").strip() or None
        repeatable   = 1 if is_repeatable(benefits) else 0

        cursor.execute(
            """
            INSERT INTO `feat` (name, description, prerequisite, repeatable, feat_category)
            VALUES (%s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                description   = VALUES(description),
                prerequisite  = VALUES(prerequisite),
                repeatable    = VALUES(repeatable),
                feat_category = VALUES(feat_category)
            """,
            (name, desc, prerequisite, repeatable, feat_category),
        )
        if cursor.rowcount == 1:
            inserted += 1
            print(f"   v {name} [{feat_category}]{'  (repeatable)' if repeatable else ''}")
        else:
            updated += 1
            print(f"   . {name} (actualizado)")

    print(f"\n   -> {inserted} insertados, {updated} actualizados de {len(feats)} feats")


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching feats from open5e API...")
    feats = fetch_all(API_URL)
    print(f"  -> {len(feats)} feats recibidos")

    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        seed_feats(cursor, feats)
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