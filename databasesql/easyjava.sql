/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50728
Source Host           : localhost:3306
Source Database       : easyjava

Target Server Type    : MYSQL
Target Server Version : 50728
File Encoding         : 65001

Date: 2019-12-01 22:44:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- nft.`user` definition

CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `account` varchar(255) NOT NULL COMMENT '账号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `my_invite_code` varchar(255) NOT NULL COMMENT '我的邀请码',
  `invite_code` varchar(255) NOT NULL COMMENT '邀请码',
  `chr_address` varchar(255) NOT NULL COMMENT '合约币地址',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态1正常',
  `chr_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '合约币数量',
  `game_coin_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '游戏币数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_unique` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- nft.klay_txs definition

CREATE TABLE `klay_txs` (
  `txHash` varchar(500) NOT NULL,
  `createdAt` bigint DEFAULT NULL,
  `txType` varchar(100) DEFAULT NULL,
  `txStatus` int DEFAULT NULL,
  `inputHeader` varchar(100) DEFAULT NULL,
  `blockNumber` bigint DEFAULT NULL,
  `fromAddress` varchar(500) DEFAULT NULL,
  `toAddress` varchar(500) DEFAULT NULL,
  `amount` varchar(100) DEFAULT NULL,
  `txFee` varchar(100) DEFAULT NULL,
  `gasLimit` varchar(100) DEFAULT NULL,
  `gasUsed` varchar(100) DEFAULT NULL,
  `nonce` varchar(100) DEFAULT NULL,
  `gasPrice` varchar(100) DEFAULT NULL,
  `parentHash` varchar(255) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `methodName` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`txHash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `order_usdt` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `send_value` varchar(100) DEFAULT NULL COMMENT '实际发送的金额，比如1usdt发送1.03324',
  `buy_amount` varchar(100) DEFAULT NULL COMMENT '购买合约币数量',
  `status` varchar(100) DEFAULT NULL COMMENT '状态，1创建，2收到usdt，3发送合约币',
  `to_address` varchar(255) DEFAULT NULL COMMENT 'usdt-erc20的接收地址',
  `date` datetime DEFAULT NULL COMMENT '创建时间',
  `price` varchar(100) DEFAULT NULL COMMENT '1usdt换多少',
  `from_address` varchar(300) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_usdt_date_IDX` (`date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- nft.eth_log definition

CREATE TABLE `eth_log` (
  `block_num` bigint DEFAULT NULL,
  `from` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `to` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `timestamp` bigint DEFAULT NULL,
  `hash` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `input` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `token` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  `contract` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL,
  UNIQUE KEY `hash_unique` (`hash`),
  KEY `eth_log_from_IDX` (`from`) USING BTREE,
  KEY `eth_log_to_IDX` (`to`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- nft.user_wallet definition

CREATE TABLE `user_wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '钱包地址',
  `encrypt_key` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `encrypted_private` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL,
  `status` int NOT NULL DEFAULT '0' COMMENT '钱包状态1主钱包',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_wallet_UN` (`address`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- nft.user_log definition

CREATE TABLE `user_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `log` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `data` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  `type` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
-- 查询服务链的交易，定期保存入库
CREATE TABLE nft.scn_scan_tx (
	hash varchar(200) NOT NULL,
	blockHash varchar(200) NULL,
	blockNumber varchar(100) NULL,
	codeFormat varchar(100) NULL,
	feePayer varchar(100) NULL,
	feeRatio varchar(100) NULL,
	`from` varchar(100) NULL,
	gas varchar(100) NULL,
	gasPrice varchar(100) NULL,
	`key` varchar(100) NULL,
	`input` varchar(100) NULL,
	nonce varchar(100) NULL,
	senderTxHash varchar(100) NULL,
	`to` varchar(100) NULL,
	transactionIndex varchar(100) NULL,
	`type` varchar(100) NULL,
	typeInt varchar(100) NULL,
	value varchar(100) NULL,
	chainID varchar(100) NULL,
	CONSTRAINT scn_scan_tx_PK PRIMARY KEY (hash)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_bin;
