package com.designpattern.handler;

import com.designpattern.model.AuthRequest;
import com.designpattern.model.AuthResponse;

public interface AuthHandler {
    // Set the next handler in the chain
    AuthHandler setNext(AuthHandler nextHandler);

    // Handle or pass to next
    AuthResponse authenticate(AuthRequest request);
}
