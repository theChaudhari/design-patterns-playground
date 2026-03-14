package com.designpattern.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String status;
    private String handler;
    private String message;

}
