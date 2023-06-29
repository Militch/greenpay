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

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `greenpay` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `greenpay`;

--
-- Table structure for table `agentpay_batch`
--

DROP TABLE IF EXISTS `agentpay_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agentpay_batch` (
  `id` int NOT NULL AUTO_INCREMENT,
  `batch_no` varchar(32) NOT NULL COMMENT '交易批次号',
  `out_batch_no` varchar(32) NOT NULL COMMENT '商户交易批次号',
  `mch_id` int NOT NULL COMMENT '商户ID',
  `total_amount` int NOT NULL DEFAULT '0' COMMENT '总金额（单位：分）',
  `total_count` int NOT NULL DEFAULT '0' COMMENT '总笔数',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '订单回调地址',
  `extra` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `agentpay_passage_id` int NOT NULL COMMENT '代付通道ID',
  `agentpay_passage_acc_id` int NOT NULL COMMENT '代付通道账户ID',
  `pay_interface_id` int NOT NULL COMMENT '支付接口ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '订单状态（1：待处理，2：处理中，3：处理成功，4：部分成功，-1：处理失败）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代付批次';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agentpay_order`
--

DROP TABLE IF EXISTS `agentpay_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agentpay_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `order_sn` varchar(32) NOT NULL COMMENT '订单流水号',
  `out_order_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '代付批次号',
  `mch_id` int NOT NULL COMMENT '商户ID',
  `amount` int NOT NULL DEFAULT '0' COMMENT '订单金额（单位：分）',
  `fee` int NOT NULL DEFAULT '0' COMMENT '订单手续费（单位：分）',
  `account_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1：对私，2：对公',
  `account_name` varchar(32) NOT NULL COMMENT '账户名',
  `account_number` varchar(32) NOT NULL COMMENT '账户号',
  `bank_name` varchar(32) NOT NULL COMMENT '开户行',
  `bank_number` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联行号',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '订单回调地址',
  `extra` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `agentpay_passage_id` int NOT NULL COMMENT '支付通道ID',
  `agentpay_passage_name` varchar(32) NOT NULL COMMENT '代付通道名称',
  `agentpay_passage_acc_id` int NOT NULL COMMENT '支付通道ID',
  `pay_interface_id` int NOT NULL COMMENT '支付接口ID',
  `pay_interface_attr` varchar(255) NOT NULL COMMENT '支付接口参数',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '订单状态（1：待处理，2：处理中，3：处理成功，-1：处理失败，-2：已退账）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代付订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agentpay_passage`
--

DROP TABLE IF EXISTS `agentpay_passage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agentpay_passage` (
  `id` int NOT NULL AUTO_INCREMENT,
  `passage_name` varchar(32) NOT NULL COMMENT '代付通道名称',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `interface_code` varchar(32) NOT NULL COMMENT '支付接口编码',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代付通道';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agentpay_passage_account`
--

DROP TABLE IF EXISTS `agentpay_passage_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agentpay_passage_account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `passage_id` int NOT NULL COMMENT '代付通道ID',
  `account_name` varchar(32) NOT NULL COMMENT '通道账户名称',
  `interface_attr` varchar(255) DEFAULT NULL COMMENT '通道接口参数',
  `weight` int NOT NULL DEFAULT '0' COMMENT '轮询权重',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代付通道账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agentpay_passage_risk`
--

DROP TABLE IF EXISTS `agentpay_passage_risk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agentpay_passage_risk` (
  `id` int NOT NULL AUTO_INCREMENT,
  `passage_id` int NOT NULL COMMENT '代付通道id',
  `passage_name` varchar(255) DEFAULT NULL COMMENT '代付通道名称',
  `amount_min` int NOT NULL DEFAULT '0' COMMENT '单笔代付最低金额',
  `amount_max` int NOT NULL DEFAULT '0' COMMENT '单笔代付最大金额',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0 关闭 1 开启',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商户ID',
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `name` varchar(32) NOT NULL COMMENT '商户名称',
  `email` varchar(32) NOT NULL DEFAULT '' COMMENT '电子邮箱',
  `phone` varchar(11) NOT NULL DEFAULT '' COMMENT '联系手机',
  `password` varchar(32) NOT NULL COMMENT '商户登录密码',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '商户状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_agentpay_passage`
--

DROP TABLE IF EXISTS `merchant_agentpay_passage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_agentpay_passage` (
  `id` int NOT NULL AUTO_INCREMENT,
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `passage_id` int NOT NULL COMMENT '通道ID',
  `passage_name` varchar(32) NOT NULL COMMENT '通道名称',
  `fee_type` int NOT NULL DEFAULT '1' COMMENT '手续费类型（1：百分比收费，2：固定收费，3：百分比加固定收费）',
  `fee_rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '通道费率',
  `fee_amount` int NOT NULL DEFAULT '0' COMMENT '固定费用（单位：分）',
  `weight` int NOT NULL DEFAULT '0' COMMENT '轮询权重',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户代付通道';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_api_config`
--

DROP TABLE IF EXISTS `merchant_api_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_api_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mch_id` int NOT NULL COMMENT '商户ID',
  `api_key` varchar(32) NOT NULL COMMENT '商户APIKEY',
  `api_security` varchar(32) NOT NULL COMMENT '商户API_SECURITY',
  `private_key` varchar(2048) DEFAULT NULL COMMENT '平台私钥',
  `pub_key` varchar(512) DEFAULT NULL COMMENT '平台公钥',
  `mch_pub_key` varchar(512) DEFAULT NULL COMMENT '商户公钥',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户密钥';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_pay_account`
--

DROP TABLE IF EXISTS `merchant_pay_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_pay_account` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商户支付账户ID',
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `avail_balance` int NOT NULL DEFAULT '0' COMMENT '可用余额（分）',
  `freeze_balance` int NOT NULL DEFAULT '0' COMMENT '冻结余额（分）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户支付账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_prepaid_account`
--

DROP TABLE IF EXISTS `merchant_prepaid_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_prepaid_account` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `avail_balance` int NOT NULL DEFAULT '0' COMMENT '可用余额（分）',
  `freeze_balance` int NOT NULL DEFAULT '0' COMMENT '冻结金额（分）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户预充值账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_product`
--

DROP TABLE IF EXISTS `merchant_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_product` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商户产品ID',
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `product_id` int DEFAULT NULL COMMENT '产品ID',
  `product_code` varchar(32) NOT NULL COMMENT '支付产品编码',
  `product_name` varchar(32) NOT NULL COMMENT '支付产品名称',
  `product_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '支付产品类型（1：收款，2：充值）',
  `interface_mode` tinyint(1) NOT NULL DEFAULT '1' COMMENT '接口模式（1:单独，2：轮训）',
  `rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '通道费率',
  `default_passage_id` int DEFAULT NULL COMMENT '默认通道ID',
  `default_passage_acc_id` int DEFAULT NULL COMMENT '默认通道账号ID',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户产品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_product_passage`
--

DROP TABLE IF EXISTS `merchant_product_passage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_product_passage` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mch_id` int NOT NULL COMMENT '商户ID',
  `product_id` int NOT NULL COMMENT '产品ID',
  `passage_id` int NOT NULL COMMENT '支付通道ID',
  `widget` int NOT NULL DEFAULT '0' COMMENT '权重',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户产品通道';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchant_settle_account`
--

DROP TABLE IF EXISTS `merchant_settle_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_settle_account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `settle_fee_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '结算收费类型（1：百分比收费，2：固定收费）',
  `settle_fee_rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '结算费率（百分比）',
  `settle_fee_amount` int NOT NULL DEFAULT '0' COMMENT '结算费用（单位，分）',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户结算账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_interface`
--

DROP TABLE IF EXISTS `pay_interface`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_interface` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '支付接口ID',
  `interface_code` varchar(32) NOT NULL COMMENT '支付接口编码',
  `interface_name` varchar(32) NOT NULL COMMENT '支付接口名称',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `scenarios` tinyint(1) NOT NULL DEFAULT '1' COMMENT '应用场景（1：二维码支付，2：PC网页，3：H5网页，4：移动APP，5：微信公众号，6：支付宝生活号，7：付款码支付）',
  `interface_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '接口调用方式（1：实现类调用，2：插件调用，3：脚本调用）',
  `interface_impl` varchar(255) DEFAULT NULL COMMENT '实现类类名',
  `interface_plugin` varchar(32) DEFAULT NULL COMMENT '插件名称',
  `interface_script` varchar(255) DEFAULT NULL COMMENT '脚本内容',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付接口';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_order`
--

DROP TABLE IF EXISTS `pay_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `order_sn` varchar(32) NOT NULL COMMENT '交易流水号',
  `mch_id` int NOT NULL COMMENT '商户ID',
  `app_id` varchar(32) NOT NULL COMMENT '应用ID',
  `subject` varchar(32) NOT NULL COMMENT '商品标题',
  `out_order_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `amount` int NOT NULL COMMENT '订单金额（单位：分）',
  `body` varchar(64) DEFAULT NULL COMMENT '商品描述',
  `client_ip` int NOT NULL DEFAULT '0' COMMENT '客户端IP',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '回调地址',
  `redirect_url` varchar(255) DEFAULT NULL COMMENT '跳转地址',
  `fee` int NOT NULL DEFAULT '0' COMMENT '订单手续费（单位：分）',
  `pay_product_id` int NOT NULL COMMENT '支付产品ID',
  `pay_product_code` varchar(32) NOT NULL COMMENT '支付产品编码',
  `pay_product_name` varchar(32) NOT NULL COMMENT '支付产品名称',
  `status` tinyint(1) NOT NULL COMMENT '订单状态（1：待付款，2：已支付，3：订单完成，-1：交易取消，-2：交易失败）',
  `paid_at` datetime DEFAULT NULL COMMENT '支付时间',
  `expired_at` datetime DEFAULT NULL COMMENT '过期时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付订单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_order_detail`
--

DROP TABLE IF EXISTS `pay_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_order_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `pay_product_id` int NOT NULL COMMENT '支付产品ID',
  `pay_passage_id` int NOT NULL COMMENT '支付通道ID',
  `pay_passage_acc_id` int NOT NULL COMMENT '支付通道子账户ID',
  `pay_interface_id` int NOT NULL COMMENT '支付接口ID',
  `pay_interface_attr` varchar(4096) NOT NULL COMMENT '支付接口参数',
  `upstream_order_no` varchar(64) DEFAULT NULL COMMENT '上游订单编号',
  `pay_credential` varchar(4096) DEFAULT NULL COMMENT '支付凭证',
  `upstream_extra` varchar(64) DEFAULT NULL COMMENT '上游扩展参数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单详情';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_passage`
--

DROP TABLE IF EXISTS `pay_passage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_passage` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '支付通道ID',
  `passage_name` varchar(32) NOT NULL COMMENT '支付通道名称',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `interface_code` varchar(32) NOT NULL COMMENT '支付接口编码',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付通道';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_passage_account`
--

DROP TABLE IF EXISTS `pay_passage_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_passage_account` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '支付通道账户ID',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `passage_id` int NOT NULL COMMENT '支付通道ID',
  `account_name` varchar(32) NOT NULL COMMENT '通道账户名称',
  `interface_attr` varchar(4096) DEFAULT NULL COMMENT '通道接口参数',
  `weight` int NOT NULL DEFAULT '0' COMMENT '轮询权重',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付通道账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_product`
--

DROP TABLE IF EXISTS `pay_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_product` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '支付产品ID',
  `product_name` varchar(32) NOT NULL COMMENT '支付产品名称',
  `product_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '支付产品类型（1：收款，2：充值）',
  `product_code` varchar(32) NOT NULL COMMENT '支付产品编码',
  `pay_type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `interface_mode` tinyint(1) NOT NULL DEFAULT '1' COMMENT '支付接口模式（1：单独，2：轮询）',
  `default_passage_id` int DEFAULT NULL COMMENT '默认通道ID',
  `default_passage_acc_id` int DEFAULT NULL COMMENT '默认通道账户ID',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付产品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_product_passage`
--

DROP TABLE IF EXISTS `pay_product_passage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_product_passage` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int NOT NULL COMMENT '产品ID',
  `passage_id` int NOT NULL COMMENT '支付通道ID',
  `widget` int NOT NULL DEFAULT '0' COMMENT '权重',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付产品通道子账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_product_passage_acc`
--

DROP TABLE IF EXISTS `pay_product_passage_acc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_product_passage_acc` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int NOT NULL COMMENT '产品ID',
  `passage_id` int NOT NULL COMMENT '支付通道ID',
  `acc_id` int NOT NULL COMMENT '子账户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付产品通道子账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_type`
--

DROP TABLE IF EXISTS `pay_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_code` varchar(32) NOT NULL COMMENT '支付类型编码',
  `type_name` varchar(32) NOT NULL COMMENT '支付类型名称',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '类别(1：支付，2，代付)',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settle_config`
--

DROP TABLE IF EXISTS `settle_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settle_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：关闭，1：开启）',
  `audit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '审核状态（0：关闭，1：开启）',
  `settle_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '结算类型（1：人工结算，2：自动结算）',
  `amount_limit_min` int NOT NULL DEFAULT '0' COMMENT '金额限制（最小值，单位：分）',
  `amount_limit_max` int NOT NULL DEFAULT '0' COMMENT '金额限制（最大值，单位：分）',
  `day_amount_limit_max` int NOT NULL DEFAULT '0' COMMENT '每日金额最大值（单位：分）',
  `settle_fee_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '结算费率类型（1：比例收费，2：固定收费，3：比例收费+固定收费）',
  `settle_rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '结算比例（百分比）',
  `settle_fee` int NOT NULL DEFAULT '0' COMMENT '固定费率（单位：分）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='结算设置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settle_order`
--

DROP TABLE IF EXISTS `settle_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settle_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '结算订单号',
  `order_sn` varchar(32) NOT NULL COMMENT '结算流水号',
  `mch_id` int NOT NULL COMMENT '商户ID',
  `settle_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '结算类型（1：人工结算，2：自动结算）',
  `amount` int NOT NULL DEFAULT '0' COMMENT '订单金额（单位：分）',
  `fee` int NOT NULL DEFAULT '0' COMMENT '结算手续费（单位：分）',
  `settle_amount` int NOT NULL DEFAULT '0' COMMENT '结算金额（单位：分）',
  `account_name` varchar(32) NOT NULL COMMENT '结算账户名',
  `account_number` varchar(32) NOT NULL COMMENT '结算账号',
  `bank_name` varchar(32) NOT NULL COMMENT '开户行名称',
  `bank_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '银行联行号',
  `bank_address` varchar(64) DEFAULT NULL COMMENT '开户行地址（或支行名称）',
  `pay_type_code` varchar(32) DEFAULT NULL COMMENT '支付类型编码',
  `agentpay_passage_id` int DEFAULT NULL COMMENT '代付通道ID',
  `agentpay_passage_acc_id` int DEFAULT NULL COMMENT '代付通道账户ID',
  `pay_interface_id` int DEFAULT NULL COMMENT '支付接口ID',
  `pay_interface_attr` varchar(255) DEFAULT NULL COMMENT '支付接口参数',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1：待审核，2：待处理，3：处理中，4：已结算，-1：已驳回，-2：结算失败）',
  `settled_at` datetime DEFAULT NULL COMMENT '结算时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户结算订单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_menu`
--

DROP TABLE IF EXISTS `system_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单标题',
  `mark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单标识',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '菜单类型（1:目录,2:菜单,3:按钮）',
  `icon` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '目录图标',
  `path` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单路由',
  `parent_id` int DEFAULT NULL COMMENT '上级菜单ID',
  `sorts` int NOT NULL DEFAULT '0' COMMENT '排序权重',
  `extra` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role`
--

DROP TABLE IF EXISTS `system_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role_menu`
--

DROP TABLE IF EXISTS `system_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL COMMENT '角色ID',
  `menu_id` int NOT NULL COMMENT '菜单ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=385 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_user`
--

DROP TABLE IF EXISTS `system_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `totp_secret_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '两步验证安全码',
  `totp_verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '两部验证是否验证（0：否，1，是）',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户邮箱',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username_uindex` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_user_role`
--

DROP TABLE IF EXISTS `system_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户角色';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-06-28 22:23:07
