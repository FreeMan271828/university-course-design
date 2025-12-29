package org.freeman.ticketmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.freeman.ticketmanager.common.RequireRole;
import org.freeman.ticketmanager.common.UserContext;
import org.freeman.ticketmanager.common.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${isTest}")
    private String isTest;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (Objects.equals(isTest, "true")) {
            UserDetail adminUser = new UserDetail();
            adminUser.setUserId(9L);
            adminUser.setUsername("admin");
            adminUser.setRoles(Arrays.asList("管理员", "ADMIN"));
            UserContext.set(adminUser);
            return true; // 直接放行，跳过后续 Token 校验
        }
        // 1. 如果不是映射到Controller方法（可能是静态资源），直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 2. 获取 Token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        token = token.substring(7);

        // 3. Redis 校验 & 获取用户信息
        String redisKey = "auth:token:" + token;
        String userJson = redisTemplate.opsForValue().get(redisKey);

        if (userJson == null) {
            response.setStatus(401); // Token 过期或无效
            return false;
        }

        // 4. 反序列化并存入 ThreadLocal
        UserDetail userDetail = objectMapper.readValue(userJson, UserDetail.class);
        UserContext.set(userDetail);

        // 5. Token 自动续期 (例如续期2小时)
        redisTemplate.expire(redisKey, 2, TimeUnit.HOURS);

        // 6. 权限校验 (@RequireRole)
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);

        // 如果方法上有注解，则校验角色
        if (requireRole != null) {
            String[] allowedRoles = requireRole.value();
            List<String> userRoles = userDetail.getRoles();

            // 取交集：只要用户拥有允许角色的其中一个，就放行
            boolean hasPermission = userRoles.stream()
                    .anyMatch(role -> Arrays.asList(allowedRoles).contains(role));

            if (!hasPermission) {
                response.setStatus(403); // 无权限
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 必须清理 ThreadLocal，防止内存泄漏或线程复用导致的数据污染
        UserContext.remove();
    }
}