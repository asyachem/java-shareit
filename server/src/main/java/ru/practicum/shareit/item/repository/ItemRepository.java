package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.comments WHERE ((LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))) OR (LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))) AND i.available = true")
    Collection<Item> findByNameOrDescriptionWithComments(@Param("text") String text);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.comments WHERE i.owner.id = :userId")
    Collection<Item> findByOwnerIdWithComments(@Param("userId") Long userId);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments WHERE i.id = :id")
    Optional<Item> findByIdWithComments(@Param("id") long id);
}
