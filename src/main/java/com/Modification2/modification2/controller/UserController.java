package com.Modification2.modification2.controller;

import com.Modification2.modification2.service.UserRegistrationService;
import com.Modification2.modification2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRegistrationService userRegistrationService;
    private final UserService userService;

    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String register(Authentication authentication)
    {
        Jwt jwt=(Jwt) authentication.getPrincipal();
       return userRegistrationService.registerUser(jwt.getTokenValue());

    }

    @PostMapping("subscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean subscribeUser(@PathVariable String userId)
    {
       userService.subscribeUser(userId);
       return true;
    }

    @PostMapping("unsubscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean unsubscribeUser(@PathVariable String userId)
    {
        userService.unSubscribeUser(userId);
        return true;
    }

    @GetMapping("/{userId}/history")
    @ResponseStatus(HttpStatus.OK)
    public Set<String>userHistory(@PathVariable String userId)
    {
      return userService.userHistory(userId);
    }

}
