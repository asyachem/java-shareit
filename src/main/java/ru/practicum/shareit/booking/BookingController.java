package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto booking, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new ValidationException("Неверно указана дата бронирования");
        }

        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@PathVariable("bookingId") Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam("approved") Boolean approved) {
        return bookingService.approvedBooking(bookingId, userId, approved);
    }
}
