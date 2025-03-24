package app.quantun.eb2c.service;

import app.quantun.eb2c.model.contract.request.UserRequestDTO;
import app.quantun.eb2c.model.contract.response.UserResponseDTO;
import app.quantun.eb2c.model.entity.core.Role;
import app.quantun.eb2c.model.entity.core.User;
import app.quantun.eb2c.repository.RoleRepository;
import app.quantun.eb2c.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role testRole;
    private UserRequestDTO testUserRequestDTO;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedpassword")
                .email("test@example.com")
                .roles(roles)
                .build();

        Set<Long> roleIds = new HashSet<>();
        roleIds.add(1L);

        testUserRequestDTO = UserRequestDTO.builder()
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .roleIds(roleIds)
                .build();
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDTO result = userService.createUser(testUserRequestDTO);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(testUserRequestDTO);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(anyString())).thenReturn("newencodedpassword");

        UserRequestDTO updateRequest = UserRequestDTO.builder()
                .username("updateduser")
                .password("newpassword")
                .email("updated@example.com")
                .roleIds(Collections.singleton(1L))
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .username("updateduser")
                .password("newencodedpassword")
                .email("updated@example.com")
                .roles(Collections.singleton(testRole))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDTO result = userService.updateUser(1L, updateRequest);

        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        verify(userRepository, never()).deleteById(anyLong());
    }
}
