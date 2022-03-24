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
  `createdAt` bigint(20) DEFAULT NULL,
  `txType` varchar(100) DEFAULT NULL,
  `txStatus` int(11) DEFAULT NULL,
  `inputHeader` varchar(100) DEFAULT NULL,
  `blockNumber` bigint(20) DEFAULT NULL,
  `fromAddress` varchar(500) DEFAULT NULL,
  `toAddress` varchar(500) DEFAULT NULL,
  `amount` varchar(100) DEFAULT NULL,
  `txFee` varchar(100) DEFAULT NULL,
  `gasLimit` varchar(100) DEFAULT NULL,
  `gasUsed` varchar(100) DEFAULT NULL,
  `nonce` varchar(100) DEFAULT NULL,
  `gasPrice` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`txHash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
