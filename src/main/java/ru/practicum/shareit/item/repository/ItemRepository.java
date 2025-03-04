package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item as i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND i.available = true")
    Collection<Item> findByNameOrDescription(@Param("text") String text);

    Collection<Item> findByOwnerId(Long userId);
}
