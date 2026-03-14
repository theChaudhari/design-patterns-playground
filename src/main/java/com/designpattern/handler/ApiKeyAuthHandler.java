package com.designpattern.handler;

import com.designpattern.model.AuthRequest;
import com.designpattern.model.AuthResponse;
import com.designpattern.utils.Constants;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ApiKeyAuthHandler extends AbstractAuthHandler {

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (Constants.API_KEY.equalsIgnoreCase(request.getType())) {
            if (request.getToken() != null && request.getToken().startsWith("APIKEY-")) {
                return new AuthResponse(Constants.SUCCESS, Constants.API_KEY_HANDLER, Constants.API_KEY_SUCCESS);
            }
            return new AuthResponse(Constants.FAILURE, Constants.API_KEY_HANDLER, Constants.INVALID_TOKEN);
        }
        // Not an API_KEY request — pass to next handler
        return super.authenticate(request);
    }

}
