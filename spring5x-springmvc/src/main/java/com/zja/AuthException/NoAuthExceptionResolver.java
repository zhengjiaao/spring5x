package com.zja.AuthException;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhengJa
 * @description 全局异常处理器：实现自定义异常处理器
 * @data 2019/10/22
 */
public class NoAuthExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        if (e instanceof NoAuthException && !isAjax(httpServletRequest)) {
            return new ModelAndView("NoAuthPage");
        }
        return null;
    }
    // 判断是否是 Ajax 请求
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}
