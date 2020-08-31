-- MySQL dump 10.13  Distrib 8.0.17, for Linux (x86_64)
--
-- Host: localhost    Database: greenadmin
-- ------------------------------------------------------
-- Server version	8.0.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `system_menu`
--

DROP TABLE IF EXISTS `system_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单标题',
  `mark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单标识',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '菜单类型（1:目录,2:菜单,3:按钮）',
  `icon` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '目录图标',
  `path` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单路由',
  `parent_id` int(11) DEFAULT NULL COMMENT '上级菜单ID',
  `sorts` int(11) NOT NULL DEFAULT '0' COMMENT '排序权重',
  `extra` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_menu`
--

LOCK TABLES `system_menu` WRITE;
/*!40000 ALTER TABLE `system_menu` DISABLE KEYS */;
INSERT INTO `system_menu` VALUES (1,'系统管理','system',1,'layui-icon-set',NULL,0,-1,NULL,'2020-08-31 22:36:21','2020-08-31 22:36:21');
INSERT INTO `system_menu` VALUES (2,'用户管理','system_user',2,NULL,'/system/user/list',1,99,NULL,'2020-08-31 22:37:29','2020-08-31 22:37:29');
INSERT INTO `system_menu` VALUES (3,'新增','system_user_add',3,NULL,'',2,99,NULL,'2020-08-31 22:38:17','2020-08-31 22:38:17');
INSERT INTO `system_menu` VALUES (4,'删除','system_user_del',3,NULL,NULL,2,98,NULL,'2020-08-31 22:38:47','2020-08-31 22:38:47');
INSERT INTO `system_menu` VALUES (5,'修改','system_user_update',3,NULL,NULL,2,97,NULL,'2020-08-31 22:39:15','2020-08-31 22:39:15');
INSERT INTO `system_menu` VALUES (6,'查看','system_user_view',3,NULL,NULL,2,96,NULL,'2020-08-31 22:39:53','2020-08-31 22:39:53');
INSERT INTO `system_menu` VALUES (7,'角色管理','system_role',2,NULL,'/system/role/list',1,98,NULL,'2020-08-31 22:40:36','2020-08-31 22:40:36');
INSERT INTO `system_menu` VALUES (8,'新增','system_role_add',3,NULL,NULL,7,99,NULL,'2020-08-31 22:42:05','2020-08-31 22:42:05');
INSERT INTO `system_menu` VALUES (9,'删除','system_role_del',3,NULL,NULL,7,98,NULL,'2020-08-31 22:42:38','2020-08-31 22:42:38');
INSERT INTO `system_menu` VALUES (10,'修改','system_role_update',3,NULL,NULL,7,97,NULL,'2020-08-31 22:43:12','2020-08-31 22:43:12');
INSERT INTO `system_menu` VALUES (11,'查看','system_role_view',3,NULL,NULL,7,96,NULL,'2020-08-31 22:44:51','2020-08-31 22:44:51');
INSERT INTO `system_menu` VALUES (12,'菜单管理','system_menu',2,NULL,'/system/menu/list',1,97,NULL,'2020-08-31 22:45:34','2020-08-31 22:45:34');
INSERT INTO `system_menu` VALUES (13,'新增','system_menu_add',3,NULL,NULL,12,99,NULL,'2020-08-31 22:46:05','2020-08-31 22:46:05');
INSERT INTO `system_menu` VALUES (14,'删除','system_menu_del',3,NULL,NULL,12,98,NULL,'2020-08-31 22:46:40','2020-08-31 22:46:40');
INSERT INTO `system_menu` VALUES (15,'修改','system_menu_update',3,NULL,NULL,12,97,NULL,'2020-08-31 22:47:23','2020-08-31 22:47:23');
INSERT INTO `system_menu` VALUES (16,'查看','system_menu_view',3,NULL,NULL,12,96,NULL,'2020-08-31 22:47:54','2020-08-31 22:47:54');
/*!40000 ALTER TABLE `system_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_role`
--

DROP TABLE IF EXISTS `system_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_role`
--

LOCK TABLES `system_role` WRITE;
/*!40000 ALTER TABLE `system_role` DISABLE KEYS */;
INSERT INTO `system_role` VALUES (1,'超级管理员','root','2020-08-31 22:48:27','2020-08-31 22:48:27');
/*!40000 ALTER TABLE `system_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_role_menu`
--

DROP TABLE IF EXISTS `system_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_role_menu`
--

LOCK TABLES `system_role_menu` WRITE;
/*!40000 ALTER TABLE `system_role_menu` DISABLE KEYS */;
INSERT INTO `system_role_menu` VALUES (1,1,1,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (2,1,2,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (3,1,3,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (4,1,4,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (5,1,5,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (6,1,6,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (7,1,7,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (8,1,8,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (9,1,9,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (10,1,10,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (11,1,11,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (12,1,12,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (13,1,13,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (14,1,14,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (15,1,15,'2020-08-31 22:49:13','2020-08-31 22:49:13');
INSERT INTO `system_role_menu` VALUES (16,1,16,'2020-08-31 22:49:13','2020-08-31 22:49:13');
/*!40000 ALTER TABLE `system_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_user`
--

DROP TABLE IF EXISTS `system_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `password` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `totp_secret_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '两步验证安全码',
  `totp_verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '两部验证是否验证（0：否，1，是）',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户邮箱',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username_uindex` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user`
--

LOCK TABLES `system_user` WRITE;
/*!40000 ALTER TABLE `system_user` DISABLE KEYS */;
INSERT INTO `system_user` VALUES (1,'admin','超级管理员','21232f297a57a5a743894a0e4a801fc3',NULL,0,'admin@example.com','2020-08-31 22:34:04','2020-08-31 22:34:04');
/*!40000 ALTER TABLE `system_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_user_role`
--

DROP TABLE IF EXISTS `system_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user_role`
--

LOCK TABLES `system_user_role` WRITE;
/*!40000 ALTER TABLE `system_user_role` DISABLE KEYS */;
INSERT INTO `system_user_role` VALUES (1,1,1,'2020-08-31 22:48:54','2020-08-31 22:48:54');
/*!40000 ALTER TABLE `system_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-08-31 22:53:44
