package com.designpattern.handler;

import com.designpattern.model.AuthRequest;
import com.designpattern.model.AuthResponse;
import com.designpattern.utils.Constants;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class JwtAuthHandler extends AbstractAuthHandler {

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (Constants.JWT.equalsIgnoreCase(request.getType())) {
            if (request.getToken() != null && request.getToken().startsWith("Bearer ")) {
                return new AuthResponse(Constants.SUCCESS, Constants.JWT_HANDLER, Constants.JWT_SUCCESS);
            }
            return new AuthResponse(Constants.FAILURE, Constants.JWT_HANDLER, Constants.INVALID_TOKEN);
        }
        // Not a JWT request — pass to next handler
        return super.authenticate(request);
    }

}