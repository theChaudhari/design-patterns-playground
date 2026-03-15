package com.designpattern.proxy;

import com.designpattern.exception.RateLimitExceededException;
import com.designpattern.model.Product;
import com.designpattern.model.UserRequestInfo;
import com.designpattern.service.IProductService;
import com.designpattern.service.ProductServiceImpl;
import com.designpattern.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Primary
public class RateLimitProxy implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitProxy.class);

    private final ProductServiceImpl realService;

    private final ConcurrentHashMap<String, UserRequestInfo> userRequestMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Product>           productCache   = new ConcurrentHashMap<>();

    public RateLimitProxy(ProductServiceImpl realService) {
        this.realService = realService;
        log.info("RateLimitProxy initialized - max {} requests per {}ms window",
                Constants.MAX_REQUESTS_PER_WINDOW, Constants.WINDOW_DURATION_MS);
    }

    @Override
    public Product getProductById(Long id) {
        throw new UnsupportedOperationException("Use getProductById(id, userId) via controller");
    }

    public Product getProductById(Long id, String userId) {
        validateUserId(userId);
        enforceRateLimit(userId);
        log.info("Rate limit passed - userId: {}, productId: {}", userId, id);
        return getFromCacheOrReal(id);
    }

    public UserRequestInfo getRateLimitInfo(String userId) {
        validateUserId(userId);
        return userRequestMap.getOrDefault(userId, new UserRequestInfo(userId));
    }

    private Product getFromCacheOrReal(Long id) {
        if (productCache.containsKey(id)) {
            log.info("Cache HIT - productId: {}", id);
            return productCache.get(id);
        }
        log.info("Cache MISS - productId: {}, fetching from real service", id);
        Product product = realService.getProductById(id);
        productCache.put(id, product);
        log.info("Product cached - productId: {}", id);
        return product;
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            log.warn("Request rejected - missing userId");
            throw new IllegalArgumentException(Constants.MISSING_USER_ID);
        }
    }

    private void enforceRateLimit(String userId) {
        UserRequestInfo info = userRequestMap.computeIfAbsent(userId, UserRequestInfo::new);

        if (info.isWindowExpired(Constants.WINDOW_DURATION_MS)) {
            log.info("Rate limit window reset - userId: {}", userId);
            info.resetWindow();
        }

        if (info.getRequestCount() >= Constants.MAX_REQUESTS_PER_WINDOW) {
            long retryAfterMs = info.getRetryAfterMs(Constants.WINDOW_DURATION_MS);
            log.warn("Rate limit exceeded - userId: {}, count: {}, retryAfter: {}ms",
                    userId, info.getRequestCount(), retryAfterMs);
            throw new RateLimitExceededException(
                    String.format(Constants.RATE_LIMIT_EXCEEDED, Constants.MAX_REQUESTS_PER_WINDOW),
                    retryAfterMs
            );
        }

        info.incrementCount();
        log.info("Request allowed - userId: {}, count: {}/{}", userId,
                info.getRequestCount(), Constants.MAX_REQUESTS_PER_WINDOW);
    }

}