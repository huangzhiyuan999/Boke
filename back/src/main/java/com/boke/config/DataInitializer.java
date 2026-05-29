package com.boke.config;

import com.boke.entity.*;
import com.boke.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PostMapper postMapper;
    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;
    private final MessageMapper messageMapper;
    private final SiteConfigMapper siteConfigMapper;
    private final FeedItemMapper feedItemMapper;
    private final FeedImageMapper feedImageMapper;
    private final FeedLikeMapper feedLikeMapper;
    private final FeedCommentMapper feedCommentMapper;
    private final FollowMapper followMapper;

    @Override
    @Transactional
    public void run(String... args) {
        if (userMapper.findByUsername("admin") != null) {
            log.info("Data already initialized, skipping");
            return;
        }

        log.info("Initializing seed data...");

        // ===== 1. Users =====
        User admin = createUser("admin", "admin@blog.com", "qwe123",
                "linear-gradient(135deg, #5B8C5A, #7EC8A8)", "博客管理员", "admin");
        User user = createUser("user", "user@blog.com", "user123",
                "linear-gradient(135deg, #667eea, #764ba2)", "普通用户", "user");
        User xm = createUser("小明", "xiaoming@qq.com", "123456",
                "linear-gradient(135deg, #4facfe, #00f2fe)", "热爱前端开发", "user");
        User dev = createUser("程序猿", "dev@163.com", "123456",
                "linear-gradient(135deg, #f093fb, #f5576c)", "全栈工程师", "user");
        User passerby = createUser("路人甲", "passerby@qq.com", "123456",
                "linear-gradient(135deg, #43e97b, #38f9d7)", "路过看看", "user");
        List<User> allUsers = List.of(admin, user, xm, dev, passerby);

        // ===== 2. Site config =====
        String[][] configs = {
            {"site_name", "笔墨之间"},
            {"site_author", "CokeSprite"},
            {"site_description", "记录学习笔记，分享技术心得"},
            {"github_url", "https://github.com/huangzhiyuan999"},
            {"contact_email", "3189509410@qq.com"},
        };
        for (String[] c : configs) {
            SiteConfig sc = new SiteConfig();
            sc.setConfigKey(c[0]);
            sc.setConfigValue(c[1]);
            sc.setUpdatedAt(LocalDateTime.now());
            siteConfigMapper.insert(sc);
        }

        // ===== 3. Posts + Tags =====
        String[][] postsData = {
            {"博客正式上线啦", "经过几周的开发，个人博客终于正式上线了。后续将持续更新技术内容。",
             "linear-gradient(135deg, #f56c6c, #e63946)", "announcement",
             "## 博客上线公告\n\n经过几个星期的设计和开发，这个个人技术博客终于和大家见面了。\n\n### 关于本站\n\n本站使用 Vue 3 + Vite 构建，Spring Boot 3 后端支撑，MySQL 数据库。\n\n### 内容规划\n\n- Java / Spring Boot 后端开发\n- Vue.js 前端技术\n- 数据库与缓存\n- DevOps 与部署\n\n感谢你的来访！"},
            {"留言板功能已开放", "留言板页面已经上线，欢迎大家在留言板留下宝贵的建议和想法。",
             "linear-gradient(135deg, #4facfe, #00f2fe)", "announcement",
             "## 留言板功能说明\n\n留言板功能已正式开放，你可以在留言板页面留下想说的话。\n\n欢迎大家提出宝贵建议！"},
            {"Vue 3 Composition API 实战指南", "深入探讨 Vue 3 Composition API 的核心概念，包括 ref、reactive、computed 和 watch 的使用。",
             "linear-gradient(135deg, #667eea, #764ba2)", "post",
             "## 什么是 Composition API\n\nComposition API 是 Vue 3 引入的一套全新的逻辑组织方式。\n\n### ref 和 reactive\n\n`ref` 用于包装基本类型的响应式数据，`reactive` 用于包装对象类型。\n\n```js\nimport { ref, reactive } from 'vue'\nconst count = ref(0)\nconst user = reactive({ name: 'Alice', age: 25 })\n```\n\n### computed 计算属性\n\n`computed` 可以根据已有的响应式数据派生出新的数据，自动缓存结果。\n\n## 总结\n\nComposition API 提供了更灵活的代码组织方式，使得大型组件的逻辑更加清晰。"},
            {"CSS Grid 布局完全教程", "从入门到精通 CSS Grid 布局系统，通过实际案例掌握网格布局。",
             "linear-gradient(135deg, #f093fb, #f5576c)", "post",
             "## CSS Grid 简介\n\nCSS Grid 是一个二维的布局系统，可以同时处理行和列。\n\n```css\n.container {\n  display: grid;\n  grid-template-columns: repeat(3, 1fr);\n  gap: 20px;\n}\n```\n\n### 响应式布局\n\n结合 `minmax()` 和 `auto-fill`，无需媒体查询就能创建响应式布局。\n\n## 总结\n\nCSS Grid 的浏览器支持已经非常广泛，是现代前端开发中不可或缺的技能。"},
            {"JavaScript 异步编程：从回调到 async/await", "梳理 JavaScript 异步编程的演进历程。",
             "linear-gradient(135deg, #4facfe, #00f2fe)", "post",
             "## 异步编程的演进\n\nJavaScript 是单线程的语言，但通过事件循环机制实现异步操作。\n\n### Promise\n\n```js\nfetch('/api/user')\n  .then(res => res.json())\n  .then(user => console.log(user))\n```\n\n### async/await\n\n```js\nasync function loadUser() {\n  const res = await fetch('/api/user')\n  return await res.json()\n}\n```\n\n## 总结\n\n从回调到 Promise 再到 async/await，每一步都在提升代码的可读性和维护性。"},
            {"前端性能优化锦囊", "分享 10 个实用的前端性能优化技巧，涵盖资源加载、渲染优化、代码分割等。",
             "linear-gradient(135deg, #43e97b, #38f9d7)", "post",
             "## 前端性能为什么重要\n\n页面加载速度直接影响用户体验。超过 3 秒的加载时间会导致大量用户流失。\n\n### 优化要点\n\n1. 图片懒加载：`loading=\"lazy\"`\n2. 代码分割：`const Comp = () => import('./Comp.vue')`\n3. 资源预加载：preload / prefetch\n4. 合理使用缓存策略\n\n## 总结\n\n优化是一个持续的过程，每次改进都会为用户带来更好的体验。"},
            {"构建你的第一个 RESTful API", "用 Node.js 和 Express 从零搭建 RESTful API，涵盖路由设计、中间件和错误处理。",
             "linear-gradient(135deg, #fa709a, #fee140)", "post",
             "## RESTful API 设计原则\n\n```\nGET    /api/posts      # 获取列表\nPOST   /api/posts      # 创建\nPUT    /api/posts/1    # 更新\nDELETE /api/posts/1    # 删除\n```\n\n### 状态码规范\n\n- 200 成功\n- 201 创建成功\n- 400 请求错误\n- 404 不存在\n- 500 服务器错误\n\n## 总结\n\n好的 API 设计应该简洁、直观、符合规范。"},
            {"Git 工作流最佳实践", "介绍 Git Flow、GitHub Flow 和 Trunk-Based Development 三种主流工作流。",
             "linear-gradient(135deg, #a18cd1, #fbc2eb)", "post",
             "## 为什么需要 Git 工作流\n\n团队协作中，清晰的分支管理策略可以减少冲突、提高效率。\n\n### GitHub Flow\n\n1. 从 main 创建功能分支\n2. 开发并提交\n3. 开启 Pull Request\n4. 代码评审后合并\n\n### 提交信息规范\n\n```\nfeat: 添加新功能\nfix: 修复bug\ndocs: 更新文档\n```\n\n## 总结\n\n选择适合团队规模的工作流至关重要。"},
        };

        String[][][] postTagsMapping = {
            {}, {},
            {{"Vue", "vue"}, {"前端", "frontend"}},
            {{"CSS", "css"}, {"布局", "layout"}},
            {{"JavaScript", "javascript"}, {"异步", "async"}},
            {{"性能", "performance"}, {"优化", "optimization"}},
            {{"Node.js", "nodejs"}, {"后端", "backend"}},
            {{"Git", "git"}, {"工具", "tools"}},
        };

        for (int i = 0; i < postsData.length; i++) {
            Post post = new Post();
            post.setAuthorId(admin.getId());
            post.setTitle(postsData[i][0]);
            post.setSummary(postsData[i][1]);
            post.setCover(postsData[i][2]);
            post.setContent(postsData[i][4]);
            post.setPostType(postsData[i][3]);
            post.setViewCount((long)(Math.random() * 500 + 100));
            post.setIsPublished(1);
            post.setCreatedAt(LocalDateTime.now().minusDays(postsData.length - i));
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.insert(post);

            for (String[] tagData : postTagsMapping[i]) {
                Tag tag = tagMapper.findByName(tagData[0]);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagData[0]);
                    tag.setSlug(tagData[1]);
                    tag.setCreatedAt(LocalDateTime.now());
                    tagMapper.insert(tag);
                }
                PostTag pt = new PostTag();
                pt.setPostId(post.getId());
                pt.setTagId(tag.getId());
                postTagMapper.insert(pt);
            }
        }

        // ===== 4. Feed items =====
        String[][] feedsData = {
            {"最近在研究 Spring Boot 3.2 的虚拟线程支持，性能提升非常明显。在 1000 并发下，响应时间从 800ms 降到了 200ms！",
             "SpringBoot3.2"},
            {"分享一个实用的开发技巧：使用 IDEA 的 Live Templates 可以大幅提升编码效率。我整理了 10 个最常用的模板。",
             "开发工具"},
            {"MySQL 优化小结：通过添加复合索引，查询速度从 3.2s 降到了 12ms。关键是要理解 EXPLAIN 执行计划。",
             "MySQL优化"},
            {"前端小伙伴们，Vite 6 已经发布了！新增模块联邦支持，构建速度进一步提升。从 Webpack 迁移到 Vite 后冷启动快了 30 倍。",
             "Vite6"},
            {"花了一个周末用 Electron + Vue 3 写了一个 Markdown 编辑器，支持实时预览、主题切换、导出PDF。开源在 GitHub 上！",
             "开源项目推荐"},
            {"整理了 2026 年最受欢迎的配色方案，从清新自然到高级商务，共 30 套。每一套都附带了应用案例和色值代码。",
             "配色灵感"},
        };

        // Images for feeds (gradient backgrounds simulating images)
        String[][] feedImages = {
            {"linear-gradient(135deg, #43e97b, #38f9d7)"},
            {"linear-gradient(135deg, #667eea, #764ba2)", "linear-gradient(135deg, #4facfe, #00f2fe)"},
            {},
            {"linear-gradient(135deg, #f093fb, #f5576c)"},
            {"linear-gradient(135deg, #667eea, #764ba2)"},
            {"linear-gradient(135deg, #43e97b, #38f9d7)", "linear-gradient(135deg, #fa709a, #fee140)", "linear-gradient(135deg, #a1c4fd, #c2e9fb)"},
        };

        int[] feedAuthorIdx = {0, 2, 3, 1, 2, 1}; // alternate authors

        for (int i = 0; i < feedsData.length; i++) {
            FeedItem feed = new FeedItem();
            feed.setAuthorId(allUsers.get(feedAuthorIdx[i]).getId());
            feed.setContent(feedsData[i][0]);
            feed.setTopic(feedsData[i][1]);
            feed.setLikeCount((long)(Math.random() * 100 + 10));
            feed.setCommentCount((long)(Math.random() * 20 + 2));
            feed.setRepostCount((long)(Math.random() * 30));
            feed.setCreatedAt(LocalDateTime.now().minusHours(i * 3L + 1));
            feedItemMapper.insert(feed);

            for (int j = 0; j < feedImages[i].length; j++) {
                FeedImage fi = new FeedImage();
                fi.setFeedId(feed.getId());
                fi.setImageUrl(feedImages[i][j]);
                fi.setSortOrder(j);
                feedImageMapper.insert(fi);
            }
        }

        // ===== 5. Follows =====
        follow(admin.getId(), user.getId());
        follow(admin.getId(), xm.getId());
        follow(user.getId(), admin.getId());
        follow(user.getId(), dev.getId());
        follow(xm.getId(), admin.getId());
        follow(xm.getId(), user.getId());
        follow(dev.getId(), admin.getId());
        follow(passerby.getId(), admin.getId());

        // ===== 6. Feed comments =====
        List<FeedItem> feeds = feedItemMapper.selectList(null);
        String[][] commentsData = {
            {"user", "写得很好，学习了！"},
            {"小明", "这个功能太实用了，感谢分享。"},
            {"程序猿", "已 star，期待更多功能。"},
            {"路人甲", "能不能详细展开说说第二部分？"},
            {"user", "收藏了，下次用到的时候来看看。"},
            {"小明", "配色很好看，已经用上了！"},
        };
        for (int i = 0; i < commentsData.length; i++) {
            User cu = userMapper.findByUsername(commentsData[i][0]);
            if (cu != null && i < feeds.size()) {
                FeedComment fc = new FeedComment();
                fc.setFeedId(feeds.get(i).getId());
                fc.setUserId(cu.getId());
                fc.setContent(commentsData[i][1]);
                fc.setCreatedAt(LocalDateTime.now().minusHours(i));
                feedCommentMapper.insert(fc);
            }
        }

        // ===== 7. Feed likes =====
        for (FeedItem f : feeds) {
            for (User u : allUsers) {
                if (Math.random() > 0.5) {
                    FeedLike fl = new FeedLike();
                    fl.setFeedId(f.getId());
                    fl.setUserId(u.getId());
                    fl.setCreatedAt(LocalDateTime.now());
                    feedLikeMapper.insert(fl);
                }
            }
        }

        // ===== 8. Messages =====
        String[][] msgs = {
            {"user", "博客做得不错，加油！"},
            {"程序猿", "文章写得很有深度，已收藏。"},
            {"路人甲", "请问会出 Spring Cloud 系列的教程吗？期待！"},
            {"小明", "网站风格很清新，内容也很实用。"},
            {"user", "建议增加一个夜间模式，晚上看会更舒服~"},
            {"程序猿", "已经推荐给同事了，一起学习。"},
        };
        for (String[] m : msgs) {
            Message msg = new Message();
            User mu = userMapper.findByUsername(m[0]);
            if (mu != null) {
                msg.setUserId(mu.getId());
                msg.setGuestName(mu.getUsername());
            } else {
                msg.setGuestName(m[0]);
            }
            msg.setContent(m[1]);
            msg.setIsVisible(1);
            msg.setCreatedAt(LocalDateTime.now().minusHours((long)(Math.random() * 48)));
            messageMapper.insert(msg);
        }

        // ===== 9. Seed more users =====
        String[][] extraUsers = {
            {"前端爱好者", "frontend@qq.com", "linear-gradient(135deg, #fa709a, #fee140)", "专注前端技术分享"},
            {"Java开发者", "javadev@163.com", "linear-gradient(135deg, #43e97b, #38f9d7)", "Java 后端开发工程师"},
            {"算法爱好者", "algo@qq.com", "linear-gradient(135deg, #a1c4fd, #c2e9fb)", "每天刷一道 LeetCode"},
        };
        for (String[] u : extraUsers) {
            User eu = createUser(u[0], u[1], "123456", u[2], u[3], "user");
            follow(eu.getId(), admin.getId());
        }

        log.info("Seed data initialized: {} users, {} posts, {} feeds, {} messages",
                userMapper.selectCount(null), postMapper.selectCount(null),
                feedItemMapper.selectCount(null), messageMapper.selectCount(null));
    }

    private User createUser(String name, String email, String pwd, String color, String bio, String role) {
        User u = new User();
        u.setUsername(name);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(pwd));
        u.setAvatarColor(color);
        u.setBio(bio);
        u.setRole(role);
        u.setStatus("active");
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(u);
        return u;
    }

    private void follow(Long followerId, Long followedId) {
        Follow f = new Follow();
        f.setFollowerId(followerId);
        f.setFollowedId(followedId);
        f.setCreatedAt(LocalDateTime.now());
        followMapper.insert(f);
    }
}
