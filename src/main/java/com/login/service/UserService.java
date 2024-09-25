package com.login.service;

import com.login.dto.UserDto;
import com.login.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(UserDto userDto);

    User findByUsername(String username);

    Optional<User> getUserById(Long id);

    List<User> getAllUsers();

    void delete(Long id);

    User edit(UserDto userDto);

    User saveAddUser(UserDto userDto);

    long countAdmins();


    long countUsers();

}
