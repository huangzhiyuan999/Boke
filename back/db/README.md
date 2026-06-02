# 数据库初始化说明

本项目使用 MySQL 8.0。后端启动时会自动执行 `back/src/main/resources/schema.sql` 创建表结构，并由 `com.boke.config.DataInitializer` 插入演示数据。

## 初始化方式

1. 确保 MySQL 已启动。
2. 检查 `back/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/boke?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
    username: root
    password: 123456
```

3. 启动后端：

```bash
cd back
mvn spring-boot:run -DskipTests
```

首次启动会自动创建 `boke` 数据库、创建所有表，并插入演示内容。

## 表结构概览

基础内容：
- `users`：用户、角色、状态、头像资料。
- `posts`：文章和公告。
- `tags` / `post_tags`：文章标签。
- `feed_items` / `feed_images` / `feed_likes` / `feed_comments`：推荐动态、图片、点赞、评论。
- `follows`：关注关系。
- `messages`：留言板。
- `site_config`：站点配置。
- `site_visits`：访问统计。

娱乐模块：
- `entertainment_items`：商城商品、分类、价格、库存、图片、热度。
- `entertainment_games`：游戏大厅条目、标签、图片、详情背景、模拟文案。
- `entertainment_game_ranks`：游戏排行榜。
- `entertainment_events`：每日活动任务。
- `entertainment_wallets`：用户娱乐币和签到日期。
- `entertainment_event_records`：用户已完成活动记录。
- `entertainment_game_plays`：游戏模拟游玩记录。

## 演示数据

`DataInitializer` 会插入：
- 管理员和普通用户账号。
- 文章、公告、标签、推荐动态、留言、关注关系。
- 娱乐商城商品 8 条。
- 娱乐游戏 3 条和对应排行榜。
- 每日活动 4 条。
- 用户娱乐币钱包数据。

测试账号：

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | admin | qwe123 |
| 普通用户 | user | user123 |

## 老数据库升级

如果旧库中已经存在 `admin` 用户，基础演示数据不会重复插入；但当娱乐模块表为空时，启动后端仍会自动补齐娱乐模块演示数据。
