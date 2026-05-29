<template>
  <div class="page-bg" :style="{ backgroundImage: `url('/photo/默认背景图.jpg')` }">
    <div class="bg-overlay"></div>
    <div class="about container">
    <!-- Profile -->
    <section class="profile">
      <img class="avatar" src="/photo/作者个人照.jpeg" alt="黄志远" />
      <h1>黄志远</h1>
      <p class="role">计算机科学专业 · 在校学生</p>
    </section>

    <!-- 关于我 -->
    <section class="block">
      <p>大家好，我是<strong>黄志远</strong>，一名计算机科学专业的在校学生。在学习过程中我发现，单纯的理论学习很容易流于表面，因此搭建了这个个人博客。</p>
      <p>建站初心很简单：将课堂与自学的技术知识落地实践，记录学习轨迹、复盘踩坑问题，也希望能和各位技术同好交流探讨，共同进步。</p>
    </section>

    <!-- 技术方向 -->
    <section class="block">
      <h2 class="title">技术方向</h2>

      <div class="skill-card">
        <h3><span class="dot dot-green"></span> Java 后端</h3>
        <div class="tags">
          <span>Java 基础</span><span>JVM 原理</span><span>JUC 并发</span>
          <span>MySQL</span><span>Redis</span><span>消息队列 MQ</span>
          <span>SSM</span><span>Spring Boot</span><span>Spring Cloud</span>
        </div>
      </div>

      <div class="skill-card">
        <h3><span class="dot dot-blue"></span> AI 应用</h3>
        <div class="tags">
          <span>Python</span><span>LLM</span><span>RAG 检索增强</span>
          <span>LangChain</span><span>LangGraph</span><span>Spring AI</span>
          <span>网络爬虫</span><span>大模型业务集成</span>
        </div>
      </div>
    </section>

    <!-- 未来规划 -->
    <section class="block">
      <h2 class="title">未来规划</h2>
      <p>持续深耕后端开发与 AI 融合领域，坚持输出实战笔记、技术总结与项目拆解内容，欢迎大家留言交流、指正问题。</p>
    </section>

    <!-- 联系 -->
    <section class="block">
      <h2 class="title">联系</h2>
      <div class="contact-row">
        <a href="https://github.com/huangzhiyuan999" target="_blank" rel="noopener" class="contact-item">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0a12 12 0 0 0-3.8 23.4c.6.1.8-.3.8-.6v-2.2c-3.3.7-4-1.4-4-1.4-.5-1.4-1.2-1.8-1.2-1.8-1-.7.1-.7.1-.7 1.1.1 1.7 1.1 1.7 1.1 1 1.7 2.6 1.2 3.2.9.1-.7.4-1.2.7-1.5-2.7-.3-5.5-1.3-5.5-5.9 0-1.3.5-2.4 1.2-3.2-.1-.3-.5-1.5.1-3.2 0 0 1-.3 3.3 1.2a11.5 11.5 0 0 1 6 0c2.3-1.5 3.3-1.2 3.3-1.2.6 1.7.2 2.9.1 3.2.8.8 1.2 1.9 1.2 3.2 0 4.6-2.8 5.6-5.5 5.9.4.4.7 1.1.7 2.2v3.3c0 .3.2.7.8.6A12 12 0 0 0 12 0z"/></svg>
          GitHub
        </a>
        <a href="mailto:3189509410@qq.com" class="contact-item">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 4-10 8L2 4"/></svg>
          3189509410@qq.com
        </a>
      </div>
    </section>
  </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-bg {
  position: relative;
  min-height: calc(100vh - 60px);
  background-size: cover;
  background-position: center 25%;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.bg-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.85);
  pointer-events: none;
  z-index: 0;
}

.about {
  position: relative;
  z-index: 1;
  padding-top: 36px;
  padding-bottom: 60px;
}

/* ===== Profile ===== */
.profile {
  text-align: left;
  margin-bottom: 32px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 14px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.profile h1 {
  font-size: 1.5rem;
  margin: 0 0 4px;
  font-weight: 600;
}

.role {
  color: var(--color-text-muted);
  font-size: 0.88rem;
  margin: 0;
}

/* ===== Block ===== */
.block {
  margin-bottom: 28px;
}

.block p {
  color: var(--color-text-secondary);
  font-size: 0.92rem;
  line-height: 1.85;
  margin: 0.3em 0;
}

.title {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 14px;
  color: var(--color-text);
}

/* ===== Skill Card ===== */
.skill-card {
  margin-bottom: 14px;
}

.skill-card h3 {
  font-size: 0.95rem;
  font-weight: 600;
  margin: 0 0 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.dot-green { background: #5B8C5A; }
.dot-blue { background: #4facfe; }

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tags span {
  font-size: 0.82rem;
  padding: 5px 14px;
  background: #f0f5f1;
  color: #4a6e4d;
  border-radius: 6px;
  font-weight: 500;
}

/* ===== Contact ===== */
.contact-row {
  display: flex;
  gap: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 7px;
  color: var(--color-text-secondary);
  font-size: 0.88rem;
  transition: color 0.2s;
}

.contact-item:hover {
  color: var(--color-primary);
}

</style>
