package dnd.manager.app.dto.ClassDto;

/*
    Clase necesaria para poder ver la información de tus personajes
    Tarjeta rápida para consultar
*/

public record ClassResponseDto (
    Long id,
    String name,
    String hitPointDie
    
){}
