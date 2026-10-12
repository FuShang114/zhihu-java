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
| J1 | 项目骨架 + 统一响应体 Result<T> + /ping | 🚧 |
| J2 | 用户注册/登录（BCrypt + JWT）+ MyBatis-Plus 落库 | ⬜ |
| J3 | 提问/回答（Question/Answer 领域模型） | ⬜ |
| J4 | 赞同/收藏（Vote/Favorite + 计数一致性） | ⬜ |
| J5 | 业务模型性能优化（索引/缓存/分页） | ⬜ |
| J6+ | 关注关系、双 Feed 流、通知、搜索热榜 → DDD 四层 → 微服务化 | ⬜ |

## 全局约定

- 错误码：1xxxx 通用 / 2xxxx 用户 / 3xxxx 内容 / 4xxxx 互动
- 端口：应用 8080（Spring Boot 默认）
- 敏感信息（密码/密钥）只存本地 `.secrets/`（已 gitignore），严禁进 git
