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
