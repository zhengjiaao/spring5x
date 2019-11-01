package com.zja.controller;

import com.zja.constant.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @author ZhengJa
 * @description 登录
 * @data 2019/10/31
 */
@Controller
@RequestMapping("rest/login")
public class LoginController {

    @GetMapping("chat")
    public String login(@RequestParam String username, HttpSession session){
        session.setAttribute(Constants.USER_NAME,username);
        return "chat";
    }

}
