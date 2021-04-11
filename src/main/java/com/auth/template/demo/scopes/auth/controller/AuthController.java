package com.auth.template.demo.scopes.auth.controller;


import com.auth.template.demo.scopes.auth.forms.SignUpDto;
import com.auth.template.demo.scopes.auth.payload.response.MessageResponse;
import com.auth.template.demo.scopes.auth.forms.LoginDto;
import com.auth.template.demo.scopes.auth.payload.response.JwtResponse;
import com.auth.template.demo.scopes.security.CustomAuthenticationProvider;
import com.auth.template.demo.scopes.security.StaticUtils;
import com.auth.template.demo.scopes.token.TokenServiceImpl;
import com.auth.template.demo.scopes.user.entities.User;
import com.auth.template.demo.scopes.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    CustomAuthenticationProvider authenticationProvider;

    @Autowired
    public UserService userService;

    @Autowired
    TokenServiceImpl tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid final LoginDto loginDto){

        Authentication authentication ;
        try {
            authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        }catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email not Found or Password invalid"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenService.createToken(user);

        List<String> roles = user.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //System.err.println(authentication.getPrincipal());

        return ResponseEntity.ok(new JwtResponse(jwt,
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                roles,
                user.getFirstname(),
                user.getLastname()
                ));
        }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser( @RequestBody SignUpDto signUpDto) {

        if (userService.doesEmailAlreadyExists(signUpDto.getEmail()) ||
        userService.doesNicknameAlreadyExists(signUpDto.getNickname())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Pseudo or Email is already taken!"));
        }

        userService.addUser(signUpDto);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/info")
    public ResponseEntity<?> userInformations(@RequestHeader (name="Authorization") String token) {

        try{
            Optional<User> user = userService.findUserByToken(token);
            if(user.isEmpty()){
                return ResponseEntity.ok(new MessageResponse("There are no users with this Token"));
            }
            return ResponseEntity.ok(user.get());

        }catch (Exception ignored){
            return ResponseEntity.ok(new MessageResponse("An Error occured"));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> handleLogout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                StaticUtils.logoutAndInvalidateSession();
            } catch (ServletException e) {
                logger.error("Error at log out of '" + authentication.getName() + "'.", e);
                return ResponseEntity.ok(new MessageResponse("Error during logout"));
            }
        }
        return ResponseEntity.ok(new MessageResponse("Logout successfully"));
    }
}
