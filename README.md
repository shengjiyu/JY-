# Weblog

前后端分离个人博客。

| 目录 | 说明 |
| --- | --- |
| `jyblog-springboot/` | 后端：Spring Boot 2.6 + MyBatis Plus + Spring Security(JWT) + MinIO，多模块（web / admin / common / jwt） |
| `weblog-vue3/` | 前端：Vue 3 + Vite + Element Plus + Tailwind CSS（Flowbite），支持深色 / 浅色模式切换 |

## 本地开发

```bash
# 后端（默认 dev 环境，需本地 MySQL，建表脚本见 weblog-web/src/main/resources/db/init.sql）
cd jyblog-springboot && mvn -DskipTests package
java -jar weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar

# 前端（/api 代理到 http://localhost:8080）
cd weblog-vue3 && npm install && npm run dev
```

## 配置与敏感信息

敏感配置不入库，生产环境通过以下任一方式提供：

- 环境变量：`DB_PASSWORD`、`MINIO_ACCESS_KEY`、`MINIO_SECRET_KEY`、`JWT_SECRET`（可选 `DB_HOST`、`DB_USERNAME`、`MINIO_ENDPOINT`、`MINIO_BUCKET`）
- 或在 jar 同级目录放置 `config/application-prod.yml`，Spring Boot 会自动加载并覆盖 jar 内配置

dev 环境同样支持上述环境变量覆盖默认值。前端页脚备案号通过 `weblog-vue3/.env.production` 中的 `VITE_APP_ICP` 配置。
