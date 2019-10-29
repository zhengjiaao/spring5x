package com.zja.MyInterceptors;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhengJa
 * @description 创建的第二个拦截器，实现HandlerInterceptor
 * @data 2019/10/22
 */
public class MySecondInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("进入第二个拦截器 preHandle");
        return true;
    }
    // 需要注意的是，如果对应的程序报错，不一定会进入这个方法 但一定会进入 afterCompletion 这个方法
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        System.out.println("进入第二个拦截器 postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        System.out.println("进入第二个拦截器 afterCompletion");
    }
}
