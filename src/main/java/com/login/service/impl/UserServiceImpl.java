package com.login.service.impl;

import com.login.dto.UserDto;
import com.login.model.Role;
import com.login.model.User;
import com.login.repository.RoleRepository;
import com.login.repository.UserRepository;
import com.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  RoleRepository roleRepository;

    @Override
    public User save(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setCity(userDto.getCity());
        user.setPassword(userDto.getPassword());
        user.setRoles(Arrays.asList(roleRepository.findByName("USER")));
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Xóa liên kết giữa người dùng và các vai trò trong bảng user_roles
        user.getRoles().clear(); // Xóa danh sách vai trò của người dùng

        // Lưu lại người dùng sau khi cập nhật
        userRepository.save(user);

        // Xóa người dùng
        userRepository.deleteById(userId);
    }

    @Override
    public User edit(UserDto userDto) {
        User user = userRepository.findUserById(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setCity(userDto.getCity());
        List<Role> roles = userDto.getRole().stream()
                .map(roleName -> roleRepository.findByName(roleName))
                .collect(Collectors.toList());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public User saveAddUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setCity(userDto.getCity());
        user.setPassword(userDto.getPassword());
        List<String> roleNames = userDto.getRole();
        List<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName))
                .collect(Collectors.toList());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public long countAdmins() {
        String userRole = "ADMIN";
        return userRepository.countByRole(userRole);
    }

    public long countUsers() {
        String userRole = "USER";
        return userRepository.countByRole(userRole);
    }
}
