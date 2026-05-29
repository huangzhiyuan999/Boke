-- ============================================
-- BoKe 个人博客数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS boke DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE boke;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_color VARCHAR(100) DEFAULT 'linear-gradient(135deg, #5B8C5A, #7EC8A8)',
    avatar_url VARCHAR(500),
    bio VARCHAR(500),
    role ENUM('admin', 'user') DEFAULT 'user',
    status ENUM('active', 'muted', 'banned') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. 文章/公告表
-- ============================================
CREATE TABLE IF NOT EXISTS posts (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    author_id INT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(500),
    cover VARCHAR(500),
    content LONGTEXT,
    post_type ENUM('post', 'announcement') DEFAULT 'post',
    view_count INT UNSIGNED DEFAULT 0,
    is_published TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. 标签表
-- ============================================
CREATE TABLE IF NOT EXISTS tags (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. 文章-标签关联表
-- ============================================
CREATE TABLE IF NOT EXISTS post_tags (
    post_id INT UNSIGNED NOT NULL,
    tag_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 5. 动态/推荐流表
-- ============================================
CREATE TABLE IF NOT EXISTS feed_items (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    author_id INT UNSIGNED NOT NULL,
    content TEXT NOT NULL,
    topic VARCHAR(100),
    like_count INT UNSIGNED DEFAULT 0,
    comment_count INT UNSIGNED DEFAULT 0,
    repost_count INT UNSIGNED DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 6. 动态图片表
-- ============================================
CREATE TABLE IF NOT EXISTS feed_images (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    feed_id INT UNSIGNED NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    sort_order INT UNSIGNED DEFAULT 0,
    FOREIGN KEY (feed_id) REFERENCES feed_items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 7. 动态点赞表
-- ============================================
CREATE TABLE IF NOT EXISTS feed_likes (
    feed_id INT UNSIGNED NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (feed_id, user_id),
    FOREIGN KEY (feed_id) REFERENCES feed_items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 8. 动态评论表
-- ============================================
CREATE TABLE IF NOT EXISTS feed_comments (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    feed_id INT UNSIGNED NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (feed_id) REFERENCES feed_items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 9. 用户关注表
-- ============================================
CREATE TABLE IF NOT EXISTS follows (
    follower_id INT UNSIGNED NOT NULL,
    followed_id INT UNSIGNED NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 10. 留言板表
-- ============================================
CREATE TABLE IF NOT EXISTS messages (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED,
    guest_name VARCHAR(50),
    content TEXT NOT NULL,
    is_visible TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 11. 站点配置表
-- ============================================
CREATE TABLE IF NOT EXISTS site_config (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 12. 访问统计表
-- ============================================
CREATE TABLE IF NOT EXISTS site_visits (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    page_path VARCHAR(255),
    visitor_ip VARCHAR(45),
    user_id INT UNSIGNED,
    visited_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 种子数据
-- ============================================

-- 管理员和测试用户 (密码都是明文对应的bcrypt: "admin", "user123")
-- bcrypt hash for "admin": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh
INSERT INTO users (username, email, password_hash, avatar_color, bio, role, status) VALUES
('admin', 'admin@blog.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'linear-gradient(135deg, #5B8C5A, #7EC8A8)', '博客管理员', 'admin', 'active'),
('user', 'user@blog.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'linear-gradient(135deg, #667eea, #764ba2)', '普通用户', 'user', 'active'),
('小明', 'xiaoming@qq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'linear-gradient(135deg, #4facfe, #00f2fe)', '热爱前端开发', 'user', 'active'),
('程序猿', 'dev@163.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'linear-gradient(135deg, #f093fb, #f5576c)', '全栈工程师', 'user', 'muted'),
('广告账号', 'spam@xxx.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'linear-gradient(135deg, #a18cd1, #fbc2eb)', '被封禁账号', 'user', 'banned'),
('路人甲', 'passerby@qq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'linear-gradient(135deg, #43e97b, #38f9d7)', '路过看看', 'user', 'active');

-- 站点配置
INSERT INTO site_config (config_key, config_value) VALUES
('site_name', '笔墨之间'),
('site_author', 'CokeSprite'),
('site_description', '记录学习笔记，分享技术心得'),
('github_url', 'https://github.com/huangzhiyuan999'),
('contact_email', '3189509410@qq.com');

-- 文章数据
INSERT INTO posts (author_id, title, summary, cover, content, post_type, view_count, is_published) VALUES
(1, '博客正式上线啦', '经过几周的开发，个人博客终于正式上线了。后续将持续更新前端技术相关的内容，欢迎大家留言交流。',
 'linear-gradient(135deg, #f56c6c, #e63946)',
 '## 博客上线公告\n\n经过几个星期的设计和开发，这个个人技术博客终于和大家见面了。\n\n### 关于本站\n\n本站使用 Vue 3 + Vite 构建，目前处于持续完善阶段。\n\n### 内容规划\n\n主要围绕技术开发栈，包括但不限于：\n\n- Vue.js / React 生态\n- Java / Spring Boot\n- 前端工程化\n- 性能优化实践\n\n感谢你的来访！',
 'announcement', 480, 1),

(1, '留言板功能已开放', '留言板页面已经上线，欢迎大家在留言板留下宝贵的建议和想法。',
 'linear-gradient(135deg, #4facfe, #00f2fe)',
 '## 留言板功能说明\n\n留言板功能已正式开放，你可以在留言板页面留下想说的话。\n\n### 使用方式\n\n1. 点击导航栏的「留言板」\n2. 填写昵称和内容\n3. 点击发布\n\n欢迎大家提出宝贵建议！',
 'announcement', 320, 1),

(1, 'Vue 3 Composition API 实战指南', '深入探讨 Vue 3 Composition API 的核心概念，包括 ref、reactive、computed 和 watch 的使用场景与最佳实践。',
 'linear-gradient(135deg, #667eea, #764ba2)',
 '## 什么是 Composition API\n\nComposition API 是 Vue 3 引入的一套全新的逻辑组织方式。它让我们能够更好地组织和复用代码逻辑。\n\n### ref 和 reactive\n\n`ref` 用于包装基本类型的响应式数据，`reactive` 用于包装对象类型的数据。\n\n```js\nimport { ref, reactive } from ''vue''\n\nconst count = ref(0)\nconst user = reactive({ name: ''Alice'', age: 25 })\n```\n\n### computed 计算属性\n\n`computed` 可以根据已有的响应式数据派生出新的数据，并且会自动缓存结果。\n\n```js\nimport { ref, computed } from ''vue''\n\nconst firstName = ref(''John'')\nconst lastName = ref(''Doe'')\nconst fullName = computed(() => `${firstName.value} ${lastName.value}`)\n```\n\n### watch 侦听器\n\n`watch` 可以监听响应式数据的变化并执行回调函数。\n\n## 总结\n\nComposition API 提供了更灵活的代码组织方式，使得大型组件的逻辑更加清晰。',
 'post', 1280, 1),

(1, 'CSS Grid 布局完全教程', '从入门到精通 CSS Grid 布局系统，通过实际案例掌握网格布局的强大功能。',
 'linear-gradient(135deg, #f093fb, #f5576c)',
 '## CSS Grid 简介\n\nCSS Grid 是一个二维的布局系统，可以同时处理行和列。\n\n### 创建网格\n\n```css\n.container {\n  display: grid;\n  grid-template-columns: repeat(3, 1fr);\n  grid-template-rows: auto;\n  gap: 20px;\n}\n```\n\n### 响应式布局\n\n结合 `minmax()` 和 `auto-fill`，无需媒体查询就能创建响应式布局。\n\n## 实践案例\n\nCSS Grid 的浏览器支持已经非常广泛，是现代前端开发中不可或缺的技能。',
 'post', 980, 1),

(1, 'JavaScript 异步编程从回调到 async/await', '梳理 JavaScript 异步编程的演进历程：从回调地狱到 Promise，再到优雅的 async/await 语法。',
 'linear-gradient(135deg, #4facfe, #00f2fe)',
 '## 异步编程的演进\n\nJavaScript 是单线程的语言，但通过事件循环机制实现异步操作。\n\n### Promise 链\n\n```js\nfetch(''/api/user'')\n  .then(res => res.json())\n  .then(user => fetch(`/api/posts/${user.id}`))\n  .then(res => res.json())\n  .then(posts => console.log(posts))\n```\n\n### async/await\n\nasync/await 让异步代码看起来像同步代码一样优雅。\n\n## 总结\n\n从回调到 Promise 再到 async/await，每一步都在提升代码的可读性和维护性。',
 'post', 756, 1),

(1, '前端性能优化锦囊', '分享 10 个实用的前端性能优化技巧，涵盖资源加载、渲染优化、代码分割等多个方面。',
 'linear-gradient(135deg, #43e97b, #38f9d7)',
 '## 前端性能为什么重要\n\n页面加载速度直接影响用户体验和转化率。\n\n### 1. 图片懒加载\n\n使用 `loading="lazy"` 属性实现原生懒加载。\n\n### 2. 代码分割\n\n利用动态 import 实现按需加载。\n\n### 3. 缓存策略\n\n合理设置 HTTP 缓存头，利用 Service Worker 实现离线访问。\n\n## 衡量标准\n\n使用 Lighthouse 和 WebPageTest 等工具定期检查页面性能。',
 'post', 890, 1),

(1, '构建你的第一个 RESTful API', '用 Node.js 和 Express 从零搭建 RESTful API，涵盖路由设计、中间件、错误处理和数据库连接。',
 'linear-gradient(135deg, #fa709a, #fee140)',
 '## RESTful API 设计原则\n\nREST 是一种软件架构风格，它定义了一组约束和原则。\n\n### 资源导向\n\n```\nGET    /api/posts      # 获取文章列表\nPOST   /api/posts      # 创建文章\nPUT    /api/posts/1    # 更新文章\nDELETE /api/posts/1    # 删除文章\n```\n\n### 状态码规范\n\n合理使用 HTTP 状态码让 API 更具语义。\n\n## 总结\n\n一个好的 API 设计应该简洁、直观、符合规范。',
 'post', 650, 1),

(1, 'Git 工作流最佳实践', '介绍 Git Flow、GitHub Flow 和 Trunk-Based Development 三种主流工作流。',
 'linear-gradient(135deg, #a18cd1, #fbc2eb)',
 '## 为什么需要 Git 工作流\n\n团队协作中，一个清晰的分支管理策略可以帮助减少冲突、提高效率。\n\n### GitHub Flow\n\n1. 从 main 创建功能分支\n2. 在功能分支上开发和提交\n3. 开启 Pull Request\n4. 代码评审后合并到 main\n\n### 提交信息规范\n\n使用 Conventional Commits 规范保持提交历史清晰。\n\n## 总结\n\n选择哪种工作流取决于团队规模和项目特点。',
 'post', 530, 1);

-- 标签数据
INSERT INTO tags (name, slug) VALUES
('Vue', 'vue'),
('前端', 'frontend'),
('CSS', 'css'),
('布局', 'layout'),
('JavaScript', 'javascript'),
('异步', 'async'),
('性能', 'performance'),
('优化', 'optimization'),
('Node.js', 'nodejs'),
('后端', 'backend'),
('Git', 'git'),
('工具', 'tools');

-- 文章-标签关联
-- post 3 (Vue 3): Vue, 前端
INSERT INTO post_tags (post_id, tag_id) VALUES (3, 1), (3, 2);
-- post 4 (CSS Grid): CSS, 布局
INSERT INTO post_tags (post_id, tag_id) VALUES (4, 3), (4, 4);
-- post 5 (JS异步): JavaScript, 异步
INSERT INTO post_tags (post_id, tag_id) VALUES (5, 5), (5, 6);
-- post 6 (性能): 性能, 优化
INSERT INTO post_tags (post_id, tag_id) VALUES (6, 7), (6, 8);
-- post 7 (REST API): Node.js, 后端
INSERT INTO post_tags (post_id, tag_id) VALUES (7, 9), (7, 10);
-- post 8 (Git): Git, 工具
INSERT INTO post_tags (post_id, tag_id) VALUES (8, 11), (8, 12);

-- 动态数据
INSERT INTO feed_items (author_id, content, topic, like_count, comment_count, repost_count) VALUES
(2, 'Vue 3.6 即将发布，带来了全新的响应式语法糖和编译时优化，性能提升约 20%。新版本还改进了 TypeScript 支持，开发者体验大幅提升。', 'Vue3.6发布', 1280, 356, 89),
(2, '本周科技周刊已更新。本期封面：AI 编程工具的崛起。2026年，超过60%的开发者已经在日常工作中使用 AI 辅助编程工具，这个数字还在快速增长。', '科技爱好者周刊', 3560, 892, 234),
(3, '整理了2026年最受欢迎的配色方案，从清新自然到高级商务，共30套配色。每一套都附带了实际应用案例和色值代码。适合UI设计师和前端开发者收藏。', '配色灵感', 2890, 467, 1203),
(3, '花了一个周末用 Electron + Vue 3 写了一个 Markdown 编辑器，支持实时预览、主题切换、导出PDF。开源在 GitHub 上，欢迎大家 star 和提意见～', '开源项目推荐', 920, 145, 67);

-- 动态图片
INSERT INTO feed_images (feed_id, image_url, sort_order) VALUES
(1, 'linear-gradient(135deg, #a18cd1, #fbc2eb)', 0),
(1, 'linear-gradient(135deg, #f093fb, #f5576c)', 1),
(3, 'linear-gradient(135deg, #43e97b, #38f9d7)', 0),
(3, 'linear-gradient(135deg, #fa709a, #fee140)', 1),
(3, 'linear-gradient(135deg, #a1c4fd, #c2e9fb)', 2),
(4, 'linear-gradient(135deg, #667eea, #764ba2)', 0);

-- 留言数据
INSERT INTO messages (user_id, guest_name, content, is_visible) VALUES
(2, NULL, '博客做得不错，加油！', 1),
(4, NULL, '文章写得很有深度，已收藏。', 1),
(NULL, '路人甲', '请问会出 Spring Cloud 系列的教程吗？期待！', 1);

-- 关注数据
INSERT INTO follows (follower_id, followed_id) VALUES
(2, 1),
(2, 3),
(2, 4),
(3, 1),
(3, 2);
