package com.login.dto;

import com.login.model.Role;
import com.login.model.User;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @Size(min = 3, max = 10, message = "First name contains 3-10 characters")
    private String firstName;
    @Size(min = 3, max = 10, message = "Last name contains 3-10 characters")
    private String lastName;
    private String username;
    @Size(min = 3, max = 10, message = "City contains 3-10 characters")
    private String city;
    @Size(min = 5, max = 10, message = "Password contains 5-10 characters")
    private String password;
    private String repeatPassword;
    private List<String> role;
}
