package ru.practicum.shareit.item.repository;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl {
    protected Map<Long, Item> items = new HashMap<>();

    private final Validator validator;

    private Long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Collection<Item> getItemsFromUser(Long userId) {
        List<Item> userItems = new ArrayList<>();

        items.values().stream().filter(elem -> Objects.equals(elem.getOwner().getId(), userId)).forEach(userItems::add);

        return userItems;
    }

    public Item getItemById(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Такой вещи не существует");
        }
        return item;
    }

    public Collection<Item> getSearchedItems(String text) {
        List<Item> searchedItems = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return searchedItems;
        }

        items.values().stream().filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable()).forEach(searchedItems::add);

        return searchedItems;
    }

    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }
}
