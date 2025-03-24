package app.quantun.eb2c.service;


import app.quantun.eb2c.model.contract.request.UserRequestDTO;
import app.quantun.eb2c.model.contract.response.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByUsername(String username);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO);

    void deleteUser(Long id);

    boolean existsByUsername(String username);
}