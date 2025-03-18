package io.github.brenno_araujo25.personal_finances.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.brenno_araujo25.personal_finances.dto.UserDTO;
import io.github.brenno_araujo25.personal_finances.entity.User;
import io.github.brenno_araujo25.personal_finances.mapper.UserMapper;
import io.github.brenno_araujo25.personal_finances.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDTO getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);

        return user
            .map(userMapper::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    public UserDTO createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toDTO(userRepository.save(user));
    }

}
