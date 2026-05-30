"""
seed_classes.py
---------------
Pobla las siguientes tablas a partir de la API open5e v2 (SRD 2024):

    class               → clases (subclass_of == null)
    subclass            → subclases (subclass_of != null)
    feature             → features de clase y subclase (CLASS_LEVEL_FEATURE)
    class_feature       → relación clase-feature con nivel
    subclass_feature    → relación subclase-feature con nivel
    class_saving_throw  → saving throws por clase

Uso:
    pip install requests mysql-connector-python
    python seed_classes.py

Ajusta DB_CONFIG con tus credenciales.
"""

import requests
import mysql.connector

# ── Configuración ──────────────────────────────────────────────────────────────
DB_CONFIG = {
    "host": "127.0.0.1",
    "database": "DnDB",
    "user": "root",
    "password": "dev",
}

API_BASE = "https://api.open5e.com/v2/classes/?document__key__in=srd-2024&limit=50"

SAVING_THROW_MAP = {
    "Strength":     "STR",
    "Dexterity":    "DEX",
    "Constitution": "CON",
    "Intelligence": "INT",
    "Wisdom":       "WIS",
    "Charisma":     "CHA",
}

FEATURE_TYPES_TO_INSERT = {"CLASS_LEVEL_FEATURE"}

# feature_type.id según tu dump: 2='class', 3='subclass'
FEATURE_TYPE_CLASS_ID    = 2
FEATURE_TYPE_SUBCLASS_ID = 3


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


def name_from_api(entry: dict) -> str:
    """Usa el campo 'name' directo de la API (ya viene bien formateado)."""
    return (entry.get("name") or "").strip()


def upsert_feature(cursor, feat_name: str, feat_desc: str, feature_type_id: int) -> int | None:
    """Inserta feature si no existe y devuelve su id."""
    if len(feat_name) > 45:
        feat_name = feat_name[:45]
    cursor.execute(
        "INSERT IGNORE INTO `feature` (name, description, type) VALUES (%s, %s, %s)",
        (feat_name, feat_desc, feature_type_id),
    )
    cursor.execute("SELECT id FROM `feature` WHERE name = %s", (feat_name,))
    row = cursor.fetchone()
    return row[0] if row else None


# ── Seeders ────────────────────────────────────────────────────────────────────
def seed_classes(cursor, entries: list) -> dict:
    """Inserta solo clases (subclass_of == null). Devuelve {name: id}."""
    print("\n── Classes ──────────────────────────────────────────")
    id_map = {}
    for e in entries:
        if e.get("subclass_of") is not None:
            continue
        name = name_from_api(e)
        if not name:
            continue
        cursor.execute("INSERT IGNORE INTO `class` (name) VALUES (%s)", (name,))
        cursor.execute("SELECT id FROM `class` WHERE name = %s", (name,))
        row = cursor.fetchone()
        if row:
            id_map[name] = row[0]
            status = "✓" if cursor.rowcount else "·"
            print(f"   {status} {name} (id={row[0]})")
    return id_map


def seed_subclasses(cursor, entries: list, class_id_map: dict) -> dict:
    """Inserta subclases y devuelve {subclass_name: subclass_id}."""
    print("\n── Subclasses ───────────────────────────────────────")
    id_map = {}
    for e in entries:
        subclass_of = e.get("subclass_of")
        if subclass_of is None:
            continue
        name = name_from_api(e)
        parent_name = (subclass_of.get("name") or "").strip()
        class_id = class_id_map.get(parent_name)
        if not class_id:
            print(f"   ⚠ clase padre '{parent_name}' no encontrada, saltando '{name}'")
            continue
        cursor.execute(
            "INSERT IGNORE INTO `subclass` (name, class_id) VALUES (%s, %s)",
            (name, class_id),
        )
        cursor.execute("SELECT id FROM `subclass` WHERE name = %s", (name,))
        row = cursor.fetchone()
        if row:
            id_map[name] = row[0]
            status = "✓" if cursor.rowcount else "·"
            print(f"   {status} {name} → {parent_name} (id={row[0]})")
    return id_map


