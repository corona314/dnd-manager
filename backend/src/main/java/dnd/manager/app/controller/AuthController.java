package dnd.manager.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.AuthDto.AuthResponseDto;
import dnd.manager.app.dto.AuthDto.LoginRequestDto;
import dnd.manager.app.dto.AuthDto.RegisterRequestDto;
import dnd.manager.app.service.UserServices.AuthService;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }
}

