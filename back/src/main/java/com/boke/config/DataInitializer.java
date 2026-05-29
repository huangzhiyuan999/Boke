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

    @Override
    @Transactional
    public void run(String... args) {
        if (userMapper.findByUsername("admin") != null) {
            log.info("Data already initialized, skipping");
            return;
        }

        log.info("Initializing default data...");

        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@blog.com");
        admin.setPasswordHash(passwordEncoder.encode("admin"));
        admin.setAvatarColor("linear-gradient(135deg, #5B8C5A, #7EC8A8)");
        admin.setBio("博客管理员");
        admin.setRole("admin");
        admin.setStatus("active");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(admin);

        // Create test user
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@blog.com");
        user.setPasswordHash(passwordEncoder.encode("user123"));
        user.setAvatarColor("linear-gradient(135deg, #667eea, #764ba2)");
        user.setBio("普通用户");
        user.setRole("user");
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        // Create more users
        String[][] usersData = {
            {"小明", "xiaoming@qq.com", "linear-gradient(135deg, #4facfe, #00f2fe)", "热爱前端开发"},
            {"程序猿", "dev@163.com", "linear-gradient(135deg, #f093fb, #f5576c)", "全栈工程师"},
            {"路人甲", "passerby@qq.com", "linear-gradient(135deg, #43e97b, #38f9d7)", "路过看看"},
        };
        for (String[] u : usersData) {
            User uu = new User();
            uu.setUsername(u[0]);
            uu.setEmail(u[1]);
            uu.setPasswordHash(passwordEncoder.encode("123456"));
            uu.setAvatarColor(u[2]);
            uu.setBio(u[3]);
            uu.setRole("user");
            uu.setStatus("active");
            uu.setCreatedAt(LocalDateTime.now());
            uu.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(uu);
        }

        // Site config
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

        // Create sample posts
        String[][] postsData = {
            {"博客正式上线啦", "经过几周的开发，个人博客终于正式上线了。",
             "linear-gradient(135deg, #f56c6c, #e63946)", "announcement",
             "## 博客上线公告\n\n经过几个星期的设计和开发，这个个人技术博客终于和大家见面了。\n\n### 关于本站\n\n本站使用 Vue 3 + Vite 构建，Spring Boot 后端支撑。\n\n感谢你的来访！"},
            {"留言板功能已开放", "留言板页面已经上线，欢迎留言。",
             "linear-gradient(135deg, #4facfe, #00f2fe)", "announcement",
             "## 留言板功能说明\n\n欢迎大家提出宝贵建议！"},
            {"Vue 3 Composition API 实战指南", "深入探讨 Vue 3 Composition API 的核心概念。",
             "linear-gradient(135deg, #667eea, #764ba2)", "post",
             "## 什么是 Composition API\n\nComposition API 是 Vue 3 引入的一套全新的逻辑组织方式。\n\n### ref 和 reactive\n\n`ref` 用于包装基本类型的响应式数据。\n\n## 总结\n\nComposition API 提供了更灵活的代码组织方式。"},
            {"CSS Grid 布局完全教程", "从入门到精通 CSS Grid 布局系统。",
             "linear-gradient(135deg, #f093fb, #f5576c)", "post",
             "## CSS Grid 简介\n\nCSS Grid 是一个二维的布局系统。\n\n## 实践案例\n\nCSS Grid 的浏览器支持已经非常广泛。"},
            {"JavaScript 异步编程", "梳理 JavaScript 异步编程的演进历程。",
             "linear-gradient(135deg, #4facfe, #00f2fe)", "post",
             "## 异步编程的演进\n\n从回调到 Promise 再到 async/await。"},
            {"前端性能优化锦囊", "分享实用的前端性能优化技巧。",
             "linear-gradient(135deg, #43e97b, #38f9d7)", "post",
             "## 前端性能为什么重要\n\n页面加载速度直接影响用户体验和转化率。"},
            {"构建你的第一个 RESTful API", "从零搭建 RESTful API。",
             "linear-gradient(135deg, #fa709a, #fee140)", "post",
             "## RESTful API 设计原则\n\n一个好的 API 设计应该简洁、直观、符合规范。"},
            {"Git 工作流最佳实践", "介绍主流 Git 工作流。",
             "linear-gradient(135deg, #a18cd1, #fbc2eb)", "post",
             "## 为什么需要 Git 工作流\n\n团队协作中，清晰的分支管理策略至关重要。"},
        };

        String[][] tagsData = {
            {"Vue", "vue"}, {"前端", "frontend"}, {"CSS", "css"}, {"布局", "layout"},
            {"JavaScript", "javascript"}, {"异步", "async"}, {"性能", "performance"},
            {"优化", "optimization"}, {"Node.js", "nodejs"}, {"后端", "backend"},
            {"Git", "git"}, {"工具", "tools"},
        };

        String[][][] postTagsMapping = {
            {}, // announcement 1 - no tags
            {}, // announcement 2 - no tags
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
            post.setViewCount(0L);
            post.setIsPublished(1);
            post.setCreatedAt(LocalDateTime.now().minusDays(postsData.length - i));
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.insert(post);

            // Tags
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

        // Sample messages
        String[][] msgs = {
            {"user", "博客做得不错，加油！"},
            {"程序猿", "文章写得很有深度，已收藏。"},
            {"路人甲", "请问会出 Spring Cloud 系列的教程吗？期待！"},
        };
        for (String[] m : msgs) {
            Message msg = new Message();
            User msgUser = userMapper.findByUsername(m[0]);
            if (msgUser != null) {
                msg.setUserId(msgUser.getId());
                msg.setGuestName(msgUser.getUsername());
            } else {
                msg.setGuestName(m[0]);
            }
            msg.setContent(m[1]);
            msg.setIsVisible(1);
            msg.setCreatedAt(LocalDateTime.now());
            messageMapper.insert(msg);
        }

        log.info("Default data initialized successfully");
    }
}
