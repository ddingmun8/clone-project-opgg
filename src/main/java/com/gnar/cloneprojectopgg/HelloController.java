package com.gnar.cloneprojectopgg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;

@Controller
public class HelloController {
    GRUtils grUtils = new GRUtils();

    @GetMapping(path = "/hellow/{name}")
    public String hellow(@PathVariable String name, Model model) {
        //소환사 명 인코딩
        System.out.println("path ==> test");
        model.addAttribute("name", name);
        return "index";
    }


    @GetMapping("/display")
    public ResponseEntity<Resource> display(@RequestParam("filename") String filename) {
        String path = "C:\\gray\\upload\\lol\\";
        String folder = "tierBorder\\";
        System.out.println("path ==> " + path + folder + filename);

        FileSystemResource resource = new FileSystemResource(path + folder + filename);
        System.out.println("resource ==> " + resource);
        if(!resource.exists()) 
            return new ResponseEntity<Resource>(null, null, HttpStatus.SC_NOT_FOUND);
        HttpHeaders header = new HttpHeaders();
        Path filePath = null;
        try{
            filePath = Paths.get(path + folder + filename);
            header.add("Content-type", Files.probeContentType(filePath));
        }catch(IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Resource>((Resource) resource, header, HttpStatus.SC_OK);
    }
}