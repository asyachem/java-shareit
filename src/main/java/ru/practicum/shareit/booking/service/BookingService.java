package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.StateBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingRequest booking, Long userId);

    BookingDto approvedBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getAllUserBookingByState(Long userId, StateBooking state);
}