def seed_class_features(cursor, entries: list, class_id_map: dict):
    """Inserta features de clase y sus relaciones en class_feature."""
    print("\n── Class features ───────────────────────────────────")
    for e in entries:
        if e.get("subclass_of") is not None:
            continue
        class_name = name_from_api(e)
        class_id = class_id_map.get(class_name)
        if not class_id:
            continue
        for feat in e.get("features", []):
            if feat.get("feature_type") not in FEATURE_TYPES_TO_INSERT:
                continue
            feat_name = (feat.get("name") or "").strip()
            feat_desc = feat.get("desc") or ""
            gained_at = feat.get("gained_at") or []
            if not feat_name or not gained_at:
                continue
            levels = [g["level"] for g in gained_at if g.get("level") is not None]
            if not levels:
                continue
            min_level = min(levels)

            feature_id = upsert_feature(cursor, feat_name, feat_desc, FEATURE_TYPE_CLASS_ID)
            if not feature_id:
                print(f"      ⚠ no se pudo obtener id para '{feat_name}'")
                continue

            cursor.execute(
                "INSERT IGNORE INTO `class_feature` (class_id, feature_id, level) VALUES (%s, %s, %s)",
                (class_id, feature_id, min_level),
            )
            action = "✓" if cursor.rowcount else "·"
            print(f"   {action} [{class_name}] lv{min_level:>2} — {feat_name}")


def seed_subclass_features(cursor, entries: list, subclass_id_map: dict):
    """Inserta features de subclase y sus relaciones en subclass_feature."""
    print("\n── Subclass features ────────────────────────────────")
    for e in entries:
        if e.get("subclass_of") is None:
            continue
        subclass_name = name_from_api(e)
        subclass_id = subclass_id_map.get(subclass_name)
        if not subclass_id:
            continue
        for feat in e.get("features", []):
            if feat.get("feature_type") not in FEATURE_TYPES_TO_INSERT:
                continue
            feat_name = (feat.get("name") or "").strip()
            feat_desc = feat.get("desc") or ""
            gained_at = feat.get("gained_at") or []
            if not feat_name or not gained_at:
                continue
            levels = [g["level"] for g in gained_at if g.get("level") is not None]
            if not levels:
                continue
            min_level = min(levels)

            feature_id = upsert_feature(cursor, feat_name, feat_desc, FEATURE_TYPE_SUBCLASS_ID)
            if not feature_id:
                print(f"      ⚠ no se pudo obtener id para '{feat_name}'")
                continue

            cursor.execute(
                "INSERT IGNORE INTO `subclass_feature` (subclass_id, feature_id, level) VALUES (%s, %s, %s)",
                (subclass_id, feature_id, min_level),
            )
            action = "✓" if cursor.rowcount else "·"
            print(f"   {action} [{subclass_name}] lv{min_level:>2} — {feat_name}")


def seed_class_saving_throws(cursor, entries: list, class_id_map: dict):
    """Inserta saving throws solo para clases (no subclases)."""
    print("\n── Class saving throws ──────────────────────────────")
    cursor.execute("SELECT id, code FROM stat")
    stat_map = {row[1]: row[0] for row in cursor.fetchall()}

    for e in entries:
        if e.get("subclass_of") is not None:
            continue
        class_name = name_from_api(e)
        class_id = class_id_map.get(class_name)
        if not class_id:
            continue
        for st in e.get("saving_throws", []):
            stat_name = st.get("name", "")
            stat_code = SAVING_THROW_MAP.get(stat_name)
            stat_id = stat_map.get(stat_code) if stat_code else None
            if not stat_id:
                print(f"   ⚠ stat '{stat_name}' no encontrado")
                continue
            cursor.execute(
                "INSERT IGNORE INTO `class_saving_throw` (class_id, stat_id) VALUES (%s, %s)",
                (class_id, stat_id),
            )
            action = "✓" if cursor.rowcount else "·"
            print(f"   {action} [{class_name}] {stat_name} ({stat_code})")


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    print("Fetching classes & subclasses from open5e API...")
    entries = fetch_all(API_BASE)
    classes    = [e for e in entries if e.get("subclass_of") is None]
    subclasses = [e for e in entries if e.get("subclass_of") is not None]
    print(f"  → {len(classes)} clases, {len(subclasses)} subclases")

    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        class_id_map    = seed_classes(cursor, entries)
        subclass_id_map = seed_subclasses(cursor, entries, class_id_map)
        seed_class_features(cursor, entries, class_id_map)
        seed_subclass_features(cursor, entries, subclass_id_map)
        seed_class_saving_throws(cursor, entries, class_id_map)
        conn.commit()
        print("\n✅ Seed completado y commiteado.")
    except Exception as e:
        conn.rollback()
        print(f"\n❌ Error — rollback: {e}")
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()