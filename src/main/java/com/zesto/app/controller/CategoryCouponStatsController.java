package com.zesto.app.controller;

import com.zesto.app.entity.*;
import com.zesto.app.model.AdminStats;
import com.zesto.app.model.Order;
import com.zesto.app.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CategoryCouponStatsController {

    private final DataService dataService;

    @Autowired
    public CategoryCouponStatsController(DataService dataService) {
        this.dataService = dataService;
    }

    // Categories
    @GetMapping("/categories")
    public List<FoodCategoryEntity> getCategories() {
        return dataService.getCategories();
    }

    // Coupons
    @GetMapping("/coupons")
    public List<CouponEntity> getCoupons() {
        return dataService.getCoupons();
    }

    @PostMapping("/coupons/apply")
    public ResponseEntity<Map<String, Object>> applyCoupon(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        Number cartAmountNum = (Number) body.get("cartAmount");
        int cartAmount = cartAmountNum != null ? cartAmountNum.intValue() : 0;

        Map<String, Object> response = new HashMap<>();

        if (code == null) {
            response.put("success", false);
            response.put("message", "Coupon code required");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<CouponEntity> couponOpt = dataService.findCouponByCode(code.trim());

        if (couponOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid coupon code");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        CouponEntity coupon = couponOpt.get();
        if (cartAmount < coupon.getMinOrder()) {
            response.put("success", false);
            response.put("message", "Minimum order amount for " + coupon.getCode() + " is ₹" + coupon.getMinOrder());
            return ResponseEntity.badRequest().body(response);
        }

        int discount = 0;
        if (coupon.isFreeDelivery()) {
            discount = 35;
        } else {
            discount = Math.min(Math.round((cartAmount * coupon.getDiscountPercentage()) / 100.0f), coupon.getMaxDiscount());
        }

        response.put("success", true);
        response.put("coupon", coupon);
        response.put("discountAmount", discount);
        response.put("message", "Coupon '" + coupon.getCode() + "' applied successfully!");
        return ResponseEntity.ok(response);
    }

    // Orders
    @GetMapping("/orders")
    public List<OrderEntity> getOrders() {
        return dataService.getOrders();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable String id) {
        Optional<OrderEntity> found = dataService.findOrderById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order req) {
        String newId = "ORD-" + (1000 + new Random().nextInt(9000));
        OrderEntity order = new OrderEntity();
        order.setOrderId(newId);
        order.setCustomerName(req.getCustomerName() != null ? req.getCustomerName() : "Valued Customer");
        order.setCustomerPhone(req.getCustomerPhone() != null ? req.getCustomerPhone() : "+91 98765 43210");
        order.setDeliveryAddress(req.getDeliveryAddress() != null ? req.getDeliveryAddress() : "123 Swiggy Street, Bangalore");
        order.setRestaurantId(req.getRestaurantId());
        order.setRestaurantName(req.getRestaurantName());
        order.setItemTotal(req.getItemTotal());
        order.setTaxesAndFee(req.getTaxesAndFee());
        order.setDiscount(req.getDiscount());
        order.setGrandTotal(req.getGrandTotal());
        order.setPaymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : "UPI");
        order.setPaymentStatus("Paid");
        order.setStatus("Placed");
        order.setPlacedAt(Instant.now().toString());
        order.setEtaMinutes(30);

        List<RiderEntity> riders = dataService.getRiders();
        if (!riders.isEmpty()) {
            RiderEntity rider = riders.get(new Random().nextInt(riders.size()));
            order.setRiderId(rider.getId());
            order.setRiderName(rider.getName());
            order.setRiderPhone(rider.getPhone());
            order.setRiderRating(rider.getRating());
            order.setRiderVehicle(rider.getVehicle());
            order.setRiderAvatar(rider.getAvatar());
        }

        if (req.getItems() != null) {
            for (Order.OrderItem item : req.getItems()) {
                OrderItemEntity itemEntity = new OrderItemEntity(item.getId(), item.getName(), item.getPrice(), item.getQuantity());
                itemEntity.setOrder(order);
                order.getItems().add(itemEntity);
            }
        }

        OrderEntity savedOrder = dataService.saveOrder(order);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("order", savedOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        Optional<OrderEntity> found = dataService.findOrderById(id);
        if (found.isEmpty()) return ResponseEntity.notFound().build();

        OrderEntity order = found.get();
        order.setStatus(newStatus);
        if ("Delivered".equalsIgnoreCase(newStatus)) {
            order.setEtaMinutes(0);
        } else if ("On The Way".equalsIgnoreCase(newStatus)) {
            order.setEtaMinutes(10);
        } else if ("Preparing".equalsIgnoreCase(newStatus)) {
            order.setEtaMinutes(20);
        }

        OrderEntity updated = dataService.saveOrder(order);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("order", updated);
        return ResponseEntity.ok(res);
    }

    // Stats & Fleet
    @GetMapping("/stats")
    public AdminStats getStats() {
        return dataService.getAdminStats();
    }

    @GetMapping("/riders")
    public List<RiderEntity> getRiders() {
        return dataService.getRiders();
    }
}
