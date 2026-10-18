package com.hanyukeji.weblog.jwt.filter;

import com.hanyukeji.weblog.jwt.utils.JwtTokenHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 我们自定义了一个 Spring Security 过滤器 TokenAuthenticationFilter，它继承了 OncePerRequestFilter，确保每个请求只被过滤一次。
 * 在重写的 doFilterInternal() 方法中来定义过滤器处理逻辑，首先，从请求头中获取 key 为 Authorization 的值，
 * 判断是否以 Bearer 开头，若是，截取出 Token, 对其进行解析，并对可能抛出的异常做出不同的返参。
 * 最后，我们获取了用户详情信息，将用户信息存入 authentication，方便后续进行校验，同时将 authentication 存入 ThreadLocal 中，方便后面方便的获取用户信息。
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    @Value("${jwt.tokenHeaderKey}")
    private String tokenHeaderKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取 key 为 Authorization 的值
        String header = request.getHeader(tokenHeaderKey);
        //获取请求的URI
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/admin")) {
            // 判断 value 值是否以 Bearer 开头
            if (StringUtils.startsWith(header, tokenPrefix)) {
                // 截取 Token 令牌（按配置的前缀长度截取，避免硬编码；不在日志中输出 Token，防止泄露）
                String token = StringUtils.trim(StringUtils.substring(header, tokenPrefix.length()));

                // 判空 Token
                if (StringUtils.isNotBlank(token)) {
                    try {
                        // 校验 Token 是否可用, 若解析异常，针对不同异常做出不同的响应参数
                        jwtTokenHelper.validateToken(token);
                    } catch (ExpiredJwtException e) {
                        authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 已失效"));
                        return;
                    } catch (JwtException | IllegalArgumentException e) {
                        // 签名错误、格式错误、签发人不匹配等，统一让 AuthenticationEntryPoint 处理响应参数
                        authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 不可用"));
                        return;
                    }

                    // 从 Token 中解析出用户名
                    String username = jwtTokenHelper.getUsernameByToken(token);

                    if (StringUtils.isNotBlank(username)
                            && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                        // 根据用户名获取用户详情信息（用户可能已被删除）
                        UserDetails userDetails;
                        try {
                            userDetails = userDetailsService.loadUserByUsername(username);
                        } catch (UsernameNotFoundException e) {
                            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("用户不存在"));
                            return;
                        }

                        // 将用户信息存入 authentication，方便后续校验
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        // 继续执行写一个过滤器
        filterChain.doFilter(request, response);
    }
}
