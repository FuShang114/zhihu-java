# zhihu-java —— 从 0 到 1 复刻知乎（Spring Boot 教学项目）

一比一复刻知乎核心业务的引导式教学项目。当前阶段聚焦**核心业务**：用户、提问、回答、赞同、收藏。
先业务模型设计，再实现，性能优化最后集中做。

> 姊妹项目 [zhihu-go](https://github.com/FuShang114/zhihu-go)：同一业务的 Go 手搓版（bcrypt/JWT/中间件全部裸写），
> 本项目在 Spring 生态重走一遍——手搓过的概念在这里都能找到框架对应物。

## 技术栈

- JDK 21 · Spring Boot 4.1 · Maven
- MySQL 8.0（MyBatis-Plus，J2 起）· Redis（J6 起）
- 统一响应体：`{"code":0,"msg":"ok","data":{...}}`（code=0 成功）

## 课程路线

| 课 | 内容 | 状态 |
|----|------|------|
| M0 | 项目骨架 + 统一响应体 Result<T> + /ping + E2E 测试基架 | ✅ |
| M1 | 用户域：注册/登录/me（BCrypt + JJWT + 拦截器 + @CurrentUser） | ✅ |
| M2 | 内容域：提问/回答（Question/Answer + 分页 + 冗余计数） | ✅ |
| M3 | 互动域：赞同投票状态机（多态 votes 表）+ 收藏（两层夹/条目） | ✅ |
| M4 | 业务模型性能优化（索引/缓存/分页/N+1/计数一致性） | ⬜ |
| M5+ | 关注关系、双 Feed 流、通知、搜索热榜 → DDD 四层 → 微服务化 | ⬜ |

## E2E 测试

```bash
mvn test    # 32 个端到端场景：真实 Spring Boot + 真实 MySQL（zhihu_test 库自动重建）
```

## 全局约定

- 错误码：1xxxx 通用 / 2xxxx 用户 / 3xxxx 内容 / 4xxxx 互动
- 端口：应用 8080（Spring Boot 默认）
- 敏感信息（密码/密钥）只存本地 `.secrets/`（已 gitignore），严禁进 git
