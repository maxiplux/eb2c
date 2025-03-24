package app.quantun.eb2c.service;


import app.quantun.eb2c.model.contract.request.UserRequestDTO;
import app.quantun.eb2c.model.contract.response.RoleResponseDTO;
import app.quantun.eb2c.model.contract.response.UserResponseDTO;
import app.quantun.eb2c.model.entity.core.Role;
import app.quantun.eb2c.model.entity.core.User;
import app.quantun.eb2c.repository.RoleRepository;
import app.quantun.eb2c.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = User.builder()
                .username(requestDTO.getUsername())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .email(requestDTO.getEmail())
                .build();

        // Add roles if provided
        if (requestDTO.getRoleIds() != null && !requestDTO.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : requestDTO.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return mapToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Check if username already exists and it's not the current user
        if (!user.getUsername().equals(requestDTO.getUsername())
                && userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        user.setUsername(requestDTO.getUsername());
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }
        user.setEmail(requestDTO.getEmail());

        // Update roles if provided
        if (requestDTO.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : requestDTO.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        Set<RoleResponseDTO> rolesDTO = user.getRoles().stream()
                .map(role -> RoleResponseDTO.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toSet());

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(rolesDTO)
                .build();
    }
}