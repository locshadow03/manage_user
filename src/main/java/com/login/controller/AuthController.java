package com.login.controller;

import com.login.dto.UserDto;
import com.login.model.Role;
import com.login.model.User;
import com.login.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;


    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/home-login")
    public String homelogin() {
        return "homelogin";
    }

    @RequestMapping("/default")
    public String defaultAfterLogin(@RequestParam(name = "cancel", required = false)Boolean cancel, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();
        model.addAttribute("loggedInUser", loggedInUser);
        if (Boolean.TRUE.equals(cancel)) {
            model.addAttribute("users", userService.getAllUsers());
            return "dashboard";
        }
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            model.addAttribute("users", userService.getAllUsers());
            return "dashboard";
        }
        return "home";
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/default/edituser/{id}")
    public String update(@PathVariable Long id, Model model) {
        Optional<User> user1 = userService.getUserById(id);
        User user = user1.get();
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setCity(user.getCity());
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        userDto.setRole(roles);
        model.addAttribute("userDto",userDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();
        model.addAttribute("loggedInUser", loggedInUser);
        return "edituser";
    }

    @PostMapping("/default/edit/{id}")
    public String saveEdit(@PathVariable("id") Long id,@Valid @ModelAttribute("userDto") UserDto userDto, Model model,BindingResult result ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();
        model.addAttribute("loggedInUser", loggedInUser);
        try {
            if (result.hasErrors()) {
                model.addAttribute("userDto", userDto);
                result.toString();
                return "edituser";
            }
             else{
                userDto.setId(id);
                userService.edit(userDto);
                System.out.println("success");
                model.addAttribute("success", "Sửa tài khoản thành công");
                model.addAttribute("userDto", userDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "Máy chủ đã bị lỗi!");
        }
        model.addAttribute("users", userService.getAllUsers());
        return "edituser";

    }

    @GetMapping("/default/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();
        model.addAttribute("loggedInUser", loggedInUser);
        userService.delete(id);
        model.addAttribute("users", userService.getAllUsers());
        return "dashboard";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @GetMapping("/default/add-user")
    public String addUser(Model model) {
        model.addAttribute("userDto", new UserDto());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();
        model.addAttribute("loggedInUser", loggedInUser);
        return "adduser";
    }

    @GetMapping("/default/chart")
    public String showchart(Model model) {
        long adminCount = userService.countAdmins();
        long userCount = userService.countUsers();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("userCount", userCount);
        return "chartjs";
    }

    @PostMapping("/default/add-user-new")
    public String addUserNew(@Valid @ModelAttribute("userDto") UserDto userDto,
                              BindingResult result,
                              Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();
        model.addAttribute("loggedInUser", loggedInUser);

        try {

            if (result.hasErrors()) {
                model.addAttribute("userDto", userDto);
                result.toString();
                return "adduser";
            }
            String username = userDto.getUsername();
            User user = userService.findByUsername(username);
            if (user != null) {
                model.addAttribute("usreDto", userDto);
                System.out.println("User not null");
                model.addAttribute("emailError", "Email của bạn đã được đăng ký!");
                return "adduser";
            }
            if (userDto.getPassword().equals(userDto.getRepeatPassword())) {
                userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
                userService.saveAddUser(userDto);
                System.out.println("success");
                model.addAttribute("success", "Thêm tài khoản thành công");
                model.addAttribute("userDto", userDto);
            } else {
                model.addAttribute("userDto", userDto);
                model.addAttribute("passwordError", "Có thể mật khẩu của bạn sai! Kiểm tra lại!");
                System.out.println("password not same");
                return "adduser";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "Máy chủ đã bị lỗi!");
        }
        model.addAttribute("users", userService.getAllUsers());
        return "adduser";

    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("userDto") UserDto userDto,
                              BindingResult result,
                              Model model) {

        try {

            if (result.hasErrors()) {
                model.addAttribute("userDto", userDto);
                result.toString();
                return "register";
            }
            String username = userDto.getUsername();
            User user = userService.findByUsername(username);
            if (user != null) {
                model.addAttribute("usreDto", userDto);
                System.out.println("User not null");
                model.addAttribute("emailError", "Email của bạn đã được đăng ký!");
                return "register";
            }
            if (userDto.getPassword().equals(userDto.getRepeatPassword())) {
                userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
                userService.save(userDto);
                System.out.println("success");
                model.addAttribute("success", "Đăng ký thành công");
                model.addAttribute("userDto", userDto);
            } else {
                model.addAttribute("userDto", userDto);
                model.addAttribute("passwordError", "Có thể mật khẩu của bạn sai! Kiểm tra lại!");
                System.out.println("password not same");
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "Máy chủ đã bị lỗi!");
        }
        return "register";

    }
}
