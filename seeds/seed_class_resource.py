"""
seed_class_resources.py
------------------------
Pobla la siguiente tabla a partir de la API open5e v2 (SRD 2024):

    class_resource   → recursos por clase y nivel
                        (Cantrips Known, Eldritch Invocations, Rages,
                        Sorcery Points, Ki Points, Sneak Attack, etc.)

IMPORTANTE: `value` se guarda como STRING, no como int, porque hay recursos
con notación de dados (p.ej. Sneak Attack = "1d6", "2d6"...). Antes de correr
este script, actualiza la columna en MySQL:

    ALTER TABLE class_resource MODIFY COLUMN value VARCHAR(20) NOT NULL;

Se obtienen a partir de las features de tipo CLASS_TABLE_DATA que ya vienen
embebidas en el listado de /v2/classes/ (mismo endpoint que seed_classes.py).
Se excluyen explícitamente PROFICIENCY_BONUS y SPELL_SLOTS (no son "recursos"
de clase en este sentido, y spell slots no tiene tabla propia todavía).

Requiere que la tabla `class` ya esté poblada (ejecutar seed_classes.py antes).

Uso:
    pip install requests mysql-connector-python
    python seed_class_resources.py

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

# Únicos feature_type que se consideran "recursos" de clase.
# PROFICIENCY_BONUS queda fuera a propósito.
INCLUDED_FEATURE_TYPES = {"CLASS_TABLE_DATA", "SPELL_SLOTS"}

# Features CLASS_TABLE_DATA que NO quieres guardar como class_resource
SKIP_FEATURE_NAMES: set[str] = {
    # "Cantrips Known",
}

# Renombra el nombre de la feature al nombre de recurso que quieras guardar
RENAME_MAP: dict[str, str] = {
}

# Correcciones manuales para features mal etiquetadas por la API.
# Clave = "key" exacto de la feature, valor = nombre de recurso correcto.
# Se procesan SIEMPRE (ignorando feature_type y name reportados), porque
# la API a veces mete los datos correctos bajo un key/type/name erróneos.
KEY_OVERRIDES: dict[str, str] = {
    # Bug open5e v2: key="srd-2024_druid_wild-shape-uses" pero viene con
    # feature_type="CLASS_LEVEL_FEATURE" y name="Cantrips Known" (pisado
    # por la feature siguiente en el markdown origen). Los valores SÍ son
    # los usos de Wild Shape, no cantrips.
    "srd-2024_druid_wild-shape-uses": "Wild Shape Uses",
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


def name_from_api(entry: dict) -> str:
    """Usa el campo 'name' directo de la API (ya viene bien formateado)."""
    return (entry.get("name") or "").strip()


def clean_value(raw_value: str | None) -> str | None:
    """Limpia column_value. Devuelve None si la celda está vacía/sin valor."""
    if raw_value is None:
        return None
    cleaned = raw_value.strip()
    if cleaned in ("", "—", "-"):
        return None
    return cleaned


def extract_resources(entry: dict, seen_names: set[str]) -> list[tuple]:
    """
    Recorre las features de una clase y devuelve tuplas
    (resource_name, level, value) listas para insertar.
    """
    rows = []
    for feature in entry.get("features", []):
        key = feature.get("key", "")
        override_name = KEY_OVERRIDES.get(key)

        if override_name is not None:
            resource_name = override_name
        else:
            feature_type = feature.get("feature_type")
            if feature_type not in INCLUDED_FEATURE_TYPES:
                continue

            raw_name = feature["name"]

            if feature_type == "SPELL_SLOTS":
                # name = "1st", "2nd"... -> "Spell Slots (1st)"
                base_name = f"Spell Slots ({raw_name})"
            else:
                base_name = raw_name

            if base_name in SKIP_FEATURE_NAMES:
                continue
            resource_name = RENAME_MAP.get(base_name, base_name)

        seen_names.add(resource_name)

        for data_entry in feature.get("data_for_class_table", []):
            level = data_entry["level"]
            value = clean_value(data_entry["column_value"])
            if value is None:
                continue
            rows.append((resource_name, level, value))

    return rows


# ── Seeder ─────────────────────────────────────────────────────────────────────
def seed_class_resources(cursor, entries: list, class_id_map: dict) -> None:
    print("\n── Class Resources ──────────────────────────────────")
    seen_names: set[str] = set()

    for e in entries:
        # Solo clases base (subclass_of == None); class_resource no tiene subclass_id
        if e.get("subclass_of") is not None:
            continue

        name = name_from_api(e)
        class_id = class_id_map.get(name)
        if not class_id:
            print(f"   ⚠ clase '{name}' no encontrada en la tabla class, saltando")
            continue

        rows = extract_resources(e, seen_names)
        if not rows:
            print(f"   · {name}: sin recursos CLASS_TABLE_DATA")
            continue

        cursor.executemany(
            """
            INSERT INTO class_resource (class_id, name, level, value)
            VALUES (%s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE value = VALUES(value)
            """,
            [(class_id, rname, level, value) for rname, level, value in rows],
        )
        print(f"   ✓ {name}: {len(rows)} filas insertadas/actualizadas (id={class_id})")

    print("\n── Nombres de recursos detectados (revisar) ────────────")
    for rname in sorted(seen_names):
        print(f"   - {rname}")


# ── Main ───────────────────────────────────────────────────────────────────────
def main():
    entries = fetch_all(API_BASE)

    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    # Reconstruir el mapa {class_name: class_id} desde la BD
    # (asume que seed_classes.py ya se ejecutó antes)
    cursor.execute("SELECT id, name FROM `class`")
    class_id_map = {name: id_ for id_, name in cursor.fetchall()}

    if not class_id_map:
        print("⚠ La tabla `class` está vacía. Ejecuta seed_classes.py primero.")
        cursor.close()
        conn.close()
        return

    try:
        seed_class_resources(cursor, entries, class_id_map)
        conn.commit()
    except mysql.connector.Error as e:
        conn.rollback()
        print(f"[ERROR] {e}")
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()