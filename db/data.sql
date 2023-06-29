-- MySQL dump 10.13  Distrib 8.0.32, for macos13.0 (x86_64)
--
-- Host: localhost    Database: greenpay
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Current Database: `greenpay`
--

USE `greenpay`;

--
-- Dumping data for table `agentpay_batch`
--

LOCK TABLES `agentpay_batch` WRITE;
/*!40000 ALTER TABLE `agentpay_batch` DISABLE KEYS */;
INSERT INTO `agentpay_batch` VALUES (1,'2','2',1,200,1,'1','1','1',1,1,1,3,'2020-06-11 22:27:01','2020-06-11 22:27:01');
/*!40000 ALTER TABLE `agentpay_batch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `agentpay_order`
--

LOCK TABLES `agentpay_order` WRITE;
/*!40000 ALTER TABLE `agentpay_order` DISABLE KEYS */;
INSERT INTO `agentpay_order` VALUES (1,'1271058780318208000','20200611083643017081','20200611123638975467',NULL,1,5000,122,1,'李川','6222803761901018721','中国建设银行','5148754',NULL,NULL,'bank_t',1,'平安银行代付通道',1,1,'{\"companyCode\":\"00901275100000003000\",\"acctNo\":\"15000103599403\",\"host\":\"http://103.126.241.222:7072\"}',3,'2020-06-11 20:36:48','2020-06-11 20:36:49'),(6,'1271062094879526912','20200611084953270882','20200611124950997323',NULL,1,5000,122,1,'李川','6222803761901018721','中国建设银行','5148754',NULL,NULL,'bank_t',1,'平安银行代付通道',1,1,'{\"companyCode\":\"00901275100000003000\",\"acctNo\":\"15000103599403\",\"host\":\"http://103.126.241.222:7072\"}',3,'2020-06-11 20:49:54','2020-06-11 20:50:30'),(7,'1271098841151705088','1271098841151705089','1271098840006660096','1271098841051041792',1,5000,244,2,'冷中平','6225210903208891','浦发银行',NULL,NULL,NULL,'bank_t',1,'平安银行代付通道',1,1,'{\"companyCode\":\"00901275100000003000\",\"acctNo\":\"15000103599403\",\"host\":\"http://103.126.241.222:7072\"}',3,'2020-06-11 23:15:56','2020-06-11 23:15:56'),(8,'1271098844427456512','1271098844427456513','1271098840006660097','1271098841051041792',1,4900,244,2,'冷中平','6228480478307210375','中国农业银行',NULL,NULL,NULL,'bank_t',1,'平安银行代付通道',1,1,'{\"companyCode\":\"00901275100000003000\",\"acctNo\":\"15000103599403\",\"host\":\"http://103.126.241.222:7072\"}',3,'2020-06-11 23:16:27','2020-06-11 23:16:27');
/*!40000 ALTER TABLE `agentpay_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `agentpay_passage`
--

LOCK TABLES `agentpay_passage` WRITE;
/*!40000 ALTER TABLE `agentpay_passage` DISABLE KEYS */;
INSERT INTO `agentpay_passage` VALUES (1,'平安银行代付通道','bank_t','银行转账',1,'2020-06-11 19:55:28','2020-06-11 19:55:28');
/*!40000 ALTER TABLE `agentpay_passage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `agentpay_passage_account`
--

LOCK TABLES `agentpay_passage_account` WRITE;
/*!40000 ALTER TABLE `agentpay_passage_account` DISABLE KEYS */;
INSERT INTO `agentpay_passage_account` VALUES (1,'bank_t',1,'苏州','{\"companyCode\":\"00901275100000003000\",\"acctNo\":\"15000103599403\",\"host\":\"http://103.126.241.222:7072\"}',9,1,'2020-06-11 19:56:20','2020-06-11 19:56:20');
/*!40000 ALTER TABLE `agentpay_passage_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `agentpay_passage_risk`
--

LOCK TABLES `agentpay_passage_risk` WRITE;
/*!40000 ALTER TABLE `agentpay_passage_risk` DISABLE KEYS */;
/*!40000 ALTER TABLE `agentpay_passage_risk` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
INSERT INTO `merchant` VALUES (1,'test','测试商户','77777@qq.com','','202cb962ac59075b964b07152d234b70',1,'2020-06-11 19:57:27','2020-06-11 19:57:27'),(2,'test2020','测试商户','admin@999.com','','21232f297a57a5a743894a0e4a801fc3',1,'2020-07-06 18:54:01','2020-07-06 18:54:01');
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant_agentpay_passage`
--

LOCK TABLES `merchant_agentpay_passage` WRITE;
/*!40000 ALTER TABLE `merchant_agentpay_passage` DISABLE KEYS */;
INSERT INTO `merchant_agentpay_passage` VALUES (2,1,1,'平安银行代付通道',3,0.45,100,9,1,'2020-06-11 19:57:59','2020-06-11 19:57:59');
/*!40000 ALTER TABLE `merchant_agentpay_passage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant_api_config`
--

LOCK TABLES `merchant_api_config` WRITE;
/*!40000 ALTER TABLE `merchant_api_config` DISABLE KEYS */;
INSERT INTO `merchant_api_config` VALUES (1,1,'662b89e1cebf629639908cc7a2f2c925','f5269d0de5d6145c4e26067ccbdfdb79','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','2020-06-11 19:57:27','2020-06-11 19:57:27'),(2,2,'ba2afbd562884db61a9779de283dec63','28da5476706942b661b86bc536f9ae78','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',NULL,'2020-07-06 18:54:04','2020-07-06 18:54:04');
/*!40000 ALTER TABLE `merchant_api_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant_pay_account`
--

LOCK TABLES `merchant_pay_account` WRITE;
/*!40000 ALTER TABLE `merchant_pay_account` DISABLE KEYS */;
INSERT INTO `merchant_pay_account` VALUES (1,1,0,0,'2020-06-11 19:57:27','2020-06-11 19:57:27'),(2,2,0,0,'2020-07-06 18:54:04','2020-07-06 18:54:04');
/*!40000 ALTER TABLE `merchant_pay_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant_prepaid_account`
--

LOCK TABLES `merchant_prepaid_account` WRITE;
/*!40000 ALTER TABLE `merchant_prepaid_account` DISABLE KEYS */;
INSERT INTO `merchant_prepaid_account` VALUES (1,1,89756,0,'2020-06-11 19:57:27','2020-06-11 19:57:27'),(2,2,0,0,'2020-07-06 18:54:04','2020-07-06 18:54:04');
/*!40000 ALTER TABLE `merchant_prepaid_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant_product`
--

LOCK TABLES `merchant_product` WRITE;
/*!40000 ALTER TABLE `merchant_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant_product_passage`
--

LOCK TABLES `merchant_product_passage` WRITE;
/*!40000 ALTER TABLE `merchant_product_passage` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_product_passage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `merchant_settle_account`
--

LOCK TABLES `merchant_settle_account` WRITE;
/*!40000 ALTER TABLE `merchant_settle_account` DISABLE KEYS */;
INSERT INTO `merchant_settle_account` VALUES (1,1,1,0.00,0,0,'2020-06-11 19:57:28','2020-06-11 19:57:27'),(2,2,1,0.00,0,0,'2020-07-06 18:54:04','2020-07-06 18:54:04');
/*!40000 ALTER TABLE `merchant_settle_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_interface`
--

LOCK TABLES `pay_interface` WRITE;
/*!40000 ALTER TABLE `pay_interface` DISABLE KEYS */;
INSERT INTO `pay_interface` VALUES (1,'银行转账','平安银行_转账','bank_t',1,1,'corder:pingAnPlugin;query:pingAnQueryPlugin',NULL,NULL,1,'2020-06-11 19:54:42','2020-06-11 19:54:42'),(2,'test','test','bank_t',1,3,'if action == \"create\" call(\"abc\") else if \"query\" call(\"query\") endif',NULL,NULL,0,'2020-06-19 18:53:38','2020-06-19 18:53:38'),(3,'hq_pay','环球支付','hq_pay',1,1,'HQPayPlugin',NULL,NULL,1,'2020-07-02 10:38:57','2020-07-02 10:38:57'),(4,'ali_h5','支付宝H5_酷云支付','alipay_h5',3,1,'comQyefd',NULL,NULL,1,'2020-07-06 16:52:05','2020-07-06 16:52:05');
/*!40000 ALTER TABLE `pay_interface` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_order`
--

LOCK TABLES `pay_order` WRITE;
/*!40000 ALTER TABLE `pay_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_order_detail`
--

LOCK TABLES `pay_order_detail` WRITE;
/*!40000 ALTER TABLE `pay_order_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_passage`
--

LOCK TABLES `pay_passage` WRITE;
/*!40000 ALTER TABLE `pay_passage` DISABLE KEYS */;
INSERT INTO `pay_passage` VALUES (1,'环球支付','hq_pay','hq_pay',1,'2020-07-02 10:42:52','2020-07-02 10:42:52'),(2,'ali_h5_com_qyefd','alipay_h5','ali_h5',1,'2020-07-06 16:55:35','2020-07-06 16:55:35');
/*!40000 ALTER TABLE `pay_passage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_passage_account`
--

LOCK TABLES `pay_passage_account` WRITE;
/*!40000 ALTER TABLE `pay_passage_account` DISABLE KEYS */;
INSERT INTO `pay_passage_account` VALUES (1,'hq_pay',1,'环球支付_测试','{\"memberid\":\"\",\"apiClientPrivKey\":\"\"}',1,1,'2020-07-02 10:45:03','2020-07-02 10:45:03'),(2,'alipay_h5',2,'商户1','{\"apiGetway\":\"http://qyefd.com/paididx/index.html\",\"memberId\":\"200730808\",\"apiKey\":\"d88wnw7ncz94pb4u1hmeyfk5fedwu7rh\"}',1,1,'2020-07-06 17:01:59','2020-07-06 17:01:59');
/*!40000 ALTER TABLE `pay_passage_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_product`
--

LOCK TABLES `pay_product` WRITE;
/*!40000 ALTER TABLE `pay_product` DISABLE KEYS */;
INSERT INTO `pay_product` VALUES (1,'支付宝_H5',1,'ali_h5','alipay_h5',1,2,2,1,'2020-07-06 17:02:57','2020-07-06 17:55:49');
/*!40000 ALTER TABLE `pay_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_product_passage`
--

LOCK TABLES `pay_product_passage` WRITE;
/*!40000 ALTER TABLE `pay_product_passage` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_product_passage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_product_passage_acc`
--

LOCK TABLES `pay_product_passage_acc` WRITE;
/*!40000 ALTER TABLE `pay_product_passage_acc` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_product_passage_acc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pay_type`
--

LOCK TABLES `pay_type` WRITE;
/*!40000 ALTER TABLE `pay_type` DISABLE KEYS */;
INSERT INTO `pay_type` VALUES (1,'bank_t','银行转账',2,1,'2020-06-11 19:54:14','2020-06-11 19:54:14'),(2,'hq_pay','环球支付',1,1,'2020-07-02 10:38:09','2020-07-02 10:38:09'),(3,'alipay_h5','支付宝h5',1,1,'2020-07-06 15:12:27','2020-07-06 15:12:27');
/*!40000 ALTER TABLE `pay_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `settle_config`
--

LOCK TABLES `settle_config` WRITE;
/*!40000 ALTER TABLE `settle_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `settle_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `settle_order`
--

LOCK TABLES `settle_order` WRITE;
/*!40000 ALTER TABLE `settle_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `settle_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `system_menu`
--

LOCK TABLES `system_menu` WRITE;
/*!40000 ALTER TABLE `system_menu` DISABLE KEYS */;
INSERT INTO `system_menu` VALUES (1,'首页','home',1,'layui-icon-home','/admin',0,99,'','2020-06-12 11:11:24','2020-06-12 11:11:24'),(2,'支付数据概览','home:payInfo',2,'','/home',1,0,'null','2020-06-13 11:16:32','2020-06-13 11:16:32'),(3,'代付数据概览','home:merchantInfo',2,NULL,'/merchantInfo',1,0,NULL,'2020-06-13 11:16:32','2020-06-13 11:16:32'),(4,'商户管理','merchant',1,'layui-icon-user','',0,98,'null','2020-06-13 11:19:27','2020-06-13 11:19:27'),(8,'商户列表','merchant:list',2,NULL,'/merchant/list',4,0,NULL,'2020-06-13 11:21:15','2020-06-13 11:21:15'),(9,'添加商户','merchant:add',2,NULL,'/merchant/add',4,0,NULL,'2020-06-13 11:21:15','2020-06-13 11:21:15'),(10,'订单管理','order',1,'layui-icon-file','',0,96,'null','2020-06-13 11:24:54','2020-06-13 11:24:54'),(11,'支付订单','order:pay',2,NULL,'/order/list',10,0,NULL,'2020-06-13 11:24:54','2020-06-13 11:24:54'),(12,'支付配置','pay',1,'layui-icon-component','',0,95,'null','2020-06-13 11:29:23','2020-06-13 11:29:23'),(20,'支付类型','pay:type:list',2,'','/pay/type/list',12,99,'null','2020-06-13 11:29:23','2020-06-13 11:29:23'),(21,'支付接口','pay:interface:list',2,'','/pay/interface/list',12,98,'null','2020-06-13 11:29:23','2020-06-13 11:29:23'),(22,'支付通道','pay:passage:list',2,'','/pay/passage/list',12,97,'null','2020-06-13 11:29:23','2020-06-13 11:29:23'),(23,'支付产品','pay:product:list',2,'','/pay/product/list',12,96,'null','2020-06-13 11:29:23','2020-06-13 11:29:23'),(24,'结算管理','settle',1,'layui-icon-star','',0,97,'null','2020-06-13 11:31:35','2020-06-13 11:31:35'),(25,'结算审核','settle:audit',2,NULL,'/settle/audit',24,0,NULL,'2020-06-13 11:33:34','2020-06-13 11:33:34'),(26,'应付结算','settle:payable',2,NULL,'/settle/payable',24,0,NULL,'2020-06-13 11:33:34','2020-06-13 11:33:34'),(27,'结算设置','admin:settle:settings',2,NULL,'/settle/settings',99999,0,NULL,'2020-06-13 11:33:34','2020-06-13 11:33:34'),(28,'代付管理','agentpay',1,'layui-icon-release',NULL,0,40,NULL,'2020-06-13 11:34:45','2020-06-13 11:34:45'),(29,'代付订单','agentpay:order:list',2,'','/agentpay/order/list',28,10,'null','2020-06-13 11:36:55','2020-06-13 11:36:55'),(30,'批次管理','agentpay:batch:list',2,'','/agentpay/batch/list',28,30,'null','2020-06-13 11:36:55','2020-06-13 11:36:55'),(31,'代付通道','agentpay:passage:list',2,'','/agentpay/passage/list',28,20,'null','2020-06-13 11:36:55','2020-06-13 11:36:55'),(32,'个人中心','user',1,'layui-icon-username','',0,1,'null','2020-06-13 11:38:32','2020-06-13 11:38:32'),(33,'基础资料','user:profile',2,NULL,'/user/profile',32,0,NULL,'2020-06-13 11:39:34','2020-06-13 11:39:34'),(34,'安全设置','user:security',2,NULL,'/user/security',32,0,NULL,'2020-06-13 11:39:34','2020-06-13 11:39:34'),(35,'系统管理','admin',1,'layui-icon-set','',0,0,'null','2020-06-13 11:43:54','2020-06-13 11:43:54'),(43,'用户管理','admin:system:user:list',2,NULL,'/admin/system/user/list',35,0,NULL,'2020-06-13 11:43:54','2020-06-13 11:43:54'),(44,'角色管理','admin:system:role:list',2,NULL,'/admin/system/role/list',35,0,NULL,'2020-06-13 11:43:54','2020-06-13 11:43:54'),(45,'权限管理','admin:system:menu:list',2,NULL,'/admin/system/menu/list',35,0,NULL,'2020-06-13 11:43:54','2020-06-13 11:43:54'),(80,'定时任务','dada',2,'','/admin/',35,0,'','2020-07-06 11:52:38','2020-07-06 11:52:38'),(82,'新增','merchant:list:add',3,'','',8,0,'','2020-07-06 18:29:57','2020-07-06 18:29:57'),(83,'删除','merchant:list:del',3,'','',8,0,'','2020-07-06 18:30:32','2020-07-06 18:30:32'),(84,'修改','merchant:list:edit',3,'','',8,0,'','2020-07-06 18:30:51','2020-07-06 18:30:51'),(85,'查看','merchant:list:view',3,'','',8,0,'','2020-07-06 18:31:24','2020-07-06 18:31:24'),(86,'查看','order:pay:view',3,'','',11,0,'','2020-07-06 18:46:25','2020-07-06 18:46:25'),(87,'通知','order:pay:notify',3,'','',11,0,'','2020-07-06 18:48:33','2020-07-06 18:48:33'),(88,'补单','order:pay:supply',3,'','',11,0,'','2020-07-06 18:49:22','2020-07-06 18:49:22'),(89,'退款','order:pay:refund',3,'','',11,0,'','2020-07-06 18:50:26','2020-07-06 18:50:26'),(90,'支付通道配置','merchant:list:paypassge',3,'','',8,0,'','2020-07-06 18:55:26','2020-07-06 18:55:26'),(91,'代付通道配置','merchant:list:agpaypassage',3,'','',8,0,'','2020-07-06 18:56:06','2020-07-06 18:56:06'),(92,'查看','settle:audit:view',3,'','',25,0,'','2020-07-06 18:59:35','2020-07-06 18:59:35'),(93,'驳回','settle:audit:nopass',3,'','',25,0,'','2020-07-06 19:00:31','2020-07-06 19:00:31'),(94,'审核通过','settle:audit:pass',3,'','',25,0,'','2020-07-06 19:01:42','2020-07-06 19:01:42'),(95,'查看','settle:payable:view',3,'','',26,0,'','2020-07-06 19:02:43','2020-07-06 19:02:43'),(96,'结算','settle:payable:pay',3,'','',26,0,'','2020-07-06 19:08:04','2020-07-06 19:08:04'),(97,'标记结算','settle:payable:mpay',3,'','',26,0,'','2020-07-06 19:16:03','2020-07-06 19:16:03');
/*!40000 ALTER TABLE `system_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `system_role`
--

LOCK TABLES `system_role` WRITE;
/*!40000 ALTER TABLE `system_role` DISABLE KEYS */;
INSERT INTO `system_role` VALUES (1,'admin','system:admin','2020-06-12 11:11:50','2020-06-12 11:11:50'),(8,'test','test','2020-06-13 18:12:45','2020-06-13 18:12:45'),(9,'Finance','2','2020-06-18 17:39:38','2020-06-18 17:39:38'),(10,'tests1','aaa','2020-06-23 18:03:15','2020-06-23 18:03:15'),(11,'test5','admin:','2020-06-23 18:08:23','2020-06-23 18:08:23');
/*!40000 ALTER TABLE `system_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `system_role_menu`
--

LOCK TABLES `system_role_menu` WRITE;
/*!40000 ALTER TABLE `system_role_menu` DISABLE KEYS */;
INSERT INTO `system_role_menu` VALUES (83,9,2,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(84,9,1,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(85,9,11,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(86,9,10,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(87,9,25,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(88,9,26,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(89,9,24,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(90,9,29,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(91,9,28,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(92,9,33,'2020-06-18 17:39:38','2020-06-18 17:39:38'),(93,9,34,'2020-06-18 17:39:39','2020-06-18 17:39:39'),(94,9,32,'2020-06-18 17:39:39','2020-06-18 17:39:39'),(95,8,2,'2020-06-20 16:00:54','2020-06-20 16:00:54'),(96,8,3,'2020-06-20 16:00:54','2020-06-20 16:00:54'),(97,8,1,'2020-06-20 16:00:54','2020-06-20 16:00:54'),(125,10,2,'2020-06-23 18:07:42','2020-06-23 18:07:42'),(126,10,3,'2020-06-23 18:07:42','2020-06-23 18:07:42'),(127,10,1,'2020-06-23 18:07:42','2020-06-23 18:07:42'),(128,10,8,'2020-06-23 18:07:43','2020-06-23 18:07:43'),(129,10,9,'2020-06-23 18:07:43','2020-06-23 18:07:43'),(130,10,4,'2020-06-23 18:07:43','2020-06-23 18:07:43'),(175,11,3,'2020-06-23 18:09:27','2020-06-23 18:09:27'),(176,11,1,'2020-06-23 18:09:27','2020-06-23 18:09:27'),(177,11,20,'2020-06-23 18:09:27','2020-06-23 18:09:27'),(178,11,21,'2020-06-23 18:09:27','2020-06-23 18:09:27'),(179,11,23,'2020-06-23 18:09:27','2020-06-23 18:09:27'),(180,11,12,'2020-06-23 18:09:27','2020-06-23 18:09:27'),(342,1,2,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(343,1,3,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(344,1,1,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(345,1,8,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(346,1,82,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(347,1,83,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(348,1,84,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(349,1,85,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(350,1,90,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(351,1,91,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(352,1,9,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(353,1,4,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(354,1,11,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(355,1,86,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(356,1,87,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(357,1,88,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(358,1,89,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(359,1,10,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(360,1,20,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(361,1,21,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(362,1,22,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(363,1,23,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(364,1,12,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(365,1,25,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(366,1,92,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(367,1,93,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(368,1,94,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(369,1,26,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(370,1,95,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(371,1,96,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(372,1,97,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(373,1,24,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(374,1,29,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(375,1,30,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(376,1,31,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(377,1,28,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(378,1,33,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(379,1,34,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(380,1,32,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(381,1,43,'2020-07-06 19:16:23','2020-07-06 19:16:23'),(382,1,44,'2020-07-06 19:16:24','2020-07-06 19:16:24'),(383,1,45,'2020-07-06 19:16:24','2020-07-06 19:16:24'),(384,1,35,'2020-07-06 19:16:24','2020-07-06 19:16:24');
/*!40000 ALTER TABLE `system_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `system_user`
--

LOCK TABLES `system_user` WRITE;
/*!40000 ALTER TABLE `system_user` DISABLE KEYS */;
INSERT INTO `system_user` VALUES (2,'admin','81dc9bdb52d04dc20036dbd8313ed055','9b5587e355c64039894d442e8b2b14e0',1,'admin@admin.com','2020-06-12 11:16:09','2020-06-12 11:16:07'),(4,'test2','81dc9bdb52d04dc20036dbd8313ed055',NULL,0,'test2@qq.com','2020-06-12 11:30:15','2020-06-12 11:30:14'),(5,'test3','81dc9bdb52d04dc20036dbd8313ed055',NULL,0,'test3@qq.com','2020-06-12 11:32:46','2020-06-12 11:32:44'),(6,'finance','81dc9bdb52d04dc20036dbd8313ed055',NULL,0,'finance@finance.com','2020-06-18 17:52:46','2020-06-18 17:52:48');
/*!40000 ALTER TABLE `system_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `system_user_role`
--

LOCK TABLES `system_user_role` WRITE;
/*!40000 ALTER TABLE `system_user_role` DISABLE KEYS */;
INSERT INTO `system_user_role` VALUES (1,2,1,'2020-06-12 11:16:08','2020-06-12 11:16:08'),(2,4,1,'2020-06-12 11:30:14','2020-06-12 11:30:14'),(3,5,1,'2020-06-12 11:32:44','2020-06-12 11:32:44'),(9,6,9,'2020-06-18 17:52:48','2020-06-18 17:52:48');
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

-- Dump completed on 2023-06-28 22:24:59
