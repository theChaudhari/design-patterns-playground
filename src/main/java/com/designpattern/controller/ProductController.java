package com.designpattern.controller;

import com.designpattern.model.Product;
import com.designpattern.model.UserRequestInfo;
import com.designpattern.proxy.RateLimitProxy;
import com.designpattern.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/product")
@Tag(name = "Product API", description = "Rate limited product endpoints — max 5 requests per minute per user")
public class ProductController {


    private final RateLimitProxy rateLimitProxy;

    public ProductController(RateLimitProxy rateLimitProxy) {
        this.rateLimitProxy = rateLimitProxy;
    }

    @Operation(summary = "Get product by ID", description = "Fetches a product. Rate limited to 5 requests per minute per user. " + "Returns cached result on repeat calls — no DB hit.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Product fetched successfully", headers = {@Header(name = "X-RateLimit-Limit", description = "Max requests allowed per window", schema = @Schema(type = "integer")), @Header(name = "X-RateLimit-Remaining", description = "Requests remaining in current window", schema = @Schema(type = "integer"))}, content = @Content(schema = @Schema(implementation = Product.class))), @ApiResponse(responseCode = "400", description = "Missing X-User-Id header"), @ApiResponse(responseCode = "404", description = "Product not found"), @ApiResponse(responseCode = "429", description = "Rate limit exceeded — too many requests")})
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@Parameter(description = "Product ID", example = "1", required = true) @PathVariable Long id,

                                              @Parameter(description = "Unique user identifier", example = "user123", required = true) @RequestHeader(Constants.HEADER_USER_ID) String userId) {

        log.info("Request received - GET /api/product/{} - userId: {}", id, userId);

        Product product = rateLimitProxy.getProductById(id, userId);
        UserRequestInfo info = rateLimitProxy.getRateLimitInfo(userId);
        int remaining = Constants.MAX_REQUESTS_PER_WINDOW - info.getRequestCount();

        log.info("Request successful - userId: {}, remaining: {}", userId, remaining);

        return ResponseEntity.ok().header(Constants.HEADER_RATE_LIMIT, String.valueOf(Constants.MAX_REQUESTS_PER_WINDOW)).header(Constants.HEADER_RATE_REMAINING, String.valueOf(remaining)).body(product);
    }

    @Operation(summary = "Get rate limit status", description = "Returns current request count and window start time for the given user.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Rate limit status fetched successfully", content = @Content(schema = @Schema(implementation = UserRequestInfo.class))), @ApiResponse(responseCode = "400", description = "Missing X-User-Id header")})
    @GetMapping("/rate-limit-status")
    public ResponseEntity<UserRequestInfo> getRateLimitStatus(@Parameter(description = "Unique user identifier", example = "user123", required = true) @RequestHeader(Constants.HEADER_USER_ID) String userId) {

        log.info("Rate limit status requested - userId: {}", userId);
        UserRequestInfo info = rateLimitProxy.getRateLimitInfo(userId);
        return ResponseEntity.ok(info);
    }

}