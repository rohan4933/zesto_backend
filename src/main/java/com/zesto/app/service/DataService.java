package com.zesto.app.service;

import com.zesto.app.entity.*;
import com.zesto.app.model.*;
import com.zesto.app.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class DataService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final CouponRepository couponRepository;
    private final RiderRepository riderRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<WebSocketSession> activeSessions = new CopyOnWriteArrayList<>();

    @Autowired
    public DataService(
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository,
            OrderRepository orderRepository,
            CategoryRepository categoryRepository,
            CouponRepository couponRepository,
            RiderRepository riderRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.couponRepository = couponRepository;
        this.riderRepository = riderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addSession(WebSocketSession session) {
        activeSessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        activeSessions.remove(session);
    }

    public void broadcastMessage(String jsonPayload) {
        for (WebSocketSession session : activeSessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonPayload));
                } catch (IOException e) {
                    // Ignore broken connections
                }
            }
        }
    }

    @PostConstruct
    @Transactional
    public void initDatabaseSeed() {
        // Seed Users if table is empty
        if (userRepository.count() == 0) {
            userRepository.save(new UserEntity("usr-1", "Aarav Mehta", "customer@zesto.com", passwordEncoder.encode("password123"), "+91 98765 43210", "CUSTOMER"));
            userRepository.save(new UserEntity("usr-2", "Zesto Super Admin", "admin@zesto.com", passwordEncoder.encode("admin123"), "+91 99999 88888", "ADMIN"));
            System.out.println("✅ Seeded default users in Database (Customer & Admin)");
        }

        // Seed Categories if table is empty
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new FoodCategoryEntity("1", "Biryani", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=300&auto=format&fit=crop&q=80"));
            categoryRepository.save(new FoodCategoryEntity("2", "Pizzas", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=300&auto=format&fit=crop&q=80"));
            categoryRepository.save(new FoodCategoryEntity("3", "Burgers", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300&auto=format&fit=crop&q=80"));
            categoryRepository.save(new FoodCategoryEntity("4", "Chinese", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=300&auto=format&fit=crop&q=80"));
            categoryRepository.save(new FoodCategoryEntity("5", "South Indian", "https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=300&auto=format&fit=crop&q=80"));
            categoryRepository.save(new FoodCategoryEntity("6", "Desserts", "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=300&auto=format&fit=crop&q=80"));
            System.out.println("✅ Seeded Food Categories in Database");
        }

        // Seed Coupons if table is empty
        if (couponRepository.count() == 0) {
            couponRepository.save(new CouponEntity("ZESTO50", 50, 120, 199, false, "50% OFF up to ₹120 on orders above ₹199"));
            couponRepository.save(new CouponEntity("SWIGGYSTYLE", 40, 100, 149, false, "40% OFF up to ₹100"));
            couponRepository.save(new CouponEntity("FREEDEL", 100, 40, 99, true, "Free Delivery on all orders"));
            couponRepository.save(new CouponEntity("WELCOME200", 60, 200, 399, false, "Flat 60% OFF up to ₹200 for new foodies"));
            System.out.println("✅ Seeded Coupons in Database");
        }

        // Seed Riders if table is empty
        if (riderRepository.count() == 0) {
            riderRepository.save(new RiderEntity("rider-1", "Ramesh Kumar", "+91 98765 43210", 4.9, "KA-01-EQ-4821", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80"));
            riderRepository.save(new RiderEntity("rider-2", "Vikram Singh", "+91 98123 45678", 4.8, "KA-03-HF-9012", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80"));
            riderRepository.save(new RiderEntity("rider-3", "Aniket Sharma", "+91 99887 76655", 4.7, "KA-05-AB-1234", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80"));
            System.out.println("✅ Seeded Delivery Riders in Database");
        }

        // Seed Restaurants & Menu items if table is empty
        if (restaurantRepository.count() == 0) {
            // Rest 1
            RestaurantEntity rest1 = new RestaurantEntity();
            rest1.setId("rest-1");
            rest1.setName("Royal Biryani House");
            rest1.setImage("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600&auto=format&fit=crop&q=80");
            rest1.setCuisine(Arrays.asList("Hyderabadi Biryani", "Mughlai", "Kebab"));
            rest1.setRating(4.5);
            rest1.setRatingCount("5K+");
            rest1.setDeliveryTime("25-30 min");
            rest1.setCostForTwo(400);
            rest1.setLocation("Indiranagar, Bangalore");
            rest1.setPureVeg(false);
            rest1.setOffer("60% OFF up to ₹120");
            rest1.setOpen(true);

            MenuItemEntity m1 = new MenuItemEntity();
            m1.setId("item-101");
            m1.setName("Hyderabadi Dum Chicken Biryani");
            m1.setPrice(289);
            m1.setOriginalPrice(350);
            m1.setCategory("Biryani");
            m1.setVeg(false);
            m1.setBestseller(true);
            m1.setRating(4.7);
            m1.setRatingCount(320);
            m1.setDescription("Authentic Basmati rice slow cooked with tender chicken pieces, aromatic herbs & spices.");
            m1.setImage("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400&auto=format&fit=crop&q=80");
            m1.setRestaurant(rest1);

            MenuItemEntity m2 = new MenuItemEntity();
            m2.setId("item-102");
            m2.setName("Paneer Royal Dum Biryani");
            m2.setPrice(249);
            m2.setOriginalPrice(299);
            m2.setCategory("Biryani");
            m2.setVeg(true);
            m2.setBestseller(true);
            m2.setRating(4.4);
            m2.setRatingCount(180);
            m2.setDescription("Soft cottage cheese marinated in tikka spices layered with fragrant Basmati rice.");
            m2.setImage("https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=400&auto=format&fit=crop&q=80");
            m2.setRestaurant(rest1);

            rest1.getMenu().add(m1);
            rest1.getMenu().add(m2);
            restaurantRepository.save(rest1);

            // Rest 2
            RestaurantEntity rest2 = new RestaurantEntity();
            rest2.setId("rest-2");
            rest2.setName("Pizza Oven Supreme");
            rest2.setImage("https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&auto=format&fit=crop&q=80");
            rest2.setCuisine(Arrays.asList("Italian", "Pizzas", "Pasta"));
            rest2.setRating(4.3);
            rest2.setRatingCount("10K+");
            rest2.setDeliveryTime("20-25 min");
            rest2.setCostForTwo(500);
            rest2.setLocation("Koramangala, Bangalore");
            rest2.setPureVeg(false);
            rest2.setOffer("EVERYTHING @ ₹199");
            rest2.setOpen(true);

            MenuItemEntity m3 = new MenuItemEntity();
            m3.setId("item-201");
            m3.setName("Pepperoni & Cheese Loaded Pizza (Medium)");
            m3.setPrice(399);
            m3.setOriginalPrice(499);
            m3.setCategory("Pizzas");
            m3.setVeg(false);
            m3.setBestseller(true);
            m3.setRating(4.8);
            m3.setRatingCount(540);
            m3.setDescription("Classic mozzarella cheese with spicy pepperoni slices baked on sourdough base.");
            m3.setImage("https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400&auto=format&fit=crop&q=80");
            m3.setRestaurant(rest2);

            rest2.getMenu().add(m3);
            restaurantRepository.save(rest2);

            // Rest 3
            RestaurantEntity rest3 = new RestaurantEntity();
            rest3.setId("rest-3");
            rest3.setName("Burger & Craft Grill");
            rest3.setImage("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&auto=format&fit=crop&q=80");
            rest3.setCuisine(Arrays.asList("American", "Burgers", "Fries"));
            rest3.setRating(4.4);
            rest3.setRatingCount("2K+");
            rest3.setDeliveryTime("15-20 min");
            rest3.setCostForTwo(350);
            rest3.setLocation("HSR Layout, Bangalore");
            rest3.setPureVeg(false);
            rest3.setOffer("50% OFF up to ₹100");
            rest3.setOpen(true);

            MenuItemEntity m4 = new MenuItemEntity();
            m4.setId("item-301");
            m4.setName("Smokey BBQ Chicken Crunch Burger");
            m4.setPrice(219);
            m4.setOriginalPrice(279);
            m4.setCategory("Burgers");
            m4.setVeg(false);
            m4.setBestseller(true);
            m4.setRating(4.6);
            m4.setRatingCount(230);
            m4.setDescription("Crispy fried chicken patty glazed in house smoked BBQ sauce, sharp cheddar & lettuce.");
            m4.setImage("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&auto=format&fit=crop&q=80");
            m4.setRestaurant(rest3);

            rest3.getMenu().add(m4);
            restaurantRepository.save(rest3);
            System.out.println("✅ Seeded Restaurants and Menus in Database");
        }

        // Seed Initial Order if empty
        if (orderRepository.count() == 0) {
            OrderEntity ord1 = new OrderEntity();
            ord1.setOrderId("ORD-9821");
            ord1.setCustomerName("Aarav Mehta");
            ord1.setCustomerPhone("+91 98765 43210");
            ord1.setDeliveryAddress("Flat 402, Sunshine Apartments, Indiranagar, Bangalore");
            ord1.setRestaurantId("rest-1");
            ord1.setRestaurantName("Royal Biryani House");
            ord1.setItemTotal(897);
            ord1.setTaxesAndFee(45);
            ord1.setDiscount(100);
            ord1.setGrandTotal(842);
            ord1.setPaymentMethod("UPI (GPay)");
            ord1.setPaymentStatus("Paid");
            ord1.setStatus("On The Way");
            ord1.setPlacedAt(Instant.now().minusSeconds(900).toString());
            ord1.setEtaMinutes(12);

            RiderEntity rider = riderRepository.findAll().stream().findFirst().orElse(null);
            if (rider != null) {
                ord1.setRiderId(rider.getId());
                ord1.setRiderName(rider.getName());
                ord1.setRiderPhone(rider.getPhone());
                ord1.setRiderRating(rider.getRating());
                ord1.setRiderVehicle(rider.getVehicle());
                ord1.setRiderAvatar(rider.getAvatar());
            }

            OrderItemEntity item1 = new OrderItemEntity("item-101", "Hyderabadi Dum Chicken Biryani", 289, 2);
            item1.setOrder(ord1);
            ord1.getItems().add(item1);

            orderRepository.save(ord1);
            System.out.println("✅ Seeded initial Order in Database");
        }
    }

    // Database access methods
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    public List<RestaurantEntity> getRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<RestaurantEntity> findRestaurantById(String id) {
        return restaurantRepository.findById(id);
    }

    public RestaurantEntity saveRestaurant(RestaurantEntity rest) {
        return restaurantRepository.save(rest);
    }

    public void deleteMenuItem(String itemId) {
        menuItemRepository.deleteById(itemId);
    }

    public List<FoodCategoryEntity> getCategories() {
        return categoryRepository.findAll();
    }

    public List<CouponEntity> getCoupons() {
        return couponRepository.findAll();
    }

    public Optional<CouponEntity> findCouponByCode(String code) {
        return couponRepository.findByCodeIgnoreCase(code);
    }

    public List<RiderEntity> getRiders() {
        return riderRepository.findAll();
    }

    public List<OrderEntity> getOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc();
    }

    public Optional<OrderEntity> findOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    public AdminStats getAdminStats() {
        List<OrderEntity> allOrders = orderRepository.findAll();
        List<RestaurantEntity> allRests = restaurantRepository.findAll();
        List<RiderEntity> allRiders = riderRepository.findAll();

        AdminStats stats = new AdminStats();
        int totalOrders = allOrders.size();
        int totalRevenue = allOrders.stream().filter(o -> !o.getStatus().equalsIgnoreCase("Cancelled")).mapToInt(OrderEntity::getGrandTotal).sum();
        int activeOrders = (int) allOrders.stream().filter(o -> !o.getStatus().equalsIgnoreCase("Delivered") && !o.getStatus().equalsIgnoreCase("Cancelled")).count();
        int activeRests = (int) allRests.stream().filter(RestaurantEntity::isOpen).count();

        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("Placed", (int) allOrders.stream().filter(o -> o.getStatus().equalsIgnoreCase("Placed")).count());
        statusMap.put("Confirmed", (int) allOrders.stream().filter(o -> o.getStatus().equalsIgnoreCase("Confirmed")).count());
        statusMap.put("Preparing", (int) allOrders.stream().filter(o -> o.getStatus().equalsIgnoreCase("Preparing")).count());
        statusMap.put("On The Way", (int) allOrders.stream().filter(o -> o.getStatus().equalsIgnoreCase("On The Way")).count());
        statusMap.put("Delivered", (int) allOrders.stream().filter(o -> o.getStatus().equalsIgnoreCase("Delivered")).count());
        statusMap.put("Cancelled", (int) allOrders.stream().filter(o -> o.getStatus().equalsIgnoreCase("Cancelled")).count());

        stats.setTotalRevenue(totalRevenue);
        stats.setTotalOrders(totalOrders);
        stats.setActiveOrdersCount(activeOrders);
        stats.setActiveRestaurants(activeRests);
        stats.setActiveRiders(allRiders.size());
        stats.setOrdersByStatus(statusMap);
        return stats;
    }
}
