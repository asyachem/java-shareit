package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
//    @Query("SELECT i FROM Item as i " +
//            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND i.available = true")
//    Collection<Item> findByNameOrDescription(@Param("text") String text);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.comments WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    Collection<Item> findByNameOrDescriptionWithComments(@Param("text") String text);

    //Collection<Item> findByOwnerId(Long userId);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments WHERE i.owner.id = :userId")
    Collection<Item> findByOwnerIdWithComments(@Param("userId") Long userId);

    //Item findById(long id);

    @Query("SELECT i FROM Item i JOIN FETCH Comment c ON i.id = c.item.id WHERE i.id = :id")
    Optional<Item> findByIdWithComments(@Param("id") long id);
}
