package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.StateBooking;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody BookingRequest requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

//    @GetMapping
//    public ResponseEntity<Object> getAllUserBookingByState(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                                           @RequestParam(defaultValue = "ALL") StateBooking state) {
//        log.info("Get all user {} bookings by state {}", userId, state);
//        return bookingClient.getAllUserBookingByState(userId, state);
//    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") StateBooking state) {
        log.info("Get all user {} bookings by state {}", userId, state);
        return bookingClient.getAllUserBooking(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@PathVariable("bookingId") long bookingId,
                                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam("approved") Boolean approved) {
        log.info("Approved {} booking {} user {}", approved, bookingId, userId);
        return bookingClient.approvedBooking(bookingId, userId, approved);
    }
}
