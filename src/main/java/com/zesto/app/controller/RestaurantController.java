package com.zesto.app.controller;

import com.zesto.app.entity.MenuItemEntity;
import com.zesto.app.entity.RestaurantEntity;
import com.zesto.app.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final DataService dataService;

    @Autowired
    public RestaurantController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public List<RestaurantEntity> getRestaurants(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "false") boolean vegOnly
    ) {
        List<RestaurantEntity> list = dataService.getRestaurants();

        return list.stream().filter(r -> {
            if (vegOnly && !r.isPureVeg()) return false;

            if (category != null && !category.trim().isEmpty()) {
                String catLower = category.toLowerCase().trim();
                boolean matchCuisine = r.getCuisine().stream().anyMatch(c -> c.toLowerCase().contains(catLower));
                boolean matchMenu = r.getMenu().stream().anyMatch(m -> m.getCategory().toLowerCase().contains(catLower));
                if (!matchCuisine && !matchMenu) return false;
            }

            if (search != null && !search.trim().isEmpty()) {
                String q = search.toLowerCase().trim();
                boolean matchName = r.getName().toLowerCase().contains(q);
                boolean matchCuisine = r.getCuisine().stream().anyMatch(c -> c.toLowerCase().contains(q));
                boolean matchDish = r.getMenu().stream().anyMatch(m -> m.getName().toLowerCase().contains(q));
                return matchName || matchCuisine || matchDish;
            }

            return true;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantEntity> getRestaurantById(@PathVariable String id) {
        Optional<RestaurantEntity> found = dataService.findRestaurantById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RestaurantEntity> addRestaurant(@RequestBody RestaurantEntity req) {
        String newId = "rest-" + System.currentTimeMillis();
        req.setId(newId);
        if (req.getImage() == null || req.getImage().isEmpty()) {
            req.setImage("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&auto=format&fit=crop&q=80");
        }
        if (req.getRating() == 0) req.setRating(4.5);
        if (req.getRatingCount() == null) req.setRatingCount("NEW");
        if (req.getDeliveryTime() == null) req.setDeliveryTime("25-30 min");
        if (req.getLocation() == null) req.setLocation("Bangalore Central");
        if (req.getOffer() == null) req.setOffer("20% OFF");
        req.setOpen(true);
        if (req.getMenu() == null) req.setMenu(new ArrayList<>());

        RestaurantEntity saved = dataService.saveRestaurant(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{id}/menu")
    public ResponseEntity<MenuItemEntity> addMenuItem(@PathVariable String id, @RequestBody MenuItemEntity item) {
        Optional<RestaurantEntity> found = dataService.findRestaurantById(id);
        if (found.isEmpty()) return ResponseEntity.notFound().build();

        RestaurantEntity restaurant = found.get();
        item.setId("item-" + System.currentTimeMillis());
        if (item.getCategory() == null) item.setCategory("Main Course");
        if (item.getImage() == null) item.setImage("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&auto=format&fit=crop&q=80");
        item.setRating(4.5);
        item.setRatingCount(1);
        item.setRestaurant(restaurant);

        restaurant.getMenu().add(item);
        dataService.saveRestaurant(restaurant);

        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{id}/menu/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteMenuItem(@PathVariable String id, @PathVariable String itemId) {
        Optional<RestaurantEntity> found = dataService.findRestaurantById(id);
        if (found.isEmpty()) return ResponseEntity.notFound().build();

        RestaurantEntity restaurant = found.get();
        restaurant.getMenu().removeIf(m -> m.getId().equals(itemId));
        dataService.deleteMenuItem(itemId);
        dataService.saveRestaurant(restaurant);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Item deleted from database");
        return ResponseEntity.ok(res);
    }
}
