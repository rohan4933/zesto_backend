package com.zesto.app.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(length = 64)
    private String orderId;

    private String customerName;
    private String customerPhone;

    @Column(length = 500)
    private String deliveryAddress;

    private String restaurantId;
    private String restaurantName;

    private int itemTotal;
    private int taxesAndFee;
    private int discount;
    private int grandTotal;
    private String paymentMethod;
    private String paymentStatus;
    private String status;
    private String placedAt;
    private int etaMinutes;

    // Embedded / Associated Rider details
    private String riderId;
    private String riderName;
    private String riderPhone;
    private double riderRating;
    private String riderVehicle;

    @Column(length = 1000)
    private String riderAvatar;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItemEntity> items = new ArrayList<>();

    public OrderEntity() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public int getItemTotal() { return itemTotal; }
    public void setItemTotal(int itemTotal) { this.itemTotal = itemTotal; }

    public int getTaxesAndFee() { return taxesAndFee; }
    public void setTaxesAndFee(int taxesAndFee) { this.taxesAndFee = taxesAndFee; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public int getGrandTotal() { return grandTotal; }
    public void setGrandTotal(int grandTotal) { this.grandTotal = grandTotal; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPlacedAt() { return placedAt; }
    public void setPlacedAt(String placedAt) { this.placedAt = placedAt; }

    public int getEtaMinutes() { return etaMinutes; }
    public void setEtaMinutes(int etaMinutes) { this.etaMinutes = etaMinutes; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }

    public String getRiderPhone() { return riderPhone; }
    public void setRiderPhone(String riderPhone) { this.riderPhone = riderPhone; }

    public double getRiderRating() { return riderRating; }
    public void setRiderRating(double riderRating) { this.riderRating = riderRating; }

    public String getRiderVehicle() { return riderVehicle; }
    public void setRiderVehicle(String riderVehicle) { this.riderVehicle = riderVehicle; }

    public String getRiderAvatar() { return riderAvatar; }
    public void setRiderAvatar(String riderAvatar) { this.riderAvatar = riderAvatar; }

    public List<OrderItemEntity> getItems() { return items; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; }
}
