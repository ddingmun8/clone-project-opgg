package com.gnar.cloneprojectopgg.controller;

import com.gnar.cloneprojectopgg.GRUtils;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class MainController {
    GRUtils grUtils = new GRUtils();

    @GetMapping(path = "/")
    public String hellow(Model model) {
        //소환사 명 인코딩
        model.addAttribute("name", "gary");
        return "index";
    }
}
