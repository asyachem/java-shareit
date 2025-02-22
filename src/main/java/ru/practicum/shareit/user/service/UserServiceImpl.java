package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    EmailValidator emailValidator = EmailValidator.getInstance();

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getUserById(id);

        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto createUser(User user) {
        if (user.getEmail() == null) {
            throw new RuntimeException("Отсутствует email у пользователя");
        }

        if (!emailValidator.isValid(user.getEmail())) {
            throw new RuntimeException("Неверно указан email");
        }

        if (userRepository.haveUser(user.getEmail())) {
            throw new RuntimeException(String.format("Этот E-mail \"%s\" уже используется", user.getEmail()));
        }

        return UserMapper.mapToUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(User newUserRequest, Long userId) {
        if (userId == null) {
            throw new RuntimeException("Id пользователя должен быть указан");
        }

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        if (newUserRequest.getEmail() != null) {
            if (!emailValidator.isValid(newUserRequest.getEmail())) {
                throw new RuntimeException("Неверно указан email");
            }

            if (userRepository.haveUser(newUserRequest.getEmail())) {
                throw new RuntimeException(String.format("Этот E-mail \"%s\" уже используется", newUserRequest.getEmail()));
            }
        }

        User updatedUser = UserMapper.updateUserFields(user, newUserRequest);
        updatedUser = userRepository.updateUser(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        if (userRepository.getUserById(id) == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        userRepository.deleteUserById(id);
    }
}
