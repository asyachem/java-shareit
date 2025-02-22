package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getItems() {
        return itemRepository.getItems().stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto getItemById(long id) {
        Item item = itemRepository.getItemById(id);

        if (item == null) {
            throw new RuntimeException("Такой вещи не существует");
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getSearchedItems(String text) {
        return itemRepository.getSearchedItems(text).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto createItem(Item item, long userId) {
        UserDto user = userService.getUserById(userId);

        if (user == null) {
            throw new RuntimeException("Такого пользователя не существует");
        }

        if (item.getAvailable() == null) {
            throw new RuntimeException("Необходимо указать статус вещи");
        }

        if (item.getName().isBlank()) {
            throw new RuntimeException("Необходимо указать название вещи");
        }

        if (item.getDescription().isBlank()) {
            throw new RuntimeException("Необходимо указать описание вещи");
        }

        item.setOwner(UserMapper.mapToUser(user));
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, Item newItemRequest) {
        UserDto user = userService.getUserById(userId);
        Item item = itemRepository.getItemById(itemId);

        if (user == null) {
            throw new RuntimeException("Такого пользователя не существует");
        }

        if (item == null) {
            throw new RuntimeException("Такой вещи не существует");
        }

        if (!item.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Этот пользователь не может редактировать объект");
        }

        Item updatedItem = ItemMapper.updateItemField(item, newItemRequest);
        updatedItem = itemRepository.updateItem(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }
}
