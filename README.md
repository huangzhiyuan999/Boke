# 笔墨之间 - 个人博客

基于 Vue 3 + Spring Boot 3 的全栈个人博客系统。

## 技术栈

| 层 | 技术 |
|---|------|
| 前端 | Vue 3 + Vite + Vue Router |
| 后端 | Spring Boot 3.2 + MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 认证 | Spring Security + JWT (jjwt) |
| 密码 | BCrypt |

## 功能模块

- 文章 / 公告管理（Markdown 编辑、标签系统、封面渐变色）
- 动态推荐流（发布动态、点赞、评论、图片上传）
- 用户关注（搜索用户、关注/取消、推荐关注）
- 留言板（游客/登录用户均可留言）
- 后台管理（概览统计、文章/公告 CRUD、用户管理、留言管理）
- 站点配置（站点名称、作者、GitHub、邮箱等 KV 配置）
- 访问统计

## 项目结构

```
Boke/
├── back/                          # Spring Boot 后端
│   ├── src/main/java/com/boke/
│   │   ├── BokeApplication.java   # 启动类
│   │   ├── common/                # Result / PageResult
│   │   ├── config/                # Security、CORS、JWT、数据初始化
│   │   ├── controller/            # REST 控制器 (8个)
│   │   ├── dto/                   # 请求/响应对象
│   │   ├── entity/                # 实体类 (12张表)
│   │   ├── mapper/                # MyBatis-Plus Mapper
│   │   ├── service/               # 业务逻辑
│   │   └── util/                  # JWT 工具
│   ├── src/main/resources/
│   │   ├── application.yml        # 应用配置
│   │   └── schema.sql             # 自动建表
│   └── db/init.sql                # 完整建库脚本（参考）
│
└── front/                         # Vue 3 前端
    ├── src/
    │   ├── components/            # NavBar、Footer、PostCard 等
    │   ├── views/                 # 页面组件 (10个)
    │   ├── stores/auth.js         # 认证状态管理
    │   ├── utils/api.js           # HTTP 请求封装
    │   └── router/index.js        # 路由配置
    ├── public/                    # 静态资源（图片、音乐）
    └── vite.config.js
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0（运行中，端口 3306）

### 1. 配置数据库

确保 MySQL 运行，修改 `back/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/boke?...
    username: root
    password: 123456
```

数据库 `boke` 和所有表会在首次启动时自动创建，种子数据也会自动初始化。

### 2. 启动后端

```bash
cd back
mvn spring-boot:run -DskipTests
# 或: mvn package -DskipTests && java -jar target/boke-backend-1.0.0.jar
```

后端运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
cd front
npm install
npx vite
```

前端运行在 `http://localhost:3000`。

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | qwe123 |
| 普通用户 | user | user123 |

## API 文档

认证方式：登录后获取 JWT token，请求头携带 `Authorization: Bearer <token>`。

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录 | - |
| GET | `/api/posts?type=post&sort=hot&tag=xxx` | 文章列表 | - |
| GET | `/api/posts/{id}` | 文章详情 | - |
| POST/PUT/DELETE | `/api/posts[/{id}]` | 创建/编辑/删除文章 | JWT |
| GET | `/api/tags` | 所有标签 | - |
| GET | `/api/feeds` | 动态列表 | - |
| POST | `/api/feeds` | 发布动态 | JWT |
| POST/DELETE | `/api/feeds/{id}/like` | 点赞/取消 | JWT |
| GET/POST | `/api/feeds/{id}/comments` | 评论列表/发表评论 | -/JWT |
| POST/DELETE | `/api/follows[/{id}]` | 关注/取消 | JWT |
| GET | `/api/follows/following` | 我的关注 | JWT |
| GET | `/api/follows/suggestions` | 推荐关注 | JWT |
| GET | `/api/follows/search?q=xxx` | 搜索用户 | JWT |
| GET/POST | `/api/messages` | 留言列表/发布留言 | - |
| GET/PUT | `/api/config` | 站点配置 | -/JWT |
| GET | `/api/admin/stats` | 管理统计 | JWT |
| GET/PUT/DELETE | `/api/admin/users[/{id}]` | 用户管理 | JWT |
