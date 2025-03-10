package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StateBooking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingRequest request, Long userId) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new NotFoundException("Объект не найден"));
        List<Booking> approvedBookings = bookingRepository.findAllByStatusAndItemId(Status.APPROVED, item.getId());

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        if (request.getStart() == null || request.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания бронирования должны быть указаны");
        }

        if (request.getStart().isAfter(request.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может быть позже даты окончания");
        }

        if (request.getStart().isEqual(request.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может быть равна дате окончания");
        }

        for (Booking booking : approvedBookings) {
            if (isTimeOverlap(request.getStart(), request.getEnd(), booking.getStart(), booking.getEnd())) {
                throw new ValidationException("Время бронирования пересекается с уже существующим бронированием");
            }
        }

        Booking booking = BookingMapper.toBookingFromRequest(request, item, booker);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    @Override
    public BookingDto approvedBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        User owner = userRepository.findById(userId).orElseThrow(() -> new ValidationException("Пользователь не найден"));

        if (!booking.getItem().getOwner().equals(owner)) {
            throw new ValidationException("Нет доступа к редактированию статуса бронирования");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование еще не одобрено");
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

        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new ValidationException("Нет доступа к информации по бронированию");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllUserBookingByState(Long userId, StateBooking state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);

        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }


}
