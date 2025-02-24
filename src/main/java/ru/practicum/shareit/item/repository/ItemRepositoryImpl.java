package ru.practicum.shareit.item.repository;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {
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

    @Override
    public Collection<Item> getItemsFromUser(Long userId) {
        List<Item> userItems = new ArrayList<>();

        items.values().stream().filter(elem -> Objects.equals(elem.getOwner().getId(), userId)).forEach(userItems::add);

        return userItems;
    }

    @Override
    public Item getItemById(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Такой вещи не существует");
        }
        return item;
    }

    @Override
    public Collection<Item> getSearchedItems(String text) {
        List<Item> searchedItems = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return searchedItems;
        }

        items.values().stream().filter(item -> item.getName().contains(text)).forEach(searchedItems::add);

        return searchedItems;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        validate(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        validate(item);
        items.put(item.getId(), item);
        return item;
    }

    private void validate(Item item) {
        var errors = validator.validate(item);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(",", errors.stream().map(elem -> "Поле " +
                    elem.getPropertyPath() + " " + elem.getMessage()).toList()));
        }
    }
}
