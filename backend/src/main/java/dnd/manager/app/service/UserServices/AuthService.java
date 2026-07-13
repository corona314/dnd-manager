package dnd.manager.app.service.UserServices;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dnd.manager.app.config.JwtService;
import dnd.manager.app.dto.AuthDto.AuthResponseDto;
import dnd.manager.app.dto.AuthDto.LoginRequestDto;
import dnd.manager.app.dto.AuthDto.RegisterRequestDto;
import dnd.manager.app.model.User;
import dnd.manager.app.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return new AuthResponseDto(jwtService.generateToken(user));
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new AuthResponseDto(jwtService.generateToken(user));
    }
}
