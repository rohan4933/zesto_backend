package com.zesto.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class FoodCategoryEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String image;

    public FoodCategoryEntity() {}

    public FoodCategoryEntity(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
