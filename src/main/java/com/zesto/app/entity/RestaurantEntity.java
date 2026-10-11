package com.zesto.app.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class RestaurantEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String image;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "restaurant_cuisines", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "cuisine")
    private List<String> cuisine = new ArrayList<>();

    private double rating;
    private String ratingCount;
    private String deliveryTime;
    private int costForTwo;
    private String location;
    private boolean isPureVeg;
    private String offer;
    private boolean isOpen = true;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MenuItemEntity> menu = new ArrayList<>();

    public RestaurantEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public List<String> getCuisine() { return cuisine; }
    public void setCuisine(List<String> cuisine) { this.cuisine = cuisine; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getRatingCount() { return ratingCount; }
    public void setRatingCount(String ratingCount) { this.ratingCount = ratingCount; }

    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }

    public int getCostForTwo() { return costForTwo; }
    public void setCostForTwo(int costForTwo) { this.costForTwo = costForTwo; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isPureVeg() { return isPureVeg; }
    public void setPureVeg(boolean pureVeg) { isPureVeg = pureVeg; }

    public String getOffer() { return offer; }
    public void setOffer(String offer) { this.offer = offer; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    public List<MenuItemEntity> getMenu() { return menu; }
    public void setMenu(List<MenuItemEntity> menu) { this.menu = menu; }
}
