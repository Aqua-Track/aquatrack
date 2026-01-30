package com.ufpb.aquatrack.infra.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {
    @GetMapping
    public String home() {
        return "redirect:/login";
    }
}