package io.github.brenno_araujo25.personal_finances.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.github.brenno_araujo25.personal_finances.dto.UserDTO;
import io.github.brenno_araujo25.personal_finances.entity.User;
import io.github.brenno_araujo25.personal_finances.mapper.UserMapper;
import io.github.brenno_araujo25.personal_finances.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UserDTO mockUserDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername("brenno");
        mockUser.setEmail("brenno@gmail.com");
        mockUser.setPassword("123456");
        
        mockUserDTO = new UserDTO();
        mockUserDTO.setUsername(mockUser.getUsername());
        mockUserDTO.setEmail(mockUser.getEmail());
        mockUserDTO.setPassword(mockUser.getPassword());
        
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDTO);
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        
        UserDTO userDTO = userService.getUserById(mockUser.getId());
        
        assertEquals(mockUserDTO, userDTO);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(id);
        });
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.existsByUsername(mockUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(false);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        
        UserDTO userDTO = userService.createUser(mockUser);
        
        assertEquals(mockUserDTO, userDTO);
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(mockUser.getUsername())).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(mockUser);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(mockUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(mockUser);
        });
    }
}
