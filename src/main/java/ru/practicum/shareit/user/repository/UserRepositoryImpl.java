package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl {
    protected Map<Long, User> users = new HashMap<>();

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public boolean haveUser(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User newUser) {
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public void deleteUserById(Long id) {
        users.remove(id);
    }
}
