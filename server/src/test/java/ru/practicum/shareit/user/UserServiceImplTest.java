package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(UserServiceImpl.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepositoryMock;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Ivan");
        testUser.setEmail("ivan@example.com");
    }

    @Test
    void getUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Ivan");
        user1.setEmail("ivan@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Maria");
        user2.setEmail("maria@example.com");

        List<User> users = Arrays.asList(user1, user2);

        when(userRepositoryMock.findAll()).thenReturn(users);

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ivan", result.get(0).getName());
        assertEquals("Maria", result.get(1).getName());
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    void getUsers_WhenEmptyList_ShouldReturnEmptyList() {
        when(userRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto userDto = userService.getUserById(1L);

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("Ivan", userDto.getName());
        assertEquals("ivan@example.com", userDto.getEmail());
        verify(userRepositoryMock, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowNotFoundException() {
        when(userRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepositoryMock, times(1)).findById(999L);
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        when(userRepositoryMock.findByEmail(anyString())).thenReturn(Collections.emptyList());
        when(userRepositoryMock.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepositoryMock, times(1)).findByEmail(testUser.getEmail());
        verify(userRepositoryMock, times(1)).save(testUser);
    }

    @Test
    void createUser_WithInvalidEmail_ShouldThrowValidationException() {
        User invalidUser = new User();
        invalidUser.setName("Тест");
        invalidUser.setEmail("invalid-email");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(invalidUser)
        );

        assertEquals("Неверно указан email", exception.getMessage());
        verify(userRepositoryMock, never()).save(any());
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowDuplicatedDataException() {
        when(userRepositoryMock.findByEmail(testUser.getEmail())).thenReturn(Collections.singletonList(testUser));

        DuplicatedDataException exception = assertThrows(
                DuplicatedDataException.class,
                () -> userService.createUser(testUser)
        );

        assertEquals(String.format("Этот E-mail \"%s\" уже используется", testUser.getEmail()), exception.getMessage());
        verify(userRepositoryMock, times(1)).findByEmail(testUser.getEmail());
        verify(userRepositoryMock, never()).save(any());
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        User updatedUser = new User();
        updatedUser.setName("New Ivan");
        updatedUser.setEmail("ivan_new@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("New Ivan");
        savedUser.setEmail("ivan_new@example.com");

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.findByEmail(anyString())).thenReturn(Collections.emptyList());
        when(userRepositoryMock.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.updateUser(updatedUser, 1L);

        assertNotNull(result);
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).findByEmail(updatedUser.getEmail());
        verify(userRepositoryMock, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NonExistingUser_ShouldThrowNotFoundException() {
        User updatedUser = new User();
        updatedUser.setName("Тест");

        when(userRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(updatedUser, 999L)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepositoryMock, times(1)).findById(999L);
        verify(userRepositoryMock, never()).save(any());
    }

    @Test
    void updateUser_OnlyName_ShouldUpdateOnlyName() {
        User updatedUser = new User();
        updatedUser.setName("New Ivan");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("New Ivan");
        savedUser.setEmail("ivan@example.com");

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.updateUser(updatedUser, 1L);

        assertNotNull(result);
        assertEquals("New Ivan", result.getName());
        assertEquals("ivan@example.com", result.getEmail());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, never()).findByEmail(anyString());
        verify(userRepositoryMock, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidEmail_ShouldThrowValidationException() {
        User updatedUser = new User();
        updatedUser.setEmail("invalid-email");

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(updatedUser, 1L)
        );

        assertEquals("Неверно указан email", exception.getMessage());
        verify(userRepositoryMock, never()).save(any());
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowDuplicatedDataException() {
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("existing@example.com");

        User updatedUser = new User();
        updatedUser.setEmail("existing@example.com");

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.findByEmail(updatedUser.getEmail())).thenReturn(Collections.singletonList(existingUser));

        DuplicatedDataException exception = assertThrows(
                DuplicatedDataException.class,
                () -> userService.updateUser(updatedUser, 1L)
        );

        assertEquals(String.format("Этот E-mail \"%s\" уже используется", updatedUser.getEmail()), exception.getMessage());
        verify(userRepositoryMock, times(1)).findByEmail(updatedUser.getEmail());
        verify(userRepositoryMock, never()).save(any());
    }

    @Test
    void deleteUserById_WithValidId_ShouldDeleteUser() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepositoryMock).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_WithInvalidId_ShouldThrowNotFoundException() {
        when(userRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUserById(999L)
        );

        assertEquals("Пользователь с ID 999 не найден", exception.getMessage());
        verify(userRepositoryMock, times(1)).findById(999L);
        verify(userRepositoryMock, never()).deleteById(anyLong());
    }
}
