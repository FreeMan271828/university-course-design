# ===============================
# Stage 1: 构建阶段 (使用 Maven 编译)
# ===============================
FROM maven:3.9-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 1. 先拷贝 pom.xml 和 src 源码
COPY pom.xml .
COPY src ./src

# 2. 执行打包 (跳过测试，避免因连不上数据库导致构建失败)
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: 运行阶段 (使用轻量级 JRE)
# ===============================
FROM eclipse-temurin:17-jre-alpine

# 设置时区为上海 (可选)
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app

# 从构建阶段拷贝生成的 jar 包，重命名为 app.jar
# 注意：这里假设 target 下只有一个 jar 包，或者你可以指定具体名字
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]