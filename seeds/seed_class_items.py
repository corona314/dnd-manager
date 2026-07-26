"""
seed_class_items.py
"""

import mysql.connector

DB_CONFIG = {
    "host":     "127.0.0.1",
    "database": "DnDB",
    "user":     "root",
    "password": "dev",
}

# Estructura: clase -> lista de items
# { "item": nombre, "qty": cantidad, "group": "A"/"B"/None, "optional": True/False }
CLASS_ITEMS = {
    "Barbarian": [
        {"item": "Greataxe",        "qty": 1, "group": "A", "optional": False},
        {"item": "Handaxe",         "qty": 4, "group": "A", "optional": False},
        {"item": "Explorer's Pack", "qty": 1, "group": "A", "optional": False},
    ],
    "Bard": [
        {"item": "Leather Armor",   "qty": 1, "group": "A", "optional": False},
        {"item": "Dagger",          "qty": 2, "group": "A", "optional": False},
        {"item": "Musical Instrument, Bagpipes",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Drum",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Dulcimer",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Flute",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Horn",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Lute",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Lyre",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Pan Flute",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Shawm",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Viol",           "qty": 1, "group": "A", "optional": True},
        {"item": "Entertainer's Pack", "qty": 1, "group": "A", "optional": False},
    ],
    "Cleric": [
        {"item": "Chain Shirt",     "qty": 1, "group": "A", "optional": False},
        {"item": "Shield",          "qty": 1, "group": "A", "optional": False},
        {"item": "Mace",            "qty": 1, "group": "A", "optional": False},
        {"item": "Holy Symbol, Amulet",         "qty": 1, "group": "A", "optional": True},
        {"item": "Holy Symbol, Emblem",         "qty": 1, "group": "A", "optional": True},
        {"item": "Holy Symbol, Reliquary",         "qty": 1, "group": "A", "optional": True},
        {"item": "Priest's Pack", "qty": 1, "group": "A", "optional": False},
    ],
    "Druid": [
        {"item": "Leather Armor",           "qty": 1, "group": "A", "optional": False},
        {"item": "Shield",          "qty": 1, "group": "A", "optional": False},
        {"item": "Druidic Focus, Wooden Staff",            "qty": 1, "group": "A", "optional": False},
        {"item": "Explorer's Pack",           "qty": 1, "group": "A", "optional": False},
        {"item": "Herbalism Kit", "qty": 1, "group": "A", "optional": False},
    ],
    "Fighter": [
        {"item": "Chain Mail",           "qty": 1, "group": "A", "optional": False},
        {"item": "Greatsword",          "qty": 1, "group": "A", "optional": False},
        {"item": "Flail",            "qty": 1, "group": "A", "optional": False},
        {"item": "Javelin",           "qty": 8, "group": "A", "optional": False},
        {"item": "Dungeoneer's Pack", "qty": 1, "group": "A", "optional": False},

        {"item": "Studded Leather Armor",           "qty": 1, "group": "B", "optional": False},
        {"item": "Scimitar",          "qty": 1, "group": "B", "optional": False},
        {"item": "Shortsword",          "qty": 1, "group": "B", "optional": False},
        {"item": "Longbow",          "qty": 1, "group": "B", "optional": False},
        {"item": "Arrows (20)",          "qty": 1, "group": "B", "optional": False},
        {"item": "Quiver",          "qty": 1, "group": "B", "optional": False},
        {"item": "Dungeoneer's Pack",          "qty": 1, "group": "B", "optional": False},

    ],
    "Monk": [
        {"item": "Spear",           "qty": 1, "group": "A", "optional": False},
        {"item": "Dagger",          "qty": 5, "group": "A", "optional": False},
        {"item": "Alchemist's Supplies",            "qty": 1, "group": "A", "optional": True},
        {"item": "Brewer's Supplies",            "qty": 1, "group": "A", "optional": True},
        {"item": "Calligrapher's Supplies",            "qty": 1, "group": "A", "optional": True},
        {"item": "Carpenter's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Cartographer's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Cobbler's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Cook's Utensils",            "qty": 1, "group": "A", "optional": True},
        {"item": "Glassblower's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Jeweler's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Leatherworker's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Mason's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Painter's Supplies",            "qty": 1, "group": "A", "optional": True},
        {"item": "Potter's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Smith's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Tinker's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Weaver's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Woodcarver's Tools",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Bagpipes",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Drum",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Dulcimer",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Flute",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Horn",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Lute",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Lyre",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Pan Flute",           "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Shawm",            "qty": 1, "group": "A", "optional": True},
        {"item": "Musical Instrument, Viol",           "qty": 1, "group": "A", "optional": True},
        {"item": "Explorer's Pack", "qty": 1, "group": "A", "optional": False},
    ],
    "Paladin": [
        {"item": "Chain Mail",           "qty": 1, "group": "A", "optional": False},
        {"item": "Shield",          "qty": 1, "group": "A", "optional": False},
        {"item": "Longsword",            "qty": 1, "group": "A", "optional": False},
        {"item": "Javelin",           "qty": 6, "group": "A", "optional": False},
        {"item": "Holy Symbol, Amulet",         "qty": 1, "group": "A", "optional": True},
        {"item": "Holy Symbol, Emblem",         "qty": 1, "group": "A", "optional": True},
        {"item": "Holy Symbol, Reliquary",         "qty": 1, "group": "A", "optional": True},
        {"item": "Priest's Pack", "qty": 1, "group": "A", "optional": False},
    ],
    "Ranger": [
        {"item": "Studded Leather Armor",           "qty": 1, "group": "A", "optional": False},
        {"item": "Scimitar",          "qty": 1, "group": "A", "optional": False},
        {"item": "Shortsword",          "qty": 1, "group": "A", "optional": False},
        {"item": "Longbow",          "qty": 1, "group": "A", "optional": False},
        {"item": "Arrows (20)",          "qty": 1, "group": "A", "optional": False},
        {"item": "Quiver",          "qty": 1, "group": "A", "optional": False},
        {"item": "Druidic Focus, Sprig of Mistletoe", "qty": 1, "group": "A", "optional": False},
        {"item": "Explorer's Pack", "qty": 1, "group": "A", "optional": False},

    ],
    "Rogue": [
        {"item": "Leather Armor",        "qty": 1, "group": "A", "optional": False},
        {"item": "Dagger",         "qty": 2, "group": "A", "optional": False},
        {"item": "Shortsword",          "qty": 1, "group": "A", "optional": False},
        {"item": "Shortbow",          "qty": 1, "group": "A", "optional": False},
        {"item": "Arrows (20)",          "qty": 1, "group": "A", "optional": False},
        {"item": "Quiver",          "qty": 1, "group": "A", "optional": False},
        {"item": "Thieves' Tools",         "qty": 2, "group": "A", "optional": False},
        {"item": "Burglar's Pack", "qty": 1, "group": "A", "optional": False},
    ],
    "Sorcerer": [
        {"item": "Spear",           "qty": 1, "group": "A", "optional": False},
        {"item": "Dagger",          "qty": 2, "group": "A", "optional": False},
        {"item": "Arcane Focus, Crystal",            "qty": 1, "group": "A", "optional": False},
        {"item": "Dungeoneer's Pack",           "qty": 1, "group": "A", "optional": False},
    ],
    "Warlock": [
        {"item": "Leather Armor",        "qty": 1, "group": "A", "optional": False},
        {"item": "Sickle",         "qty": 1, "group": "A", "optional": False},
        {"item": "Dagger", "qty": 2, "group": "A", "optional": False},
        {"item": "Arcane Focus, Orb",         "qty": 1, "group": "A", "optional": False},
        {"item": "Book", "qty": 1, "group": "A", "optional": False},
        {"item": "Scholar's Pack", "qty": 1, "group": "A", "optional": False},
    ],
    "Wizard": [
        {"item": "Dagger",           "qty": 2, "group": "A", "optional": False},
        {"item": "Arcane Focus, Staff",          "qty": 1, "group": "A", "optional": False},
        {"item": "Robe",            "qty": 1, "group": "A", "optional": False},
        {"item": "Spellbook",           "qty": 1, "group": "A", "optional": False},
        {"item": "Scholar's Pack", "qty": 1, "group": "A", "optional": False},
    ],
}


def main():
    conn   = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        # Cargar clases e items en caché
        cursor.execute("SELECT id, name FROM `class`")
        class_map = {row[1]: row[0] for row in cursor.fetchall()}

        cursor.execute("SELECT id, name FROM `item`")
        item_map = {row[1]: row[0] for row in cursor.fetchall()}

        inserted = 0
        skipped  = 0

        for class_name, items in CLASS_ITEMS.items():
            class_id = class_map.get(class_name)
            if not class_id:
                print(f"! Clase '{class_name}' no encontrada")
                continue

            for entry in items:
                item_id = item_map.get(entry["item"])
                if not item_id:
                    print(f"! Item '{entry['item']}' no encontrado para {class_name}")
                    skipped += 1
                    continue

                cursor.execute(
                    """
                    INSERT INTO class_item (class_id, item_id, quantity, option_group, optional)
                    VALUES (%s, %s, %s, %s, %s)
                    ON DUPLICATE KEY UPDATE
                        quantity     = VALUES(quantity),
                        option_group = VALUES(option_group),
                        optional     = VALUES(optional)
                    """,
                    (class_id, item_id, entry["qty"],
                     entry["group"], 1 if entry["optional"] else 0)
                )
                inserted += 1
                print(f"   v {class_name} -> {entry['item']} x{entry['qty']} [group={entry['group']}, optional={entry['optional']}]")

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