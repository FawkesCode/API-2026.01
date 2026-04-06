package com.fawkes.api.Controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping(value = "/panel")
    public String managerpanel() {
        return "Somente Gerente Acessa!";
    }
}
