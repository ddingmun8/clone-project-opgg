package com.gnar.cloneprojectopgg.controller;

import com.gnar.cloneprojectopgg.GRUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {
    GRUtils grUtils = new GRUtils();

    @GetMapping(path = "/")
    public String hellow(Model model) {
        System.out.println("test");
        //소환사 명 인코딩
        model.addAttribute("name", "gary");
        return "index";
    }

    @GetMapping(path = "/test")
    public String test(Model model) {
        System.out.println("test");
        //소환사 명 인코딩
        model.addAttribute("name", "gary");
        return "gold";
    }

}
