package com.fawkes.api.Controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operational")
public class OperationalController {

    @GetMapping(value = "/panel")
    public String operationalpanel() {
        return "Somente Operacional Acessa!";
    }
}