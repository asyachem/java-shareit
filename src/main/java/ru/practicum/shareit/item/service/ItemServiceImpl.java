package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItemsFromUser(Long userId) {
        User user = userRepository.getUserById(userId);
        return itemRepository.getItemsFromUser(user.getId()).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    @Override
    public List<ItemDto> getSearchedItems(String text) {
        return itemRepository.getSearchedItems(text).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto createItem(Item item, Long userId) {
        User user = userRepository.getUserById(userId);

        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, Item newItemRequest) {
        User user = userRepository.getUserById(userId);
        Item item = itemRepository.getItemById(itemId);

        if (!item.getOwner().getId().equals(user.getId())) {
            throw new ValidationException("Этот пользователь не может редактировать объект");
        }

        Item updatedItem = ItemMapper.updateItemField(item, newItemRequest);
        updatedItem = itemRepository.updateItem(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }
}
