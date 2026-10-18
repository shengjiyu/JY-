package com.hanyukeji.weblog.jwt.handler;

import com.hanyukeji.weblog.common.enums.ResponseCodeEnum;
import com.hanyukeji.weblog.common.utils.Response;
import com.hanyukeji.weblog.jwt.exception.UsernameOrPasswordNullException;
import com.hanyukeji.weblog.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 通过自定义了一个实现了 Spring Security 的 AuthenticationFailureHandler 接口类，用于在用户身份验证失败后执行一些逻辑。
 * 首先，我们打印了异常日志，方便后续定位问题，然后对异常的类型进行判断，通过 ResultUtil 工具类，返回不同的错误信息，
 * 如用户名或者密码为空、用户名或密码错误等，若未判断出异常是什么类型，则统一提示为 登录失败。
 */
@Component
@Slf4j
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("AuthenticationException: ", exception);
        if (exception instanceof UsernameOrPasswordNullException) {
            // 用户名或密码为空
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
            return;
        } else if (exception instanceof BadCredentialsException) {
            // 用户名或密码错误
            ResultUtil.fail(response, Response.fail(ResponseCodeEnum.USERNAME_OR_PWD_ERROR));
            return;
        }

        // 登录失败
        ResultUtil.fail(response, Response.fail(ResponseCodeEnum.LOGIN_FAIL));
    }
}
