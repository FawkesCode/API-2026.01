package com.fawkes.api.Controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/director")
public class DirectorController {

    @GetMapping(value = "/panel")
    public String directorpanel() {
        return "Somente Diretor Acessa!";
    }
}