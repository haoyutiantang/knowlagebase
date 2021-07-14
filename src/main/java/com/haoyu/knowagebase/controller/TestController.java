package com.haoyu.knowagebase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @author haoyu
 * @date 2021/7/14 23:39
 */
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello(){
        return "hello world";
    }
}
