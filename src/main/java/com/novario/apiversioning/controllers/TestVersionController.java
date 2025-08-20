package com.novario.apiversioning.controllers;

import com.novario.apiversioning.annotations.Version;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Version("v1") // Class-level default version
public class TestVersionController {

    // Inherits v1 from class
    @GetMapping("/hello")
    public String helloV1() {
        return "Hello from v1 (class-level)";
    }

    // Still under v1 because of class-level
    @GetMapping("/ping")
    public String pingV1() {
        return "Ping v1 OK";
    }

    // Method-level override to v2
    @Version("v2")
    @GetMapping("/hello")
    public String helloV2() {
        return "Hello from v2 (method-level override)";
    }

    // Unversioned endpoint (ignores class-level version, will map under plain /api/test/info)
    @Version("")
    @GetMapping("/info")
    public String info() {
        return "Unversioned endpoint";
    }
}
