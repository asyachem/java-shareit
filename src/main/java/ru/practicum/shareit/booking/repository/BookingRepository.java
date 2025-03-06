package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdOrderByStartDesc(@Param("bookerId") Long bookerId);
}
