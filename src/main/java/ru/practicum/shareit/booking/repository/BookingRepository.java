package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(@Param("bookerId") Long bookerId);

    List<Booking> findAllByStatusAndItemId(Status status, Long itemId);
}
