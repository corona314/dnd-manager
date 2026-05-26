-- MySQL dump 10.13  Distrib 8.4.9, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: DnDB
-- ------------------------------------------------------
-- Server version	8.4.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `armor`
--

DROP TABLE IF EXISTS `armor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `armor` (
  `item_id` int NOT NULL,
  `ac_base` int NOT NULL,
  `ac_max` int NOT NULL,
  `str_min` int DEFAULT '0',
  `stealth_dis` tinyint NOT NULL DEFAULT '0',
  `armor_type_id` int NOT NULL,
  PRIMARY KEY (`item_id`),
  KEY `armor_armor_type` (`armor_type_id`),
  CONSTRAINT `armor_armor_type` FOREIGN KEY (`armor_type_id`) REFERENCES `armor_type` (`id`),
  CONSTRAINT `armor_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `armor`
--

LOCK TABLES `armor` WRITE;
/*!40000 ALTER TABLE `armor` DISABLE KEYS */;
/*!40000 ALTER TABLE `armor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `armor_type`
--

DROP TABLE IF EXISTS `armor_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `armor_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `armor_type`
--

LOCK TABLES `armor_type` WRITE;
/*!40000 ALTER TABLE `armor_type` DISABLE KEYS */;
INSERT INTO `armor_type` VALUES (3,'heavy'),(1,'light'),(2,'medium');
/*!40000 ALTER TABLE `armor_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `background`
--

DROP TABLE IF EXISTS `background`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `background` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `background_name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `background`
--

LOCK TABLES `background` WRITE;
/*!40000 ALTER TABLE `background` DISABLE KEYS */;
/*!40000 ALTER TABLE `background` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `background_skill`
--

DROP TABLE IF EXISTS `background_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `background_skill` (
  `background_id` int NOT NULL,
  `skill_id` int NOT NULL,
  PRIMARY KEY (`background_id`,`skill_id`),
  KEY `background_skill_skill` (`skill_id`),
  CONSTRAINT `background_skill_background` FOREIGN KEY (`background_id`) REFERENCES `background` (`id`),
  CONSTRAINT `background_skill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `background_skill`
--

LOCK TABLES `background_skill` WRITE;
/*!40000 ALTER TABLE `background_skill` DISABLE KEYS */;
/*!40000 ALTER TABLE `background_skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `background_trait`
--

DROP TABLE IF EXISTS `background_trait`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `background_trait` (
  `background_id` int NOT NULL,
  `trait_id` int NOT NULL,
  PRIMARY KEY (`background_id`,`trait_id`),
  KEY `background_trait_trait` (`trait_id`),
  CONSTRAINT `background_trait_background` FOREIGN KEY (`background_id`) REFERENCES `background` (`id`),
  CONSTRAINT `background_trait_trait` FOREIGN KEY (`trait_id`) REFERENCES `trait` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `background_trait`
--

LOCK TABLES `background_trait` WRITE;
/*!40000 ALTER TABLE `background_trait` DISABLE KEYS */;
/*!40000 ALTER TABLE `background_trait` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bonus_feat_stat`
--

DROP TABLE IF EXISTS `bonus_feat_stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bonus_feat_stat` (
  `feat_id` int NOT NULL,
  `stat_id` int NOT NULL,
  `value` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`feat_id`,`stat_id`),
  KEY `feat_stat_bonus_stat` (`stat_id`),
  CONSTRAINT `feat_stat_bonus_feat` FOREIGN KEY (`feat_id`) REFERENCES `feat` (`id`),
  CONSTRAINT `feat_stat_bonus_stat` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bonus_feat_stat`
--

LOCK TABLES `bonus_feat_stat` WRITE;
/*!40000 ALTER TABLE `bonus_feat_stat` DISABLE KEYS */;
/*!40000 ALTER TABLE `bonus_feat_stat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bonus_trait_item`
--

DROP TABLE IF EXISTS `bonus_trait_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bonus_trait_item` (
  `trait_id` int NOT NULL,
  `item_id` int NOT NULL,
  `value` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`trait_id`,`item_id`),
  KEY `bonus_trait_item_item` (`item_id`),
  CONSTRAINT `bonus_trait_item_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `bonus_trait_item_trait` FOREIGN KEY (`trait_id`) REFERENCES `trait` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bonus_trait_item`
--

LOCK TABLES `bonus_trait_item` WRITE;
/*!40000 ALTER TABLE `bonus_trait_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `bonus_trait_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bonus_trait_stat`
--

DROP TABLE IF EXISTS `bonus_trait_stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bonus_trait_stat` (
  `trait_id` int NOT NULL,
  `stat_id` int NOT NULL,
  `value` int NOT NULL,
  PRIMARY KEY (`trait_id`,`stat_id`),
  KEY `bonus_trait_stat_stat` (`stat_id`),
  CONSTRAINT `bonus_trait_stat_stat` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`),
  CONSTRAINT `bonus_trait_stat_trait` FOREIGN KEY (`trait_id`) REFERENCES `trait` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bonus_trait_stat`
--

LOCK TABLES `bonus_trait_stat` WRITE;
/*!40000 ALTER TABLE `bonus_trait_stat` DISABLE KEYS */;
/*!40000 ALTER TABLE `bonus_trait_stat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character`
--

DROP TABLE IF EXISTS `character`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `level` int NOT NULL,
  `max_hp` int DEFAULT NULL,
  `species_id` int DEFAULT NULL,
  `class_id` int DEFAULT NULL,
  `subclass_id` int DEFAULT NULL,
  `background_id` int DEFAULT NULL,
  `current_hp` int DEFAULT NULL,
  `walk_speed` int DEFAULT '0',
  `fly_speed` int DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `finalized_at` datetime DEFAULT NULL,
  `status` enum('DRAFT','FINAL') DEFAULT 'DRAFT',
  PRIMARY KEY (`id`),
  KEY `fk_character_species` (`species_id`),
  KEY `fk_character_class` (`class_id`),
  KEY `fk_character_subclass` (`subclass_id`),
  KEY `character_user` (`user_id`),
  KEY `fk_character_background` (`background_id`),
  CONSTRAINT `character_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_character_background` FOREIGN KEY (`background_id`) REFERENCES `background` (`id`),
  CONSTRAINT `fk_character_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`),
  CONSTRAINT `fk_character_species` FOREIGN KEY (`species_id`) REFERENCES `species` (`id`),
  CONSTRAINT `fk_character_subclass` FOREIGN KEY (`subclass_id`) REFERENCES `subclass` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Character info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character`
--

LOCK TABLES `character` WRITE;
/*!40000 ALTER TABLE `character` DISABLE KEYS */;
/*!40000 ALTER TABLE `character` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_feat`
--

DROP TABLE IF EXISTS `character_feat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_feat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `character_id` int NOT NULL,
  `feat_id` int NOT NULL,
  `source` varchar(30) DEFAULT NULL,
  `source_level` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `character_feat_character` (`character_id`),
  KEY `character_feat_feat` (`feat_id`),
  CONSTRAINT `character_feat_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
  CONSTRAINT `character_feat_feat` FOREIGN KEY (`feat_id`) REFERENCES `feat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_feat`
--

LOCK TABLES `character_feat` WRITE;
/*!40000 ALTER TABLE `character_feat` DISABLE KEYS */;
/*!40000 ALTER TABLE `character_feat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_item`
--

DROP TABLE IF EXISTS `character_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_item` (
  `character_id` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int DEFAULT '1',
  `equipped` tinyint DEFAULT '0',
  `attuned` tinyint DEFAULT '0',
  PRIMARY KEY (`character_id`,`item_id`),
  KEY `character_item_item` (`item_id`),
  CONSTRAINT `character_item_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
  CONSTRAINT `character_item_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_item`
--

LOCK TABLES `character_item` WRITE;
/*!40000 ALTER TABLE `character_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `character_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_resource`
--

DROP TABLE IF EXISTS `character_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_resource` (
  `id` int NOT NULL AUTO_INCREMENT,
  `character_id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `current_value` int NOT NULL,
  `max_value` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `character_id` (`character_id`),
  CONSTRAINT `character_resource_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_resource`
--

LOCK TABLES `character_resource` WRITE;
/*!40000 ALTER TABLE `character_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `character_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_saving_throw`
--

DROP TABLE IF EXISTS `character_saving_throw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_saving_throw` (
  `character_id` int NOT NULL,
  `stat_id` int NOT NULL,
  PRIMARY KEY (`character_id`,`stat_id`),
  KEY `character_saving_throw_stat` (`stat_id`),
  CONSTRAINT `character_saving_throw_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
  CONSTRAINT `character_saving_throw_stat` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_saving_throw`
--

LOCK TABLES `character_saving_throw` WRITE;
/*!40000 ALTER TABLE `character_saving_throw` DISABLE KEYS */;
/*!40000 ALTER TABLE `character_saving_throw` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_skill`
--

DROP TABLE IF EXISTS `character_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_skill` (
  `character_id` int NOT NULL,
  `skill_id` int NOT NULL,
  `proficient` tinyint(1) NOT NULL DEFAULT '0',
  `expertise` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`character_id`,`skill_id`),
  KEY `character_id` (`character_id`),
  KEY `skill_id` (`skill_id`),
  CONSTRAINT `character_skill_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
  CONSTRAINT `character_skill_ibfk_2` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_skill`
--

LOCK TABLES `character_skill` WRITE;
/*!40000 ALTER TABLE `character_skill` DISABLE KEYS */;
/*!40000 ALTER TABLE `character_skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_spell`
--

DROP TABLE IF EXISTS `character_spell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_spell` (
  `id` int NOT NULL AUTO_INCREMENT,
  `character_id` int NOT NULL,
  `spell_id` int NOT NULL,
  `prepared` tinyint(1) NOT NULL DEFAULT '0',
  `always_prepared` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `character_spell_unique` (`character_id`,`spell_id`),
  KEY `character_spell_spell` (`spell_id`),
  CONSTRAINT `character_spell_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
  CONSTRAINT `character_spell_spell` FOREIGN KEY (`spell_id`) REFERENCES `spell` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_spell`
--

LOCK TABLES `character_spell` WRITE;
/*!40000 ALTER TABLE `character_spell` DISABLE KEYS */;
/*!40000 ALTER TABLE `character_spell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_stat`
--

DROP TABLE IF EXISTS `character_stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_stat` (
  `character_id` int NOT NULL,
  `stat_id` int NOT NULL,
  `base_value` int NOT NULL,
  PRIMARY KEY (`character_id`,`stat_id`),
  KEY `character_id` (`character_id`),
  KEY `stat_id` (`stat_id`),
  CONSTRAINT `character_stat_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
  CONSTRAINT `character_stat_ibfk_2` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_stat`
--

LOCK TABLES `character_stat` WRITE;
/*!40000 ALTER TABLE `character_stat` DISABLE KEYS */;
/*!40000 ALTER TABLE `character_stat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Class Catalog';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_saving_throw`
--

DROP TABLE IF EXISTS `class_saving_throw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_saving_throw` (
  `class_id` int NOT NULL,
  `stat_id` int NOT NULL,
  PRIMARY KEY (`class_id`,`stat_id`),
  KEY `class_saving_throw_stat` (`stat_id`),
  CONSTRAINT `class_saving_throw_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`),
  CONSTRAINT `class_saving_throw_stat` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_saving_throw`
--

LOCK TABLES `class_saving_throw` WRITE;
/*!40000 ALTER TABLE `class_saving_throw` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_saving_throw` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_spell`
--

DROP TABLE IF EXISTS `class_spell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_spell` (
  `class_id` int NOT NULL,
  `spell_id` int NOT NULL,
  PRIMARY KEY (`class_id`,`spell_id`),
  KEY `class_spell_spell` (`spell_id`),
  CONSTRAINT `class_spell_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`),
  CONSTRAINT `class_spell_spell` FOREIGN KEY (`spell_id`) REFERENCES `spell` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_spell`
--

LOCK TABLES `class_spell` WRITE;
/*!40000 ALTER TABLE `class_spell` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_spell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_trait`
--

DROP TABLE IF EXISTS `class_trait`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_trait` (
  `class_id` int NOT NULL,
  `trait_id` int NOT NULL,
  `level` int NOT NULL,
  PRIMARY KEY (`class_id`,`trait_id`),
  KEY `class_trait_trait` (`trait_id`),
  CONSTRAINT `class_trait_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`),
  CONSTRAINT `class_trait_trait` FOREIGN KEY (`trait_id`) REFERENCES `trait` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_trait`
--

LOCK TABLES `class_trait` WRITE;
/*!40000 ALTER TABLE `class_trait` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_trait` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `damage_type`
--

DROP TABLE IF EXISTS `damage_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `damage_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `damage_type`
--

LOCK TABLES `damage_type` WRITE;
/*!40000 ALTER TABLE `damage_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `damage_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feat`
--

DROP TABLE IF EXISTS `feat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `description` text,
  `prerequisite` varchar(255) DEFAULT NULL,
  `repeatable` tinyint(1) NOT NULL DEFAULT '0',
  `feat_category` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `feat_name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feat`
--

LOCK TABLES `feat` WRITE;
/*!40000 ALTER TABLE `feat` DISABLE KEYS */;
/*!40000 ALTER TABLE `feat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `weight` float DEFAULT '0',
  `price` int DEFAULT '0',
  `item_type_id` int NOT NULL,
  `magic` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `item_item_type` (`item_type_id`),
  CONSTRAINT `item_item_type` FOREIGN KEY (`item_type_id`) REFERENCES `item_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_trait`
--

DROP TABLE IF EXISTS `item_trait`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_trait` (
  `item_id` int NOT NULL,
  `trait_id` int NOT NULL,
  PRIMARY KEY (`item_id`,`trait_id`),
  KEY `item_trait_trait` (`trait_id`),
  CONSTRAINT `item_trait_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `item_trait_trait` FOREIGN KEY (`trait_id`) REFERENCES `trait` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_trait`
--

LOCK TABLES `item_trait` WRITE;
/*!40000 ALTER TABLE `item_trait` DISABLE KEYS */;
/*!40000 ALTER TABLE `item_trait` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_type`
--

DROP TABLE IF EXISTS `item_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_type`
--

LOCK TABLES `item_type` WRITE;
/*!40000 ALTER TABLE `item_type` DISABLE KEYS */;
INSERT INTO `item_type` VALUES (1,'Armor'),(3,'Item'),(2,'Weapon');
/*!40000 ALTER TABLE `item_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mastery`
--

DROP TABLE IF EXISTS `mastery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mastery` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mastery`
--

LOCK TABLES `mastery` WRITE;
/*!40000 ALTER TABLE `mastery` DISABLE KEYS */;
/*!40000 ALTER TABLE `mastery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skill`
--

DROP TABLE IF EXISTS `skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skill` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `stat_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `stat_id` (`stat_id`),
  CONSTRAINT `skill_ibfk_1` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skill`
--

LOCK TABLES `skill` WRITE;
/*!40000 ALTER TABLE `skill` DISABLE KEYS */;
INSERT INTO `skill` VALUES (1,'Athletics',1),(2,'Acrobatics',2),(3,'Sleight of Hand',2),(4,'Stealth',2),(5,'Arcana',4),(6,'History',4),(7,'Investigation',4),(8,'Nature',4),(9,'Religion',4),(10,'Animal Handling',5),(11,'Insight',5),(12,'Medicine',5),(13,'Perception',5),(14,'Survival',5),(15,'Deception',6),(16,'Intimidation',6),(17,'Performance',6),(18,'Persuasion',6);
/*!40000 ALTER TABLE `skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `species`
--

DROP TABLE IF EXISTS `species`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `species` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `size` varchar(20) DEFAULT NULL,
  `walk_speed` int NOT NULL,
  `description` text,
  `fly_speed` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `species`
--

LOCK TABLES `species` WRITE;
/*!40000 ALTER TABLE `species` DISABLE KEYS */;
INSERT INTO `species` VALUES (1,'Aasimar','3',30,'Aasimar, descended from celestial beings, possess an aura of light and supernatural abilities.',0),(2,'Human','3',30,'Humans are versatile and adaptable, able to learn many skills.',0),(3,'Dragonborn','3',30,'Dragonborn, humanoids with draconic heritage, command respect with their elemental breath.',0),(4,'Dwarf','3',25,'Dwarves are sturdy and resilient, skilled in mining and forging.',0),(5,'Elf','3',30,'Elves are agile and long-lived, closely connected to magic and nature.',0),(6,'Gnome','2',25,'Gnomes are clever and curious, masters of invention and illusion.',0),(7,'Half-Elf','3',30,'Half-Elves blend elven grace and human versatility.',0),(8,'Half-Orc','3',30,'Half-Orcs are strong and fearsome, with instincts for combat and physical endurance.',0),(9,'Halfling','2',25,'Halflings are small and nimble, masters of stealth and luck.',0),(10,'Orc','3',30,'Orcs are powerful warriors, aggressive and resilient.',0),(11,'Tiefling','3',30,'Tieflings have infernal heritage, granting magical abilities and fiendish traits.',0),(12,'Tabaxi','3',30,'Tabaxi are feline humanoids, fast and curious, excellent explorers.',0),(13,'Aarakocra','3',25,'Aarakocra are winged humanoids, capable of flight and swift movement.',50),(14,'Kenku','3',30,'Kenku are crow-like humanoids, unable to fly but excellent imitators and stealthy.',0),(15,'Firbolg','4',30,'Firbolgs are peaceful giants connected to nature, strong and silent.',0),(16,'Triton','3',30,'Tritons are aquatic beings with control over water and the ability to breathe underwater.',0),(17,'Goliath','4',30,'Goliaths are mountain giants, exceptional in strength and endurance.',0),(18,'Lizardfolk','3',30,'Lizardfolk are reptilian humanoids, adapted to water and physically strong.',0);
/*!40000 ALTER TABLE `species` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `species_trait`
--

DROP TABLE IF EXISTS `species_trait`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `species_trait` (
  `species_id` int NOT NULL,
  `trait_id` int NOT NULL,
  PRIMARY KEY (`species_id`,`trait_id`),
  KEY `species_trait_trait` (`trait_id`),
  CONSTRAINT `species_trait_species` FOREIGN KEY (`species_id`) REFERENCES `species` (`id`),
  CONSTRAINT `species_trait_trait` FOREIGN KEY (`trait_id`) REFERENCES `trait` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `species_trait`
--

LOCK TABLES `species_trait` WRITE;
/*!40000 ALTER TABLE `species_trait` DISABLE KEYS */;
/*!40000 ALTER TABLE `species_trait` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spell`
--

DROP TABLE IF EXISTS `spell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spell` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `level` int NOT NULL,
  `school_id` int NOT NULL,
  `casting_time` varchar(60) DEFAULT NULL,
  `range` varchar(40) DEFAULT NULL,
  `duration` varchar(60) DEFAULT NULL,
  `components` varchar(10) DEFAULT NULL,
  `material` varchar(255) DEFAULT NULL,
  `concentration` tinyint(1) NOT NULL DEFAULT '0',
  `ritual` tinyint(1) NOT NULL DEFAULT '0',
  `description` text,
  `higher_levels` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `spell_name_UNIQUE` (`name`),
  KEY `spell_school` (`school_id`),
  CONSTRAINT `spell_school` FOREIGN KEY (`school_id`) REFERENCES `spell_school` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spell`
--

LOCK TABLES `spell` WRITE;
/*!40000 ALTER TABLE `spell` DISABLE KEYS */;
/*!40000 ALTER TABLE `spell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spell_school`
--

DROP TABLE IF EXISTS `spell_school`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spell_school` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `spell_school_name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spell_school`
--

LOCK TABLES `spell_school` WRITE;
/*!40000 ALTER TABLE `spell_school` DISABLE KEYS */;
INSERT INTO `spell_school` VALUES (1,'Abjuration'),(2,'Conjuration'),(3,'Divination'),(4,'Enchantment'),(5,'Evocation'),(6,'Illusion'),(7,'Necromancy'),(8,'Transmutation');
/*!40000 ALTER TABLE `spell_school` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stat`
--

DROP TABLE IF EXISTS `stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` char(3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `stat_catalogcol_UNIQUE` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='All the stats';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stat`
--

LOCK TABLES `stat` WRITE;
/*!40000 ALTER TABLE `stat` DISABLE KEYS */;
INSERT INTO `stat` VALUES (6,'CHA'),(3,'CON'),(2,'DEX'),(4,'INT'),(1,'STR'),(5,'WIS');
/*!40000 ALTER TABLE `stat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stat_base_modifier`
--

DROP TABLE IF EXISTS `stat_base_modifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stat_base_modifier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `character_id` int NOT NULL,
  `stat_id` int NOT NULL,
  `value` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `character_id` (`character_id`),
  KEY `stat_id` (`stat_id`),
  CONSTRAINT `stat_base_modifier_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`),
  CONSTRAINT `stat_base_modifier_ibfk_2` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stat_base_modifier`
--

LOCK TABLES `stat_base_modifier` WRITE;
/*!40000 ALTER TABLE `stat_base_modifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `stat_base_modifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subclass`
--

DROP TABLE IF EXISTS `subclass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subclass` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `class_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `subclass_class` (`class_id`),
  CONSTRAINT `subclass_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='For every class subclass catalog';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subclass`
--

LOCK TABLES `subclass` WRITE;
/*!40000 ALTER TABLE `subclass` DISABLE KEYS */;
/*!40000 ALTER TABLE `subclass` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subclass_trait`
--

DROP TABLE IF EXISTS `subclass_trait`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subclass_trait` (
  `subclass_id` int NOT NULL,
  `trait_id` int NOT NULL,
  `level` int NOT NULL,
  PRIMARY KEY (`subclass_id`,`trait_id`),
  KEY `subclass_trait_trait` (`trait_id`),
  CONSTRAINT `subclass_trait_class` FOREIGN KEY (`subclass_id`) REFERENCES `subclass` (`id`),
  CONSTRAINT `subclass_trait_trait` FOREIGN KEY (`trait_id`) REFERENCES `trait` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subclass_trait`
--

LOCK TABLES `subclass_trait` WRITE;
/*!40000 ALTER TABLE `subclass_trait` DISABLE KEYS */;
/*!40000 ALTER TABLE `subclass_trait` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trait`
--

DROP TABLE IF EXISTS `trait`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trait` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` text,
  `type` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `trait_trait_type` (`type`),
  CONSTRAINT `trait_trait_type` FOREIGN KEY (`type`) REFERENCES `trait_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trait`
--

LOCK TABLES `trait` WRITE;
/*!40000 ALTER TABLE `trait` DISABLE KEYS */;
/*!40000 ALTER TABLE `trait` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trait_type`
--

DROP TABLE IF EXISTS `trait_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trait_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `trait_type_name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trait_type`
--

LOCK TABLES `trait_type` WRITE;
/*!40000 ALTER TABLE `trait_type` DISABLE KEYS */;
INSERT INTO `trait_type` VALUES (5,'background'),(2,'class'),(4,'feat'),(7,'general'),(6,'item'),(1,'racial'),(3,'subclass');
/*!40000 ALTER TABLE `trait_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL DEFAULT '',
  `email` varchar(254) NOT NULL DEFAULT '',
  `password_hash` varchar(255) NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_email_UNIQUE` (`email`),
  UNIQUE KEY `user_username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'test','test@test.com','$2a$12$PMGp8LvQHzsAOeRnOlZ8ROcQIxMRPDaMsJzCNNDWjn2Bdwjlh2Nvu','2026-05-25 22:57:48');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weapon`
--

DROP TABLE IF EXISTS `weapon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weapon` (
  `item_id` int NOT NULL,
  `damage_dice` varchar(20) NOT NULL,
  `damage_type_id` int NOT NULL,
  `mastery_id` int NOT NULL,
  `range_normal` int DEFAULT '0',
  `range_long` int DEFAULT '0',
  PRIMARY KEY (`item_id`),
  KEY `weapon_damage_type` (`damage_type_id`),
  KEY `weapon_mastery` (`mastery_id`),
  CONSTRAINT `weapon_damage_type` FOREIGN KEY (`damage_type_id`) REFERENCES `damage_type` (`id`),
  CONSTRAINT `weapon_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`),
  CONSTRAINT `weapon_mastery` FOREIGN KEY (`mastery_id`) REFERENCES `mastery` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weapon`
--

LOCK TABLES `weapon` WRITE;
/*!40000 ALTER TABLE `weapon` DISABLE KEYS */;
/*!40000 ALTER TABLE `weapon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weapon_property`
--

DROP TABLE IF EXISTS `weapon_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weapon_property` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weapon_property`
--

LOCK TABLES `weapon_property` WRITE;
/*!40000 ALTER TABLE `weapon_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `weapon_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weapon_weapon_property`
--

DROP TABLE IF EXISTS `weapon_weapon_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weapon_weapon_property` (
  `weapon_id` int NOT NULL,
  `property_id` int NOT NULL,
  `value` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`weapon_id`,`property_id`),
  KEY `weapon_weapon_property_weapon_property` (`property_id`),
  CONSTRAINT `weapon_weapon_property_weapon` FOREIGN KEY (`weapon_id`) REFERENCES `weapon` (`item_id`),
  CONSTRAINT `weapon_weapon_property_weapon_property` FOREIGN KEY (`property_id`) REFERENCES `weapon_property` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weapon_weapon_property`
--

LOCK TABLES `weapon_weapon_property` WRITE;
/*!40000 ALTER TABLE `weapon_weapon_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `weapon_weapon_property` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-26 19:46:56
