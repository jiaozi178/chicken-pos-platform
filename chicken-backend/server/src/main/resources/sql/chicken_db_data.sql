-- 只保留数据的脚本
USE chicken_db;

-- 员工数据
INSERT IGNORE INTO `employee` VALUES (1, '员工', 'root', 'e10adc3949ba59abbe56e057f20f883e', '11111111111', 0, 1, NULL, 1, 100, 100, '2025-07-15 15:38:11', '2025-07-15 15:38:11');

-- 分类数据
INSERT IGNORE INTO `category` VALUES (1, '正新炸鸡', 1, 1, 1, 1, 1, '2025-07-15 15:53:13', '2025-07-15 15:53:13');
INSERT IGNORE INTO `category` VALUES (2, '正新烧烤', 1, 2, 1, 1, 1, '2025-07-15 15:53:29', '2025-07-15 15:53:29');
INSERT IGNORE INTO `category` VALUES (3, '正新炸串', 1, 3, 1, 1, 1, '2025-07-15 15:53:45', '2025-07-15 15:53:45');
INSERT IGNORE INTO `category` VALUES (4, '单人餐', 2, 4, 1, 1, 1, '2025-07-15 17:15:53', '2025-07-15 17:21:47');
INSERT IGNORE INTO `category` VALUES (5, '双人餐', 2, 5, 1, 1, 1, '2025-07-15 17:22:01', '2025-07-15 17:22:01');
INSERT IGNORE INTO `category` VALUES (6, '正新饮料', 1, 6, 1, 1, 1, '2025-07-15 17:31:11', '2025-07-15 17:31:28');
INSERT IGNORE INTO `category` VALUES (7, '正新小食', 1, 7, 1, 1, 1, '2025-07-15 17:31:41', '2025-07-15 17:31:41');

-- 菜品数据
INSERT IGNORE INTO `dish` VALUES (10, '香辣大鸡排', '/upload/chicken_menu/cd197cd1-7fc2-4982-bd2b-68a0449af386.jpeg', '正新招牌', 12.00, 1, 1, 1, 1, '2025-07-15 16:58:14', '2025-07-15 16:58:14');
INSERT IGNORE INTO `dish` VALUES (11, '疯狂烤翅', '/upload/chicken_menu/8d9adbce-1b9d-4b71-b776-809a786a5177.jpeg', '疯狂烤翅', 12.00, 1, 2, 1, 1, '2025-07-15 16:58:53', '2025-07-15 16:58:53');
INSERT IGNORE INTO `dish` VALUES (12, '鲜嫩小牛肉串', '/upload/chicken_menu/dfe2339e-991a-482b-a7e9-d6f4ef3d07c8.jpeg', '鲜嫩小牛肉串', 12.00, 1, 3, 1, 1, '2025-07-15 16:59:17', '2025-07-15 16:59:17');
INSERT IGNORE INTO `dish` VALUES (13, '爆料鸡排（避风塘甜辣）', '/upload/chicken_menu/65e3e599-a587-4df2-8b57-69043eb71a97.jpeg', '爆料鸡排（避风塘甜辣）', 10.00, 1, 1, 1, 1, '2025-07-15 17:26:22', '2025-07-15 17:26:22');
INSERT IGNORE INTO `dish` VALUES (14, '爆料鸡排（海苔肉松）', '/upload/chicken_menu/27269b28-aa2b-487a-b76e-85f6d765edf0.jpeg', '爆料鸡排（海苔肉松）', 10.00, 1, 1, 1, 1, '2025-07-15 17:26:39', '2025-07-15 17:26:39');
INSERT IGNORE INTO `dish` VALUES (15, '爆料鸡排（双椒）', '/upload/chicken_menu/4333cd41-e2ea-46c2-9cf8-9e408c84c6d0.jpeg', '爆料鸡排（双椒）', 10.00, 1, 1, 1, 1, '2025-07-15 17:26:59', '2025-07-15 17:26:59');
INSERT IGNORE INTO `dish` VALUES (16, '香嫩大鸡肉串', '/upload/chicken_menu/850bc2ca-b1b3-4f6b-afd4-8c65cc1acb6f.jpeg', '香嫩大鸡肉串', 10.00, 1, 2, 1, 1, '2025-07-15 17:28:07', '2025-07-15 17:28:07');
INSERT IGNORE INTO `dish` VALUES (17, '正新大烤肠', '/upload/chicken_menu/4f011d60-3ced-4d09-a8e6-3bf56d806812.jpeg', '正新大烤肠', 6.00, 1, 2, 1, 1, '2025-07-15 17:28:34', '2025-07-15 17:28:34');
INSERT IGNORE INTO `dish` VALUES (18, '鸭肉串', '/upload/chicken_menu/e8d518f8-e3e1-4a40-ac53-6e6c15ee5d7a.jpeg', '鸭肉串', 10.00, 1, 2, 1, 1, '2025-07-15 17:28:57', '2025-07-15 17:28:57');
INSERT IGNORE INTO `dish` VALUES (19, '爽滑小鸡肉串', '/upload/chicken_menu/5d9de70a-25fa-4326-a8c0-5fcb905cec31.jpeg', '爽滑小鸡肉串', 10.00, 1, 3, 1, 1, '2025-07-15 17:29:42', '2025-07-15 17:29:42');
INSERT IGNORE INTO `dish` VALUES (20, '爽脆鸭肠', '/upload/chicken_menu/83108c75-0f8e-43f3-aee0-64c1af26b3e3.jpeg', '爽脆鸭肠', 10.00, 1, 3, 1, 1, '2025-07-15 17:29:58', '2025-07-15 17:29:58');
INSERT IGNORE INTO `dish` VALUES (21, '招牌火爆鱿鱼', '/upload/chicken_menu/872c0036-2f4c-4a15-948e-ffb0fbdff954.jpeg', '招牌火爆鱿鱼', 12.00, 1, 3, 1, 1, '2025-07-15 17:30:37', '2025-07-15 17:30:37');
INSERT IGNORE INTO `dish` VALUES (22, '乳茶', '/upload/chicken_menu/b488efdf-1fb7-4a05-b661-2fbacd027a23.jpeg', '乳茶', 4.00, 1, 6, 1, 1, '2025-07-15 17:32:05', '2025-07-15 17:32:05');
INSERT IGNORE INTO `dish` VALUES (23, '顺益多', '/upload/chicken_menu/d243f8aa-f47f-44ed-a735-c4ecf22cccf0.jpeg', '顺益多', 4.00, 1, 6, 1, 1, '2025-07-15 17:32:15', '2025-07-15 17:32:15');
INSERT IGNORE INTO `dish` VALUES (24, '生炸大鸡腿', '/upload/chicken_menu/456dfb7d-305b-4134-8d8d-0683fe664179.jpeg', '生炸大鸡腿', 9.90, 1, 7, 1, 1, '2025-07-15 17:32:36', '2025-07-15 17:32:36');
INSERT IGNORE INTO `dish` VALUES (25, '拉丝芝士棒', '/upload/chicken_menu/44e7702d-0678-4e62-be9d-b99e14b06f03.jpeg', '拉丝芝士棒', 6.00, 1, 7, 1, 1, '2025-07-15 17:32:50', '2025-07-15 17:32:50');
INSERT IGNORE INTO `dish` VALUES (26, '黑椒鸡块', '/upload/chicken_menu/0c9ccdc2-5da6-41d1-82b7-a8e77b6013dc.jpeg', '黑椒鸡块', 8.00, 1, 7, 1, 1, '2025-07-15 17:33:04', '2025-07-15 17:33:04');
INSERT IGNORE INTO `dish` VALUES (27, '甘梅地瓜条', '/upload/chicken_menu/5c78be78-1c92-45b2-87e8-587ec3f8754b.jpeg', '甘梅地瓜条', 10.00, 1, 7, 1, 1, '2025-07-15 17:33:19', '2025-07-15 17:33:19');
INSERT IGNORE INTO `dish` VALUES (28, '冰红茶气泡水', '/upload/chicken_menu/d5eb76d7-f061-4580-8e09-f6aa455b3bf4.jpeg', '冰红茶气泡水', 4.00, 1, 6, 1, 1, '2025-07-15 17:41:00', '2025-07-15 17:41:00');
INSERT IGNORE INTO `dish` VALUES (29, '中国功夫腿', '/upload/chicken_menu/79fe3feb-5345-4f44-a223-8384efef6c6b.jpeg', '中国功夫腿', 5.00, 1, 7, 1, 1, '2025-07-15 17:41:21', '2025-07-15 17:41:21');

