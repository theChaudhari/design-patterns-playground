package com.designpattern.handler;

import com.designpattern.model.AuthRequest;
import com.designpattern.model.AuthResponse;
import com.designpattern.utils.Constants;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class OAuthHandler extends AbstractAuthHandler {

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (Constants.OAUTH.equalsIgnoreCase(request.getType())) {
            if (request.getToken() != null && request.getToken().startsWith("oauth_")) {
                return new AuthResponse(Constants.SUCCESS, Constants.OAUTH_HANDLER, Constants.OAUTH_SUCCESS);
            }
            return new AuthResponse(Constants.FAILURE, Constants.OAUTH_HANDLER, Constants.INVALID_TOKEN);
        }
        // Not an OAuth request — pass to next handler (end of chain)
        return super.authenticate(request);
    }

}