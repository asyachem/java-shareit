package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getItemsFromUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return itemRepository.findByOwnerIdWithComments(user.getId()).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findByIdWithComments(id).orElseThrow(() -> new NotFoundException("Объект не найден"));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getSearchedItems(String text) {
        return itemRepository.findByNameOrDescriptionWithComments(text).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto createItem(Item item, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, Item newItemRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findByIdWithComments(itemId).orElseThrow(() -> new NotFoundException("Объект не найден"));

        if (!item.getOwner().equals(user)) {
            throw new ValidationException("Этот пользователь не может редактировать объект");
        }

        Item updatedItem = ItemMapper.updateItemField(item, newItemRequest);
        updatedItem = itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, String text) {
        Item item = itemRepository.findByIdWithComments(itemId).orElseThrow(() -> new NotFoundException("Объект не найден"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> booking = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId());
        boolean isThisBooker = false;

        for (Booking bookingItem : booking) {
            if (bookingItem.getItem().equals(item)) {
                isThisBooker = true;

                if (bookingItem.getStatus() == Status.APPROVED && bookingItem.getEnd().isAfter(LocalDateTime.now())) {
                    throw new ValidationException("Этот пользователь не может добавить комментарий");
                }

                break;
            }
        }

        if (!isThisBooker) {
            throw new ValidationException("Этот пользователь не может добавить комментарий");
        }

        Comment comment = CommentMapper.toComment(item, text, user);
        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }
}
