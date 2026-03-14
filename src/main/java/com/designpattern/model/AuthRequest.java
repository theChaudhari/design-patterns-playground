package com.designpattern.model;

import lombok.Data;

@Data
public class AuthRequest {

    private String type;   // API_KEY | JWT | OAUTH
    private String token;

}