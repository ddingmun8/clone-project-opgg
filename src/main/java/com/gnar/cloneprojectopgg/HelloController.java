package com.gnar.cloneprojectopgg;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HelloController {
    GRUtils grUtils = new GRUtils();

    @GetMapping(path = "/hellow/{name}")
    public String hellow(@PathVariable String name, Model model) {
        //소환사 명 인코딩
        model.addAttribute("name", name);
        return "index";
    }
}