-- 套餐数据
INSERT IGNORE INTO `setmeal` VALUES (1, '香辣鸡排-正新烤肠-乳茶', '/upload/setmeal/13099a15-8ef0-4301-af67-a3a8f04f48b5.jpeg', '香辣鸡排+正新烤肠+乳茶，原价26', 18.90, 1, 4, 1, 1, '2025-07-15 17:34:54', '2025-07-15 19:23:54');
INSERT IGNORE INTO `setmeal` VALUES (3, '中国功夫腿双人餐', '/upload/setmeal/08e39091-2820-43ef-a975-1580d91415a5.jpeg', '中国功夫腿*3+黑椒鸡块+饮料*2', 29.90, 1, 5, 1, 1, '2025-07-15 17:43:30', '2025-07-15 19:24:16');
INSERT IGNORE INTO `setmeal` VALUES (4, '香辣鸡排三件套', '/upload/setmeal/fbaace23-1eef-4d23-b4fa-be23b3dfb0d7.jpeg', '鸡排+烤肠+鸭肉串，原价28', 19.80, 1, 4, 1, 1, '2025-07-15 19:22:16', '2025-07-15 19:22:16');

-- 套餐菜品关联数据
INSERT IGNORE INTO `setmeal_dish` VALUES (12, '香嫩大鸡肉串', 10.00, 1, 16, 4);
INSERT IGNORE INTO `setmeal_dish` VALUES (13, '正新大烤肠', 6.00, 1, 17, 4);
INSERT IGNORE INTO `setmeal_dish` VALUES (14, '香辣大鸡排', 12.00, 1, 10, 4);
INSERT IGNORE INTO `setmeal_dish` VALUES (15, '乳茶', 4.00, 1, 22, 1);
INSERT IGNORE INTO `setmeal_dish` VALUES (16, '正新大烤肠', 6.00, 1, 17, 1);
INSERT IGNORE INTO `setmeal_dish` VALUES (17, '香辣大鸡排', 12.00, 1, 10, 1);
INSERT IGNORE INTO `setmeal_dish` VALUES (18, '乳茶', 4.00, 1, 22, 3);
INSERT IGNORE INTO `setmeal_dish` VALUES (19, '甘梅地瓜条', 10.00, 1, 27, 3);
INSERT IGNORE INTO `setmeal_dish` VALUES (20, '黑椒鸡块', 8.00, 1, 26, 3);
INSERT IGNORE INTO `setmeal_dish` VALUES (21, '冰红茶气泡水', 4.00, 1, 28, 3);
INSERT IGNORE INTO `setmeal_dish` VALUES (22, '中国功夫腿', 5.00, 1, 29, 3);