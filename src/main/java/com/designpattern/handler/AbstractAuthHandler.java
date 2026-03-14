package com.designpattern.handler;

import com.designpattern.model.AuthRequest;
import com.designpattern.model.AuthResponse;
import com.designpattern.utils.Constants;

public abstract class AbstractAuthHandler implements AuthHandler {

    private AuthHandler nextHandler;

    @Override
    public AuthHandler setNext(AuthHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler; // allows chaining: h1.setNext(h2).setNext(h3)
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (nextHandler != null) {
            return nextHandler.authenticate(request);
        }
        // End of chain — no handler could process the request
        return new AuthResponse(Constants.FAILURE, Constants.DEFAULT_HANDLER, Constants.AUTH_FAILED);
    }

}