package com.yachiyo.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.yachiyo.entity.User;
import com.yachiyo.Config.FastMethodConfig;
import com.yachiyo.Utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component @Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${security.open-api}")
    private String[] openApi;

    // 在类中添加路径匹配器
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt != null && jwtUtils.isTokenValid(jwt)) {
                String userId = jwtUtils.getUserIdFromToken(jwt);
                // 验证通过，继续处理请求
                String username = jwtUtils.getNameFromToken(jwt);
                User user = new User(Integer.parseInt(userId), username, null, null, null);
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未认证");
            }
        } catch (Exception e) {
            // 处理异常，返回内部服务器错误
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误"+e.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        for (String openUrl : openApi) {
            if (pathMatcher.match(openUrl, uri)) {
                return true;
            }
        }
        return false;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (!response.isCommitted()) {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String jsonResponse = String.format("{\"error\": \"%d\", \"message\": \"%s\"}",
                    status, message);
            response.getWriter().write(jsonResponse);
        } else {
            logger.warn("Response already committed, cannot send error: {} - {}");
        }
    }
}
