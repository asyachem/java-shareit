package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import java.util.Collection;

public interface UserRepository {
    Collection<User> getUsers();
    boolean haveUser(String email);
    User getUserById(Long id);
    User createUser(User user);
    User updateUser(User newUser);
    void deleteUserById(Long id);
}
