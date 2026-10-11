package com.zesto.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "coupons")
public class CouponEntity {

    @Id
    @Column(length = 64)
    private String code;

    private int discountPercentage;
    private int maxDiscount;
    private int minOrder;
    private boolean isFreeDelivery;

    @Column(length = 500)
    private String description;

    public CouponEntity() {}

    public CouponEntity(String code, int discountPercentage, int maxDiscount, int minOrder, boolean isFreeDelivery, String description) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.maxDiscount = maxDiscount;
        this.minOrder = minOrder;
        this.isFreeDelivery = isFreeDelivery;
        this.description = description;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }

    public int getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(int maxDiscount) { this.maxDiscount = maxDiscount; }

    public int getMinOrder() { return minOrder; }
    public void setMinOrder(int minOrder) { this.minOrder = minOrder; }

    public boolean isFreeDelivery() { return isFreeDelivery; }
    public void setFreeDelivery(boolean freeDelivery) { isFreeDelivery = freeDelivery; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
