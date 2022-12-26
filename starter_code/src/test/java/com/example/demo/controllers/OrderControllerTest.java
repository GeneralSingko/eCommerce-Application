package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submit_happy_path() {
        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(10));

        Cart cart = new Cart();
        cart.addItem(item);

        User user = new User();
        user.setUsername("test");
        user.setCart(cart);
        cart.setUser(user);

        when(userRepo.findByUsername("test")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();

        assertNotNull(userOrder);
        assertEquals(1, userOrder.getItems().size());
        assertEquals(BigDecimal.valueOf(10), userOrder.getTotal());
        assertEquals("test", userOrder.getUser().getUsername());
    }

    @Test
    public void submit_negative_path() {
        when(userRepo.findByUsername("test")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
