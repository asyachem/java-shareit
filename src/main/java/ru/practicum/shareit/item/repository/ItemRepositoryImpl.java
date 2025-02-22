package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> getItems() {
        return items.values();
    }

    @Override
    public Item getItemById(long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> getSearchedItems(String text) {
        List<Item> searchedItems = new ArrayList<>();

        items.values().stream().filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())).forEach(searchedItems::add);

        return searchedItems;
    }

    @Override
    public Item createItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }
}
