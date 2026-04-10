# Gateway 新手入门指南

本项目是一个基于 **Spring Cloud Gateway** 的网关示例，面向刚接触微服务网关的同学。  
目标是让你快速掌握 Gateway 的基本用法，并理解实际项目中常见的路由规则写法。

## 1. 你将学到什么

- Gateway 的核心概念：`Route`、`Predicate`、`Filter`
- 基于服务发现（Nacos）的转发方式：`lb://service-name`
- 常见路由规则（路径匹配、前缀剥离、路径重写、Header 注入、重试）

## 2. 项目结构

- `src/main/resources/application.yaml`：当前运行配置
- `src/main/resources/application-routes-demo.yaml`：常用路由规则示例（学习用）

## 3. 环境要求

- JDK 8
- Maven 3.8+
- 可用的 Nacos 服务（项目默认使用 Nacos 服务发现）

## 4. 快速启动

```bash
mvn clean package
mvn spring-boot:run
```

默认端口：`8040`

## 5. 核心概念（新手必读）

### Route（路由）

一条路由 = 什么时候匹配（Predicate） + 匹配后怎么处理（Filter） + 转发到哪里（uri）。

### Predicate（断言）

决定请求是否命中路由，例如：

- `Path=/api/user/**`
- `Method=GET`
- `After=2026-01-01T00:00:00+08:00[Asia/Shanghai]`

### Filter（过滤器）

命中路由后对请求/响应做加工，例如：

- `StripPrefix=1`：去掉路径前缀
- `RewritePath=/api/(?<segment>.*), /${segment}`：重写路径
- `AddRequestHeader=X-Gateway, demo-gateway`：增加请求头
- `Retry=3,INTERNAL_SERVER_ERROR`：失败重试

## 6. 常见路由规则示例

请查看：

`src/main/resources/application-routes-demo.yaml`

其中包含实际项目常用规则示例：

- 基于 Path + StripPrefix 的后端服务转发
- 基于 RewritePath 的 URL 重写
- 基于 Method + Header 的精确匹配
- 基于 Host 的域名路由
- 基于 Retry 的容错配置

## 7. 学习建议

1. 先跑通当前项目，确认网关进程能正常启动。
2. 从 `application-routes-demo.yaml` 复制一条路由到 `application.yaml` 并实践。
3. 用 Postman / curl 分别测试命中与不命中的请求，观察行为差异。

---

如果你是第一次接触网关，建议先只关注三件事：

1. 请求如何匹配到路由（Predicate）
2. 请求在网关做了哪些变换（Filter）
3. 最终被转发到了哪个服务（uri）
