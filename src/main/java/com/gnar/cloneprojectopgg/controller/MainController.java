package com.gnar.cloneprojectopgg.controller;

import com.gnar.cloneprojectopgg.GRUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {
    GRUtils grUtils = new GRUtils();

    @GetMapping(path = "/main")
    public String main(Model model) {
        return "main";
    }

    @GetMapping(path = "/summonerInfo/{summonerName}")
    public String summonerInfo(@PathVariable(name = "summonerName") String summonerName, Model model) {
        model.addAttribute("summonerName", summonerName);
        return "summonerInfo";
    }

    @GetMapping(path = "/hellow")
    public String hellow(Model model) {
        System.out.println("test");
        //소환사 명 인코딩
        model.addAttribute("name", "gary");
        return "index";
    }
}
