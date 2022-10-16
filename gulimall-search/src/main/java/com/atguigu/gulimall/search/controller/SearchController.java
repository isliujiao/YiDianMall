package com.atguigu.gulimall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author:厚积薄发
 * @create:2022-10-16-11:37
 */
@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String listPage(){
        return "list";
    }

}
