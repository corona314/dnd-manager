package dnd.manager.app.service.UserServices;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dnd.manager.app.dto.AuthDto.AuthResponseDto;
import dnd.manager.app.dto.AuthDto.LoginRequestDto;
import dnd.manager.app.dto.AuthDto.RegisterRequestDto;
import dnd.manager.app.model.User;
import dnd.manager.app.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())){
            throw new RuntimeException("El usuario ya existe");
        }
        User user = new User();

        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new AuthResponseDto("OK" + user.getId());
    }

    public AuthResponseDto login(LoginRequestDto request) {
 
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new RuntimeException("El usuario no existe"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return new AuthResponseDto("OK_" + user.getId());
    }

}
