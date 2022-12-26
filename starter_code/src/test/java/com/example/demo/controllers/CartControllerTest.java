package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void add_to_cart_happy_path() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1);
        request.setQuantity(5);

        User user = new User();
        user.setCart(new Cart());

        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(10));

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(item));

        final ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();

        assertNotNull(cart);
        assertFalse(cart.getItems().isEmpty());
        assertEquals(item.getPrice(), cart.getItems().get(0).getPrice());
        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())), cart.getTotal());
    }

    @Test
    public void add_to_cart_negative_path() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1);
        request.setQuantity(5);

        when(userRepo.findByUsername("test")).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_happy_path() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1);
        request.setQuantity(2);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(10));

        Cart cart = new Cart();
        cart.addItem(item);
        cart.addItem(item);

        User user = new User();
        user.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(item));

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart responseCart = response.getBody();

        assertNotNull(responseCart);
        assertTrue(responseCart.getItems().isEmpty());
        assertEquals(BigDecimal.valueOf(0), responseCart.getTotal());
    }

    @Test
    public void remove_from_cart_negative_path() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1);
        request.setQuantity(2);

        when(userRepo.findByUsername("test")).thenReturn(new User());
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
