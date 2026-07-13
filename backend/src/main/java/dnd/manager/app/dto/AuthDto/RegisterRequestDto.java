package dnd.manager.app.dto.AuthDto;

public record RegisterRequestDto (
    String username,
    String email,
    String password
){}
