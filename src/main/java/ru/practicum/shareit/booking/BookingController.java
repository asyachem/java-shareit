package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookingByState(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") StateBooking state) {
        return bookingService.getAllUserBookingByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") StateBooking state) {
        return bookingService.getAllUserBookingByState(userId, state);
    }

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingRequest booking, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@PathVariable("bookingId") Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam("approved") Boolean approved) {
        return bookingService.approvedBooking(bookingId, userId, approved);
    }
}
