package com.hanyukeji.weblog.admin.config;

import com.hanyukeji.weblog.jwt.config.JwtAuthenticationSecurityConfig;
import com.hanyukeji.weblog.jwt.filter.TokenAuthenticationFilter;
import com.hanyukeji.weblog.jwt.handler.RestAccessDeniedHandler;
import com.hanyukeji.weblog.jwt.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 在 configure() 方法中，首先禁用了 CSRF（Cross-Site Request Forgery）攻击防护。在前后端分离的情况下，通常不需要启用 CSRF 防护。
 * 同时，还禁用了表单登录，并应用了 JWT 相关的配置类 JwtAuthenticationSecurityConfig。
 * 最后，配置会话管理这块，将会话策略设置为无状态（STATELESS），适用于前后端分离的情况，无需创建会话。
 * 我们注入了前面写好的相关类，如过滤器、处理器等，主要改动代码如下：
 * <p>
 * .httpBasic().authenticationEntryPoint(authEntryPoint) : 处理用户未登录访问受保护的资源的情况；
 * .exceptionHandling().accessDeniedHandler(deniedHandler) : 处理登录成功后访问受保护的资源，但是权限不够的情况;
 * .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) : 将 Token 校验过滤器添加到用户认证过滤器之前;
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;
    @Autowired
    private RestAuthenticationEntryPoint authEntryPoint;
    @Autowired
    private RestAccessDeniedHandler deniedHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(). // 禁用 csrf
                formLogin().disable() // 禁用表单登录
                .apply(jwtAuthenticationSecurityConfig) // 设置用户登录认证相关配置
                .and()
                .authorizeHttpRequests()
                .mvcMatchers("/admin/**").authenticated() // 认证所有以 /admin 为前缀的 URL 资源
                .anyRequest().permitAll() // 其他都需要放行，无需认证
                .and()
                .httpBasic().authenticationEntryPoint(authEntryPoint) // 处理用户未登录访问受保护的资源的情况
                .and()
                .exceptionHandling().accessDeniedHandler(deniedHandler) // 处理登录成功后访问受保护的资源，但是权限不够的情况
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 前后端分离，无需创建会话
                .and()
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // 将 Token 校验过滤器添加到用户认证过滤器之前
        ;
    }

    /**
     * Token 校验过滤器
     *
     * @return
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

}
