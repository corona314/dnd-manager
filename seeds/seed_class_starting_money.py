"""
seed_class_starting_money.py
-----------------------------
"""

import mysql.connector

DB_CONFIG = {
    "host":     "127.0.0.1",
    "database": "DnDB",
    "user":     "root",
    "password": "dev",
}

# Estructura: clase -> lista de opciones de dinero
# group 'A' por defecto si solo hay una opción
CLASS_STARTING_MONEY = {
    "Barbarian": [
        {"group": "A", "amount": 1500},
        {"group": "B", "amount": 7500},

    ],
    "Bard": [
        {"group": "A", "amount": 1900},
        {"group": "B", "amount": 9000},
    ],
    "Cleric": [
        {"group": "A", "amount": 700},
        {"group": "B", "amount": 1100},
    ],
    "Druid": [
        {"group": "A", "amount": 900},
        {"group": "B", "amount": 5000},
    ],
    "Fighter": [
        {"group": "A", "amount": 400},
        {"group": "B", "amount": 1100},
        {"group": "C", "amount": 15500},
    ],
    "Monk": [
        {"group": "A", "amount": 1100},
        {"group": "B", "amount": 5000},
    ],
    "Paladin": [
        {"group": "A", "amount": 900},
        {"group": "B", "amount": 15000},
    ],
    "Ranger": [
        {"group": "A", "amount": 700},
        {"group": "B", "amount": 15000},
    ],
    "Rogue": [
        {"group": "A", "amount": 800},
        {"group": "B", "amount": 10000},
    ],
    "Sorcerer": [
        {"group": "A", "amount": 2800},
        {"group": "B", "amount": 5000},
    ],
    "Warlock": [
        {"group": "A", "amount": 1500},
        {"group": "B", "amount": 10000},
    ],
    "Wizard": [
        {"group": "A", "amount": 500},
        {"group": "B", "amount": 5500},
    ],
}


def main():
    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT id, name FROM `class`")
        class_map = {row[1]: row[0] for row in cursor.fetchall()}

        inserted = 0
        skipped  = 0

        for class_name, options in CLASS_STARTING_MONEY.items():
            class_id = class_map.get(class_name)
            if not class_id:
                print(f"! Clase '{class_name}' no encontrada")
                skipped += 1
                continue

            for entry in options:
                cursor.execute(
                    """
                    INSERT INTO class_starting_money (class_id, option_group, amount)
                    VALUES (%s, %s, %s)
                    ON DUPLICATE KEY UPDATE amount = VALUES(amount)
                    """,
                    (class_id, entry["group"], entry["amount"])
                )
                inserted += 1
                print(f"   v {class_name} [{entry['group']}] -> {entry['amount']} cp ({entry['amount'] // 100} GP)")

        conn.commit()
        print(f"\n-> {inserted} insertados, {skipped} saltados")

    except Exception as e:
        conn.rollback()
        print(f"Error: {e}")
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()