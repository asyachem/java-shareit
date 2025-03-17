package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = {"items"})
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    @EntityGraph(attributePaths = {"items"})
    List<ItemRequest> findAllByOrderByCreatedDesc();

    @EntityGraph(attributePaths = {"items"})
    Optional<ItemRequest> findById(Long requestId);
}
