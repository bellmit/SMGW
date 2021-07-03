/*
 Navicat Premium Data Transfer

 Source Server         : 120.79.11.146
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : 120.79.11.146:27071
 Source Schema         : sms

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 08/06/2021 09:22:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_account`;
CREATE TABLE `tb_account`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `extno` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接入号',
  `price` decimal(18, 3) NULL DEFAULT NULL COMMENT '单价',
  `balance` decimal(18, 3) NULL DEFAULT NULL COMMENT '余额',
  `alertBalance` decimal(18, 3) NULL DEFAULT NULL COMMENT '余额告警',
  `alertPhone` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知手机号 逗号分割',
  `accountStatus` int(0) NULL DEFAULT NULL COMMENT '账号是否启用0未启用 1启用',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(0) NULL DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `updateUserId` int(0) NULL DEFAULT NULL COMMENT '修改人',
  `isDelete` int(0) NULL DEFAULT NULL,
  `sendTime` datetime(0) NULL DEFAULT NULL COMMENT '最近发送时间',
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip端口',
  data
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_sendtime`(`sendTime`) USING BTREE COMMENT '最后发送时间'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_account
-- ----------------------------
INSERT INTO `tb_account` VALUES (2, '220147', 'SZdWXj', '10690', NULL, 4120.000, 5.000, '', 1, '2021-06-05 15:48:37', 1, NULL, NULL, 0, '2021-06-07 16:16:25');

-- ----------------------------
-- Table structure for tb_attachment
-- ----------------------------
DROP TABLE IF EXISTS `tb_attachment`;
CREATE TABLE `tb_attachment`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件大小 单位字节',
  `file_path` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件存储路径',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` int(0) NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for tb_data_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_data_operation_log`;
CREATE TABLE `tb_data_operation_log`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '日志id',
  `create_user_id` int(0) NULL DEFAULT NULL COMMENT '创建人id',
  `create_user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `system_flag` int(0) NULL DEFAULT NULL COMMENT '系统标识(1:开销户；2：运营管理平台)',
  `module_flag` int(0) NULL DEFAULT NULL COMMENT '模块标识',
  `bean_id` int(0) NULL DEFAULT NULL COMMENT 'bean_id',
  `action_type` int(0) NULL DEFAULT NULL COMMENT '操作类型(1:修改；2:新增；3:删除)',
  `is_delete` int(0) NULL DEFAULT 0 COMMENT '是否删除(0:未删除；1:已删除)',
  `operation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_data_log_bean_id`(`bean_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1652 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据操作日志表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_data_operation_log
-- ----------------------------
INSERT INTO `tb_data_operation_log` VALUES (1632, 1, 'admin', '2021-05-08 10:05:26', NULL, 0, NULL, 5, 0, '修改密码');
INSERT INTO `tb_data_operation_log` VALUES (1633, 1, 'admin', '2021-05-08 10:06:20', NULL, 0, NULL, 5, 0, '修改密码');
INSERT INTO `tb_data_operation_log` VALUES (1634, 1, 'admin', '2021-05-08 10:06:28', NULL, 0, NULL, 5, 0, '修改密码');
INSERT INTO `tb_data_operation_log` VALUES (1635, 1, 'admin', '2021-05-10 10:06:48', NULL, 0, NULL, 5, 0, '角色授权');
INSERT INTO `tb_data_operation_log` VALUES (1636, 1, 'admin', '2021-05-10 10:10:26', NULL, 0, NULL, 0, 0, '新增角色：bb');
INSERT INTO `tb_data_operation_log` VALUES (1637, 1, 'admin', '2021-05-10 10:10:31', NULL, 0, NULL, 1, 0, '修改角色：bbcccc');
INSERT INTO `tb_data_operation_log` VALUES (1638, 1, 'admin', '2021-05-10 10:10:35', NULL, 0, NULL, 5, 0, '角色授权');
INSERT INTO `tb_data_operation_log` VALUES (1639, 1, 'admin', '2021-05-10 10:10:45', NULL, 0, NULL, 0, 0, '新增角色：b');
INSERT INTO `tb_data_operation_log` VALUES (1640, 1, 'admin', '2021-05-10 10:10:49', NULL, 0, NULL, 5, 0, '角色授权');
INSERT INTO `tb_data_operation_log` VALUES (1641, 1, 'admin', '2021-05-10 10:11:08', NULL, 0, NULL, 0, 0, '新增用户：aaaaaa');
INSERT INTO `tb_data_operation_log` VALUES (1642, 1, 'admin', '2021-05-10 10:11:16', NULL, 0, NULL, 5, 0, '重置密码：null');
INSERT INTO `tb_data_operation_log` VALUES (1643, 1, 'admin', '2021-05-27 21:47:15', NULL, 0, NULL, 5, 0, '角色授权');
INSERT INTO `tb_data_operation_log` VALUES (1644, 1, 'admin', '2021-05-31 19:39:52', NULL, 0, NULL, 5, 0, '角色授权');
INSERT INTO `tb_data_operation_log` VALUES (1645, 1, 'admin', '2021-06-05 13:55:15', NULL, 0, NULL, 0, 0, '新增用户：zhangsan');
INSERT INTO `tb_data_operation_log` VALUES (1646, 1, 'admin', '2021-06-05 13:57:18', NULL, 0, NULL, 5, 0, '修改用户：zhangsan');
INSERT INTO `tb_data_operation_log` VALUES (1647, 1, 'admin', '2021-06-05 14:58:17', NULL, 0, NULL, 0, 0, '新增角色：普通用户');
INSERT INTO `tb_data_operation_log` VALUES (1648, 1, 'admin', '2021-06-05 14:58:41', NULL, 0, NULL, 5, 0, '角色授权');
INSERT INTO `tb_data_operation_log` VALUES (1649, 1, 'admin', '2021-06-05 15:35:12', NULL, 0, NULL, 0, 0, '新增用户：wang');
INSERT INTO `tb_data_operation_log` VALUES (1650, 1, 'admin', '2021-06-05 15:59:51', NULL, 0, NULL, 5, 0, '角色授权');
INSERT INTO `tb_data_operation_log` VALUES (1651, 1, 'admin', '2021-06-07 18:29:26', NULL, 0, NULL, 5, 0, '角色授权');

-- ----------------------------
-- Table structure for tb_group
-- ----------------------------
DROP TABLE IF EXISTS `tb_group`;
CREATE TABLE `tb_group`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `groupName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分组名称',
  `createTime` datetime(0) NULL DEFAULT NULL,
  `createUserId` int(0) NULL DEFAULT NULL,
  `updateTime` datetime(0) NULL DEFAULT NULL,
  `updateUserId` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_group
-- ----------------------------
INSERT INTO `tb_group` VALUES (1, '同学', '2021-05-27 20:58:57', 1, NULL, NULL);
INSERT INTO `tb_group` VALUES (2, '分组1', '2021-06-06 20:07:11', 42, NULL, NULL);

-- ----------------------------
-- Table structure for tb_group_member
-- ----------------------------
DROP TABLE IF EXISTS `tb_group_member`;
CREATE TABLE `tb_group_member`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `groupId` int(0) NULL DEFAULT NULL COMMENT '通讯录分组id',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `birthday` datetime(0) NULL DEFAULT NULL COMMENT '生日',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系地址',
  `company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司',
  `qq` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'qq',
  `memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(0) NULL DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `updateUserId` int(0) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_groupid`(`groupId`) USING BTREE COMMENT '组id'
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_group_member
-- ----------------------------
INSERT INTO `tb_group_member` VALUES (1, 1, '13500001111', '张三', NULL, '', '', '', '', '2021-05-27 20:59:10', 1, NULL, NULL);
INSERT INTO `tb_group_member` VALUES (2, 1, '13500001111', '李思思', NULL, '', '', '', '', '2021-05-27 20:59:26', 1, NULL, NULL);
INSERT INTO `tb_group_member` VALUES (3, 2, '13500001111', '111', '2021-06-06 08:00:00', '', '', '', '', '2021-06-06 20:08:39', 42, NULL, NULL);

-- ----------------------------
-- Table structure for tb_ip_white
-- ----------------------------
DROP TABLE IF EXISTS `tb_ip_white`;
CREATE TABLE `tb_ip_white`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `visit_count` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_logs
-- ----------------------------
DROP TABLE IF EXISTS `tb_logs`;
CREATE TABLE `tb_logs`  (
  `log_xh` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '序号',
  `log_nr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `log_tjr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '添加人',
  `log_tjsj` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`log_xh`) USING BTREE,
  INDEX `idx_logs_tjsj`(`log_tjsj`) USING BTREE COMMENT '统计时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_logs
-- ----------------------------
INSERT INTO `tb_logs` VALUES ('402880e879e7263e0179e73339ac0000', '创建IP白名单：1.2.3.4', '管理员', '2021-06-07 23:59:25');
INSERT INTO `tb_logs` VALUES ('402880e879e7263e0179e73351e00001', '批量删除ip白名单，ids:1', '管理员', '2021-06-07 23:59:31');
INSERT INTO `tb_logs` VALUES ('402880e879e7263e0179e7341caa0002', '导出短信明细', '管理员', '2021-06-08 00:00:23');
INSERT INTO `tb_logs` VALUES ('4028b88179e5ff320179e6009f1f0000', '导出短信明细', '管理员', '2021-06-07 18:24:31');

-- ----------------------------
-- Table structure for tb_notify
-- ----------------------------
DROP TABLE IF EXISTS `tb_notify`;
CREATE TABLE `tb_notify`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容 富文本内容',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `createName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发件人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_notify_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_notify_user`;
CREATE TABLE `tb_notify_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `notifyId` int(0) NULL DEFAULT NULL COMMENT '公告id',
  `userId` int(0) NULL DEFAULT NULL COMMENT '接收人id',
  `isRead` int(0) NULL DEFAULT NULL COMMENT '是否已读',
  `isDelete` int(0) NULL DEFAULT NULL COMMENT '是否删除',
  `readTime` datetime(0) NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_op_menu_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_op_menu_info`;
CREATE TABLE `tb_op_menu_info`  (
  `menu_id` int(0) NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `menu_icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `menu_index` int(0) NULL DEFAULT NULL COMMENT '菜单排序',
  `menu_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单路径',
  `menu_level` int(0) NULL DEFAULT NULL COMMENT '菜单级别 1 父级 ，2 子级',
  `menu_parent_id` int(0) NULL DEFAULT NULL COMMENT '父级菜单id',
  `menu_status` int(0) NULL DEFAULT NULL COMMENT '菜单状态',
  `menu_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标记菜单选中状态',
  `is_jump_manage` int(0) NULL DEFAULT NULL COMMENT '是否跳manage 0 不跳，1 跳',
  `request_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '跳转manage类型',
  PRIMARY KEY (`menu_id`) USING BTREE,
  UNIQUE INDEX `menu_code`(`menu_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台菜单表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_op_menu_info
-- ----------------------------
INSERT INTO `tb_op_menu_info` VALUES (1, '首页', 'fa-home', 1, 'main', 1, 0, 1, 'main', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (2, '账号资料', 'fa-folder-open-o', 2, '', 1, 0, 1, 'zhcl', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (3, '短信发送', 'fa-commenting-o', 3, NULL, 1, 0, 1, 'dxfs', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (4, '任务管理', 'fa-tasks', 4, NULL, 1, 0, 1, 'rwgl', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (5, '报表中心', 'fa-fighter-jet', 5, NULL, 1, 0, 1, 'sjrz', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (6, '相关协议文档', 'fa-sticky-note-o', 6, NULL, 1, 0, 0, 'xgxywd', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (7, '信息安全告知', 'fa-bell-o', 8, 'publicNotify', 1, 0, 1, 'xxaqgz', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (8, '系统设置', 'fa-wrench', 7, NULL, 1, 0, 1, 'xtsz', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (9, '角色管理', NULL, 3, 'platrole/index', 2, 8, 1, 'jsgl', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (10, '用户管理', NULL, 4, 'platManager/index', 2, 8, 1, 'yhgl', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (11, '操作日志', NULL, 6, 'logs/list', 2, 8, 1, 'czrz', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (12, '账号信息', NULL, 10, 'platManager/accountInfo', 2, 2, 1, 'zhcl-zhxx', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (14, '账号充值', NULL, 30, 'wallet/pay', 2, 2, 0, 'zhcl-zhcz', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (16, '充值消费记录', NULL, 50, 'walletLog/walletLogList', 2, 2, 1, 'zhcl-czxfjl', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (22, '短信模板', NULL, 50, 'smsTemplate/smsTemplateList', 2, 3, 1, 'dxfs-dxmb', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (23, '通讯录', NULL, 60, '/group/index', 2, 3, 1, 'dxfs-txl', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (24, '正在发送任务', NULL, 10, 'smsTask/sendingList', 2, 4, 1, 'rwgl-fsz', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (25, '发送完成任务', NULL, 20, 'smsTask/completeList', 2, 4, 1, 'rwgl-ywc', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (27, '明细查询', NULL, 10, 'sms/smsLogList', 2, 5, 1, 'sjrz-xxrz', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (29, '数据统计', NULL, 30, 'sms/smsDateList', 2, 5, 1, 'sjrz-sjtj', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (30, 'SMS57协议', NULL, 10, 'smgw/sms57', 2, 6, 1, 'sms57', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (31, '通讯录', NULL, 10, 'groupMember/groupMemberList', 3, 23, 1, 'txlList', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (32, '分组', NULL, 20, 'group/groupList', 3, 23, 1, 'txlTypeList', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (33, '系统消息', 'fa-exclamation-triangle', 9, 'notify/list', 1, 0, 1, 'notify', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (34, '短信账号管理', NULL, 20, 'account/accountList', 2, 8, 1, 'account', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (35, '在线发送', NULL, 1, 'smsTask/smsTaskForm', 2, 3, 1, 'smsTask', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (36, '模板审批', NULL, 30, 'smsTemplate/smsTemplateApproveList', 2, 8, 0, 'approveTemplate', 0, NULL);
INSERT INTO `tb_op_menu_info` VALUES (37, 'IP白名单', NULL, 40, '/ip/white/list', 2, 8, 1, 'ipwhite', 0, NULL);

-- ----------------------------
-- Table structure for tb_op_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_op_menu_role`;
CREATE TABLE `tb_op_menu_role`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `menu_id` int(0) NULL DEFAULT NULL,
  `role_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '平台菜单角色关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_op_menu_role
-- ----------------------------
INSERT INTO `tb_op_menu_role` VALUES (1, 1, 1);
INSERT INTO `tb_op_menu_role` VALUES (5, 5, 1);
INSERT INTO `tb_op_menu_role` VALUES (6, 6, 1);
INSERT INTO `tb_op_menu_role` VALUES (7, 7, 1);
INSERT INTO `tb_op_menu_role` VALUES (8, 8, 1);
INSERT INTO `tb_op_menu_role` VALUES (9, 9, 1);
INSERT INTO `tb_op_menu_role` VALUES (10, 10, 1);
INSERT INTO `tb_op_menu_role` VALUES (11, 11, 1);
INSERT INTO `tb_op_menu_role` VALUES (27, 27, 1);
INSERT INTO `tb_op_menu_role` VALUES (29, 29, 1);
INSERT INTO `tb_op_menu_role` VALUES (30, 30, 1);
INSERT INTO `tb_op_menu_role` VALUES (57, 33, 1);
INSERT INTO `tb_op_menu_role` VALUES (58, 34, 1);
INSERT INTO `tb_op_menu_role` VALUES (71, 32, 56);
INSERT INTO `tb_op_menu_role` VALUES (72, 1, 56);
INSERT INTO `tb_op_menu_role` VALUES (73, 33, 56);
INSERT INTO `tb_op_menu_role` VALUES (74, 2, 56);
INSERT INTO `tb_op_menu_role` VALUES (75, 3, 56);
INSERT INTO `tb_op_menu_role` VALUES (76, 35, 56);
INSERT INTO `tb_op_menu_role` VALUES (77, 4, 56);
INSERT INTO `tb_op_menu_role` VALUES (78, 5, 56);
INSERT INTO `tb_op_menu_role` VALUES (79, 7, 56);
INSERT INTO `tb_op_menu_role` VALUES (80, 12, 56);
INSERT INTO `tb_op_menu_role` VALUES (81, 14, 56);
INSERT INTO `tb_op_menu_role` VALUES (82, 16, 56);
INSERT INTO `tb_op_menu_role` VALUES (83, 22, 56);
INSERT INTO `tb_op_menu_role` VALUES (84, 23, 56);
INSERT INTO `tb_op_menu_role` VALUES (85, 24, 56);
INSERT INTO `tb_op_menu_role` VALUES (86, 25, 56);
INSERT INTO `tb_op_menu_role` VALUES (87, 27, 56);
INSERT INTO `tb_op_menu_role` VALUES (88, 29, 56);
INSERT INTO `tb_op_menu_role` VALUES (89, 31, 56);
INSERT INTO `tb_op_menu_role` VALUES (90, 37, 1);

-- ----------------------------
-- Table structure for tb_op_menu_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_op_menu_user`;
CREATE TABLE `tb_op_menu_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `menu_id` int(0) NULL DEFAULT NULL,
  `user_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台用户菜单关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for tb_plat_group
-- ----------------------------
DROP TABLE IF EXISTS `tb_plat_group`;
CREATE TABLE `tb_plat_group`  (
  `group_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门名称',
  `parent_group_id` int(0) NULL DEFAULT NULL COMMENT '父级部门ID',
  `group_code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门编码',
  `group_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门层级路径',
  PRIMARY KEY (`group_id`) USING BTREE,
  UNIQUE INDEX `group_code`(`group_code`) USING BTREE,
  UNIQUE INDEX `group_path`(`group_path`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台部门表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_plat_group
-- ----------------------------
INSERT INTO `tb_plat_group` VALUES (1, '测试', NULL, 'ROOT', ',1,');

-- ----------------------------
-- Table structure for tb_plat_group_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_plat_group_user`;
CREATE TABLE `tb_plat_group_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id` int(0) NOT NULL COMMENT '部门ID',
  `user_id` int(0) NOT NULL COMMENT '人员ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台部门人员中间表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for tb_plat_manager
-- ----------------------------
DROP TABLE IF EXISTS `tb_plat_manager`;
CREATE TABLE `tb_plat_manager`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `login_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录名',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nick_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称(代理商用户显示代理商名称，内部员工显示员工姓名)',
  `state` int(0) NULL DEFAULT NULL COMMENT '状态(100,启用；200禁用)',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` int(0) NULL DEFAULT NULL COMMENT '创建人ID',
  `creater_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `is_sub_account` int(0) NULL DEFAULT NULL COMMENT '是否子账号(0和null不是, 1是)',
  `company_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `last_login_time` datetime(0) NULL DEFAULT NULL,
  `price` decimal(18, 3) NULL DEFAULT NULL,
  `isDelete` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台用户' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_plat_manager
-- ----------------------------
INSERT INTO `tb_plat_manager` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 100, '2018-04-13 16:38:16', NULL, NULL, '15000000001', 0, 'd95ff8dc08b8415ca1820fc77e22c55c', '2021-06-08 00:01:10', 0.010, 0);
INSERT INTO `tb_plat_manager` VALUES (40, 'aaaaaa', 'e10adc3949ba59abbe56e057f20f883e', 'aaaaaaa', 200, '2021-05-10 10:11:08', 1, '管理员', '15000000002', 0, NULL, '2021-05-10 10:12:28', 0.010, 1);
INSERT INTO `tb_plat_manager` VALUES (41, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 100, '2021-06-05 13:55:15', 1, '管理员', '15000000002', 0, NULL, NULL, 0.020, 0);
INSERT INTO `tb_plat_manager` VALUES (42, 'wang', 'e10adc3949ba59abbe56e057f20f883e', '小王', 100, '2021-06-05 15:35:11', 1, '管理员', '13511112222', 0, NULL, '2021-06-07 20:59:14', 0.010, 0);

-- ----------------------------
-- Table structure for tb_plat_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_plat_role`;
CREATE TABLE `tb_plat_role`  (
  `role_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `module_codes` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块代码',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台角色' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_plat_role
-- ----------------------------
INSERT INTO `tb_plat_role` VALUES (1, 'ADMIN', '管理员', NULL);
INSERT INTO `tb_plat_role` VALUES (56, 'common', '普通用户', NULL);

-- ----------------------------
-- Table structure for tb_plat_role_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_plat_role_user`;
CREATE TABLE `tb_plat_role_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(0) NOT NULL COMMENT '角色ID',
  `user_id` int(0) NOT NULL COMMENT '人员ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 394 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台角色人员中间表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_plat_role_user
-- ----------------------------
INSERT INTO `tb_plat_role_user` VALUES (352, 1, 1);
INSERT INTO `tb_plat_role_user` VALUES (392, 1, 41);
INSERT INTO `tb_plat_role_user` VALUES (393, 56, 42);

-- ----------------------------
-- Table structure for tb_sms
-- ----------------------------
DROP TABLE IF EXISTS `tb_sms`;
CREATE TABLE `tb_sms`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `taskId` int(0) NULL DEFAULT NULL COMMENT '任务id',
  `batchId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次id',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短信内容',
  `sendResult` int(0) NULL DEFAULT NULL COMMENT '发送结果',
  `sendStatus` int(0) NULL DEFAULT NULL COMMENT '0待发送 1发送成功 2发送失败3已终止 4发送中',
  `sendTime` datetime(0) NULL DEFAULT NULL COMMENT '发送时间',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(0) NULL DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `updateUserId` int(0) NULL DEFAULT NULL COMMENT '修改人',
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `extno` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接入号',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `mid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `memo` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sendStat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `
idx_sms_taskId`(`taskId`) USING BTREE COMMENT '任务id',
  INDEX `idx_sms_batchId`(`batchId`) USING BTREE COMMENT '批次id',
  INDEX `idx_sms_sendStatus`(`sendStatus`) USING BTREE COMMENT '发送状态',
  INDEX `idx_sms_sendTime`(`sendTime`) USING BTREE COMMENT '发送时间',
  INDEX `idx_sms_mid`(`mid`) USING BTREE COMMENT '消息id'
) ENGINE = InnoDB AUTO_INCREMENT = 94 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sms_batch
-- ----------------------------
DROP TABLE IF EXISTS `tb_sms_batch`;
CREATE TABLE `tb_sms_batch`  (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `taskId` int(0) NULL DEFAULT NULL COMMENT '任务id',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板内容',
  `sendStatus` int(0) NULL DEFAULT NULL COMMENT '发送状态',
  `sendResult` int(0) NULL DEFAULT NULL COMMENT '服务器返回状态',
  `sendTime` datetime(0) NULL DEFAULT NULL COMMENT '发送时间',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(0) NULL DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `updateUserId` int(0) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_smsbatch_taskId`(`taskId`) USING BTREE COMMENT '任务id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_sms_batch
-- ----------------------------
INSERT INTO `tb_sms_batch` VALUES ('0bc520fca3ed451abaa030788cb973d5', 26, '纵有千古 横有八荒 前途似海 来日方长', 4, 0, '2021-06-05 18:40:55', '2021-06-05 18:40:55', NULL, '2021-06-05 18:40:56', NULL);
INSERT INTO `tb_sms_batch` VALUES ('0f891f6599094733b421b04f2da6e575', 33, '纵有千古 横有八荒 前途似海 来日方长', 4, 0, '2021-06-07 16:14:40', '2021-06-07 16:14:40', NULL, '2021-06-07 16:14:40', NULL);
INSERT INTO `tb_sms_batch` VALUES ('279fb28cb6654fe58d28f722a7d3e5ef', 22, '纵有千古 横有八荒 前途似海 来日方长', NULL, 0, '2021-06-05 18:32:15', '2021-06-05 18:32:15', NULL, '2021-06-05 18:32:15', NULL);
INSERT INTO `tb_sms_batch` VALUES ('28f233bf1cb04ab0af4fde708b531e30', 27, '纵有千古 横有八荒 前途似海 来日方长', 4, 0, '2021-06-05 18:56:31', '2021-06-05 18:56:31', NULL, '2021-06-05 18:56:31', NULL);
INSERT INTO `tb_sms_batch` VALUES ('6af094b70f174ebeaa441decb645af8c', 28, '纵有千古 横有八荒 前途似海 来日方长', 4, 0, '2021-06-06 10:49:01', '2021-06-06 10:49:01', NULL, '2021-06-06 10:49:01', NULL);
INSERT INTO `tb_sms_batch` VALUES ('f390ea5480c8411ca11b3af2e8a52f99', 34, '纵有千古 横有八荒 前途似海 来日方长', 4, 0, '2021-06-07 16:16:25', '2021-06-07 16:16:25', NULL, '2021-06-07 16:16:25', NULL);

-- ----------------------------
-- Table structure for tb_sms_task
-- ----------------------------
DROP TABLE IF EXISTS `tb_sms_task`;
CREATE TABLE `tb_sms_task`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板内容',
  `sendStatus` int(0) NULL DEFAULT NULL COMMENT '0待发送 1发送成功 2发送失败3已终止 4发送中',
  `sendTime` datetime(0) NULL DEFAULT NULL COMMENT '发送时间',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(0) NULL DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `updateUserId` int(0) NULL DEFAULT NULL COMMENT '修改人',
  `contentType` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_smstask_sendStatus`(`sendStatus`) USING BTREE COMMENT '任务状态'
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_sms_task
-- ----------------------------
INSERT INTO `tb_sms_task` VALUES (16, '你好，您的验证码是${B}.', 1, '2021-05-27 22:45:19', '2021-05-27 22:46:44', 1, '2021-05-27 22:46:48', 1, NULL);
INSERT INTO `tb_sms_task` VALUES (17, NULL, -1, NULL, '2021-05-30 10:54:42', 1, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (18, '你好，您的验证码是${B}.', 1, '2021-05-30 10:56:42', '2021-05-30 10:56:31', 1, '2021-05-30 10:56:43', 1, 2);
INSERT INTO `tb_sms_task` VALUES (19, NULL, -1, NULL, '2021-06-05 17:28:11', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (20, NULL, -1, NULL, '2021-06-05 17:30:54', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (21, '纵有千古 横有八荒 前途似海 来日方长', 1, '2021-06-05 17:31:07', '2021-06-05 17:31:18', 42, '2021-06-05 17:31:56', 42, 1);
INSERT INTO `tb_sms_task` VALUES (22, '纵有千古 横有八荒 前途似海 来日方长', 1, '2021-06-05 18:31:51', '2021-06-05 18:32:06', 42, '2021-06-05 18:32:13', 42, 1);
INSERT INTO `tb_sms_task` VALUES (23, NULL, -1, NULL, '2021-06-05 18:39:00', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (24, NULL, -1, NULL, '2021-06-05 18:39:49', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (25, NULL, -1, NULL, '2021-06-05 18:40:03', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (26, '纵有千古 横有八荒 前途似海 来日方长', 1, '2021-06-05 18:40:25', '2021-06-05 18:40:42', 42, '2021-06-05 18:40:50', 42, 1);
INSERT INTO `tb_sms_task` VALUES (27, '纵有千古 横有八荒 前途似海 来日方长', 1, '2021-06-05 18:56:03', '2021-06-05 18:56:22', 42, '2021-06-05 18:56:27', 42, 1);
INSERT INTO `tb_sms_task` VALUES (28, '纵有千古 横有八荒 前途似海 来日方长', 1, '2021-06-06 10:48:40', '2021-06-06 10:48:54', 42, '2021-06-06 10:48:57', 42, 1);
INSERT INTO `tb_sms_task` VALUES (29, NULL, -1, NULL, '2021-06-06 20:09:37', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (30, NULL, -1, NULL, '2021-06-07 09:21:32', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (31, NULL, -1, NULL, '2021-06-07 09:59:11', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (32, NULL, -1, NULL, '2021-06-07 10:05:12', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (33, '纵有千古 横有八荒 前途似海 来日方长', 1, '2021-06-07 16:13:18', '2021-06-07 16:14:30', 42, '2021-06-07 16:14:36', 42, 1);
INSERT INTO `tb_sms_task` VALUES (34, '纵有千古 横有八荒 前途似海 来日方长', 1, '2021-06-07 16:16:02', '2021-06-07 16:16:17', 42, '2021-06-07 16:16:23', 42, 1);
INSERT INTO `tb_sms_task` VALUES (35, NULL, -1, NULL, '2021-06-07 17:17:56', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (36, NULL, -1, NULL, '2021-06-07 17:19:02', 42, NULL, NULL, 1);
INSERT INTO `tb_sms_task` VALUES (37, NULL, -1, NULL, '2021-06-07 17:38:38', 42, NULL, NULL, 1);

-- ----------------------------
-- Table structure for tb_sms_template
-- ----------------------------
DROP TABLE IF EXISTS `tb_sms_template`;
CREATE TABLE `tb_sms_template`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板内容',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `createUserId` int(0) NULL DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `updateUserId` int(0) NULL DEFAULT NULL COMMENT '修改人',
  `approveStatus` int(0) NULL DEFAULT NULL COMMENT '审批状态 0待审批 1审批通过 2审批不通过',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `memo` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `smsSize` int(0) NULL DEFAULT NULL COMMENT '字数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_sms_template
-- ----------------------------
INSERT INTO `tb_sms_template` VALUES (1, '你好，您的验证码是${B}.', '2021-05-23 16:17:27', 1, '2021-05-31 19:37:36', 1, 2, '短信验证码', '不行', 14);
INSERT INTO `tb_sms_template` VALUES (2, 'bbbbb', '2021-05-31 19:35:50', 1, '2021-05-31 19:38:27', 1, 1, 'aaa', 'abcccc', 5);
INSERT INTO `tb_sms_template` VALUES (3, 'ddd', '2021-05-31 19:38:15', 1, NULL, NULL, 0, 'cc', NULL, 3);
INSERT INTO `tb_sms_template` VALUES (4, '纵有千古 横有八荒 前途似海 来日方长', '2021-06-05 16:15:54', 42, NULL, NULL, 1, '模板测试', NULL, 19);

-- ----------------------------
-- Table structure for tb_wallet
-- ----------------------------
DROP TABLE IF EXISTS `tb_wallet`;
CREATE TABLE `tb_wallet`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `userId` int(0) NULL DEFAULT NULL,
  `money` decimal(18, 3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_wallet_userId`(`userId`) USING BTREE COMMENT '用户id'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_wallet
-- ----------------------------
INSERT INTO `tb_wallet` VALUES (1, 1, 30.010);
INSERT INTO `tb_wallet` VALUES (2, 41, 0.000);
INSERT INTO `tb_wallet` VALUES (3, 42, 3.000);
INSERT INTO `tb_wallet` VALUES (4, 40, 10.000);

-- ----------------------------
-- Table structure for tb_wallet_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_wallet_log`;
CREATE TABLE `tb_wallet_log`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `userId` int(0) NULL DEFAULT NULL,
  `walletId` int(0) NULL DEFAULT NULL,
  `money` decimal(18, 3) NULL DEFAULT NULL,
  `oldMoney` decimal(18, 3) NULL DEFAULT NULL,
  `memo` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_wallet_userId`(`userId`) USING BTREE COMMENT '用户id',
  INDEX `idx_wallet_walletId`(`walletId`) USING BTREE COMMENT '钱包id'
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_wallet_log
-- ----------------------------
INSERT INTO `tb_wallet_log` VALUES (11, 42, 3, 1.000, 2.000, '小王充值了1元', '2021-06-07 17:45:52');

SET FOREIGN_KEY_CHECKS = 1;
