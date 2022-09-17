package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JPAItemEMTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    ItemRepository repository;

    @Test
    public void listOfItemsIsEmpty() {
        List<Item> allItems = repository.findAll();
        assertThat(allItems).isEmpty();
    }

    @Test
    public void saveItem() {
        Item item = repository.save(Item.builder()
                .name("Hammer")
                .description("Hammer for you")
                .available(true)
                .build());
        assertThat(item).hasFieldOrPropertyWithValue("name", "Hammer");
        assertThat(item).hasFieldOrPropertyWithValue("description", "Hammer for you");
        assertThat(item).hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    public void searchItems() {
        Item item = Item.builder()
                .name("Hammer")
                .description("Hammer for you")
                .available(true)
                .build();
        entityManager.persist(item);
        Item item2 = Item.builder()
                .name("Test")
                .description("Text for you")
                .available(true)
                .build();
        entityManager.persist(item2);
        Item item3 = Item.builder()
                .name("Car")
                .description("Cat for you")
                .available(true)
                .build();
        entityManager.persist(item3);
        List<Item> items = repository.search("Car");
        assertThat(items).hasSize(1).contains(item3);
        List<Item> items2 = repository.search("Text");
        assertThat(items2).hasSize(1).contains(item2);

    }


}
