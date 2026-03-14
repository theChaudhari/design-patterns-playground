package com.designpattern.controller;

import com.designpattern.handler.AuthHandler;
import com.designpattern.model.AuthRequest;
import com.designpattern.model.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthHandler authHandlerChain;

    @PostMapping("/authenticate")
    public AuthResponse authenticate(@RequestBody AuthRequest request) {
        log.info("authenticate request: {}", request);
        return authHandlerChain.authenticate(request);
    }

}
