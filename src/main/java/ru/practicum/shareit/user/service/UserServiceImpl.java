package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
        return userRepository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.mapToUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    public UserDto createUser(User user) {

        if (!emailValidator.isValid(user.getEmail())) {
            throw new ValidationException("Неверно указан email");
        }

        if (!userRepository.findByEmail(user.getEmail()).isEmpty()) {
            throw new DuplicatedDataException(String.format("Этот E-mail \"%s\" уже используется", user.getEmail()));
        }

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(User newUserRequest, Long userId) {
        if (newUserRequest.getEmail() != null) {
            if (!emailValidator.isValid(newUserRequest.getEmail())) {
                throw new ValidationException("Неверно указан email");
            }

            if (!userRepository.findByEmail(newUserRequest.getEmail()).isEmpty()) {
                throw new DuplicatedDataException(String.format("Этот E-mail \"%s\" уже используется", newUserRequest.getEmail()));
            }
        }

        User updatedUser = UserMapper.updateUserFields(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден")),
                newUserRequest);
        updatedUser = userRepository.save(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
