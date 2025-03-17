package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("Ivan");
        userDto1.setEmail("ivan@example.com");

        userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("Maria");
        userDto2.setEmail("maria@example.com");
    }

    @Test
    void getUsers_shouldReturnUsersList() throws Exception {
        List<UserDto> users = Arrays.asList(userDto1, userDto2);
        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Ivan"))
                .andExpect(jsonPath("$[0].email").value("ivan@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Maria"))
                .andExpect(jsonPath("$[1].email").value("maria@example.com"));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDto1);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void createUser_shouldCreateAndReturnUser() throws Exception {
        UserDto userToCreate = new UserDto();
        userToCreate.setName("New user");
        userToCreate.setEmail("new@example.com");

        User mappedUser = new User();
        mappedUser.setName("New user");
        mappedUser.setEmail("new@example.com");

        UserDto createdUser = new UserDto();
        createdUser.setId(3L);
        createdUser.setName("New user");
        createdUser.setEmail("new@example.com");

        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            userMapperMock.when(() -> UserMapper.mapToUser(userToCreate)).thenReturn(mappedUser);

            when(userService.createUser(mappedUser)).thenReturn(createdUser);

            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .content(objectMapper.writeValueAsString(userToCreate))
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(3L))
                    .andExpect(jsonPath("$.name").value("New user"))
                    .andExpect(jsonPath("$.email").value("new@example.com"));

            verify(userService, times(1)).createUser(mappedUser);
        }
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() throws Exception {
        UserDto userToUpdate = new UserDto();
        userToUpdate.setName("Updated Ivan");
        userToUpdate.setEmail("updated-ivan@example.com");

        User mappedUser = new User();
        mappedUser.setName("Updated Ivan");
        mappedUser.setEmail("updated-ivan@example.com");

        UserDto updatedUser = new UserDto();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Ivan");
        updatedUser.setEmail("updated-ivan@example.com");

        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            userMapperMock.when(() -> UserMapper.mapToUser(userToUpdate)).thenReturn(mappedUser);

            when(userService.updateUser(mappedUser, 1L)).thenReturn(updatedUser);

            mockMvc.perform(
                            MockMvcRequestBuilders.patch("/users/1")
                                    .content(objectMapper.writeValueAsString(userToUpdate))
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Updated Ivan"))
                    .andExpect(jsonPath("$.email").value("updated-ivan@example.com"));

            verify(userService, times(1)).updateUser(mappedUser, 1L);
        }
    }

    @Test
    void deleteUserById_shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/users/1")
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(1L);
    }
}
