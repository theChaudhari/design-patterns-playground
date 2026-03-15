package com.designpattern.controller;

import com.designpattern.factory.DeliveryFactory;
import com.designpattern.model.OrderRequest;
import com.designpattern.model.OrderResponse;
import com.designpattern.service.IDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/delivery")
@Tag(name = "Food Delivery API", description = "Factory Method Pattern — platform-based delivery routing")
public class DeliveryController {

    private final DeliveryFactory deliveryFactory;

    public DeliveryController(DeliveryFactory deliveryFactory) {
        this.deliveryFactory = deliveryFactory;
    }

    @Operation(summary = "Place a food delivery order", description = "Factory resolves the correct delivery service based on platform. " + "Supported: ZOMATO, SWIGGY, UBEREATS")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Order placed successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))), @ApiResponse(responseCode = "400", description = "Unsupported platform or missing fields")})
    @PostMapping("/order")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        log.info("Request received - POST /delivery/order - platform: {}, item: {}", request.getPlatform(), request.getItem());

        IDeliveryService service = deliveryFactory.getDeliveryService(request.getPlatform());
        OrderResponse response = service.placeOrder(request);

        log.info("Order placed successfully - platform: {}, estimatedTime: {}min, charge: ₹{}",
                response.getPlatform(), response.getEstimatedTimeMinutes(), response.getDeliveryCharge());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Compare all platforms", description = "Returns delivery time and charges for all supported platforms for quick comparison.")
    @ApiResponse(responseCode = "200", description = "Platform comparison fetched successfully")
    @GetMapping("/compare")
    public ResponseEntity<List<Map<String, Object>>> comparePlatforms(@Parameter(description = "Item to compare delivery for", example = "Burger") @RequestParam(defaultValue = "Food") String item) {

        log.info("Request received - GET /delivery/compare - item: {}", item);

        List<Map<String, Object>> comparison = List.of(Map.of("platform", "ZOMATO", "estimatedTime", "30 min", "deliveryCharge", "₹29.00", "partner", "Zomato Delivery"), Map.of("platform", "SWIGGY", "estimatedTime", "25 min", "deliveryCharge", "₹0.00", "partner", "Swiggy Genie"), Map.of("platform", "UBEREATS", "estimatedTime", "35 min", "deliveryCharge", "₹49.00", "partner", "Uber Eats Runner"));

        return ResponseEntity.ok(comparison);
    }

}