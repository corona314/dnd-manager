"""
seed_background_starting_items_money.py
----------------------------------------
"""

import mysql.connector

DB_CONFIG = {
    "host":     "127.0.0.1",
    "database": "DnDB",
    "user":     "root",
    "password": "dev",
}

BACKGROUND_ITEMS = {
    "Acolyte": [
        {"item": "Calligrapher's Supplies",      "qty": 1, "group": "A", "optional": False},
        {"item": "Book",      "qty": 1, "group": "A", "optional": False},
        {"item": "Holy Symbol",      "qty": 1, "group": "A", "optional": False},
        {"item": "Parchment",             "qty": 10, "group": "A", "optional": False},
        {"item": "Robe",   "qty": 1, "group": "A", "optional": False},
    ],
    "Criminal": [
            {"item": "Dagger",      "qty": 2, "group": "A", "optional": False},
            {"item": "Thieves' Tools",      "qty": 1, "group": "A", "optional": False},
            {"item": "Crowbar",      "qty": 1, "group": "A", "optional": False},
            {"item": "Pouch",             "qty": 2, "group": "A", "optional": False},
            {"item": "Clothes, Traveler's",   "qty": 1, "group": "A", "optional": False},
    ],
    "Sage": [
            {"item": "Quarterstaff",      "qty": 1, "group": "A", "optional": False},
            {"item": "Calligrapher's Supplies",      "qty": 1, "group": "A", "optional": False},
            {"item": "Book",      "qty": 1, "group": "A", "optional": False},
            {"item": "Parchment",             "qty": 8, "group": "A", "optional": False},
            {"item": "Robe",   "qty": 1, "group": "A", "optional": False},
    ],
    "Soldier": [
            {"item": "Spear",      "qty": 1, "group": "A", "optional": False},
            {"item": "Shortbow",      "qty": 1, "group": "A", "optional": False},
            {"item": "Arrows (20)",      "qty": 1, "group": "A", "optional": False},
            {"item": "Gaming Set, Dice",             "qty": 1, "group": "A", "optional": True},
            {"item": "Gaming Set, Dragonchess",             "qty": 1, "group": "A", "optional": True},
            {"item": "Gaming Set, Playing Cards",             "qty": 1, "group": "A", "optional": True},
            {"item": "Gaming Set, Three-Dragon Ante",             "qty": 1, "group": "A", "optional": True},
            {"item": "Healer's Kit",             "qty": 1, "group": "A", "optional": False},
            {"item": "Quiver",             "qty": 1, "group": "A", "optional": False},
            {"item": "Clothes, Traveler's",   "qty": 1, "group": "A", "optional": False},
        ],
}

BACKGROUND_STARTING_MONEY = {
    "Acolyte": [
        {"group": "A", "amount": 800},
        {"group": "B", "amount": 5000},
    ],
    "Criminal": [
            {"group": "A", "amount": 1600},
            {"group": "B", "amount": 5000},
    ],
    "Sage": [
            {"group": "A", "amount": 800},
            {"group": "B", "amount": 5000},
    ],
    "Soldier": [
            {"group": "A", "amount": 1400},
            {"group": "B", "amount": 5000},
    ],
}


def main():
    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT id, name FROM `background`")
        background_map = {row[1]: row[0] for row in cursor.fetchall()}

        cursor.execute("SELECT id, name FROM `item`")
        item_map = {row[1]: row[0] for row in cursor.fetchall()}

        # ── background_item ───────────────────────────────────
        print("\n── Background Items ─────────────────────────────────")
        inserted = 0
        skipped  = 0

        for bg_name, items in BACKGROUND_ITEMS.items():
            bg_id = background_map.get(bg_name)
            if not bg_id:
                print(f"! Background '{bg_name}' no encontrado")
                skipped += 1
                continue

            for entry in items:
                item_id = item_map.get(entry["item"])
                if not item_id:
                    print(f"! Item '{entry['item']}' no encontrado para {bg_name}")
                    skipped += 1
                    continue

                cursor.execute(
                    """
                    INSERT INTO background_item (background_id, item_id, quantity, option_group, optional)
                    VALUES (%s, %s, %s, %s, %s)
                    ON DUPLICATE KEY UPDATE
                        quantity     = VALUES(quantity),
                        option_group = VALUES(option_group),
                        optional     = VALUES(optional)
                    """,
                    (bg_id, item_id, entry["qty"],
                     entry["group"], 1 if entry["optional"] else 0)
                )
                inserted += 1
                print(f"   v {bg_name} -> {entry['item']} x{entry['qty']} [group={entry['group']}, optional={entry['optional']}]")

        print(f"\n   -> {inserted} insertados, {skipped} saltados")

        # ── background_starting_money ─────────────────────────
        print("\n── Background Starting Money ────────────────────────")
        inserted = 0
        skipped  = 0

        for bg_name, options in BACKGROUND_STARTING_MONEY.items():
            bg_id = background_map.get(bg_name)
            if not bg_id:
                print(f"! Background '{bg_name}' no encontrado")
                skipped += 1
                continue

            for entry in options:
                cursor.execute(
                    """
                    INSERT INTO background_starting_money (background_id, option_group, amount)
                    VALUES (%s, %s, %s)
                    ON DUPLICATE KEY UPDATE amount = VALUES(amount)
                    """,
                    (bg_id, entry["group"], entry["amount"])
                )
                inserted += 1
                print(f"   v {bg_name} [{entry['group']}] -> {entry['amount']} cp ({entry['amount'] // 100} GP)")

        print(f"\n   -> {inserted} insertados, {skipped} saltados")

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