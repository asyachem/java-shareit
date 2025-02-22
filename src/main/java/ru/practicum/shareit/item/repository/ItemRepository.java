package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Collection<Item> getItems();
    Item getItemById(long id);
    Collection<Item> getSearchedItems(String text);
    Item createItem(Item item);
    Item updateItem(Item item);
}
