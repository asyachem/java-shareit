package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.requester.id = :requesterId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequesterIdWithItems(Long requesterId);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items ORDER BY ir.created DESC")
    List<ItemRequest> findAllWithItems();

    // @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.id = :requestId ORDER BY ir.created DESC")
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.id = :requestId ORDER BY ir.created DESC")
    ItemRequest findByIdWithItems(@Param("requestId") Long requestId);
}
