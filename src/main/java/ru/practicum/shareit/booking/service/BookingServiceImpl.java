package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDto booking, Long userId) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Объект не найден"));

        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания бронирования должны быть указаны");
        }

        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может быть позже даты окончания");
        }

        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может быть равна дате окончания");
        }

        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        bookingRepository.save(BookingMapper.toBooking(booking, item));
        return booking;
    }

    @Override
    public BookingDto approvedBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!booking.getItem().getOwner().equals(owner)) {
            throw new ValidationException("Нет доступа к редактированию статуса бронирования");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!booking.getBooker().equals(user) || !booking.getItem().getOwner().equals(user)) {
            throw new ValidationException("Нет доступа к информации по бронированию");
        }

        return BookingMapper.toBookingDto(booking);
    }


}
