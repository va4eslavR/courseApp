package com.courseApp.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {
    @GetMapping(value = "/authPage")
    public String getLoginPage(){
        return "authPage";
    }
    @GetMapping(value = "/authentication")
    public String getauthPage(){
        var name=SecurityContextHolder.getContext().getAuthentication().getName();
        var tostr=SecurityContextHolder.getContext().getAuthentication().toString();
        System.out.println(name);
        System.out.println(tostr);
        return "authPage";
    }
}
