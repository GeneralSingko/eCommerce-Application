package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void get_items_by_name_happy_path() {
        Item item = new Item();
        item.setName("testItem");

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepo.findByName("testItem")).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> list = response.getBody();

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals("testItem", list.get(0).getName());
    }

    @Test
    public void get_items_by_name_negative_path() {
        when(itemRepo.findByName("testItem")).thenReturn(null);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
