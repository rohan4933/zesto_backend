package com.zesto.app.repository;

import com.zesto.app.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, String> {
    List<MenuItemEntity> findByRestaurantId(String restaurantId);
}
