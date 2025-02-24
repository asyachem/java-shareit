package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Item getItemById(long id);

    Collection<Item> getSearchedItems(String text);

    Item createItem(Item item);

    Item updateItem(Item item);

    Collection<Item> getItemsFromUser(Long userId);
}
