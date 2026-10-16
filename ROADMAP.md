# Estado Actual del Desarrollo

Resumen del progreso por fases. Para instalación, arquitectura y stack, ver el [README](README.md).

| Fase | Estado |
|------|--------|
| 1. Compendio | Completada |
| 2. Creador de personajes | Flujo principal hecho, en pulido |
| 3. Creador de homebrew | Planificada |
| 4. Foro homebrew | Planificada |
| 5. Gestor de iniciativas | Planificada |
| 6. Sistema colaborativo DM-Players | Visión a futuro |

---

## Fase 1: Compendio (Completada)

**Backend:**
- API REST con búsqueda y filtrado avanzado para objetos, hechizos, clases, especies y trasfondos
- Endpoints paginados para manejar grandes volúmenes de datos
- Filtros multi-criterio: rareza, sintonización, magia, tipo de armadura, rango de precio, peso, alcance, etc.
- Seeding de datos canónicos desde scripts Python (`seed_*.py`)
- Autenticación con JWT

**Endpoints:**
- `GET /api/items` y `GET /api/items/{armor|weapons|shields}`: objetos, con filtros completos
- `GET /api/classes`, `GET /api/species`, `GET /api/backgrounds`, `GET /api/feats`: clases, especies, trasfondos  y dotes (cada uno con `/{id}` para el detalle)
- `GET /api/spells` y `GET /api/spells/{id}`: hechizos
- `POST /api/auth/login` y `POST /api/auth/register`: autenticación

**Frontend:**
- Una pantalla por entidad: hechizos, objetos (con pestañas por categoría), clases, especies y trasfondos
- Páginas de detalle para clases, subclases, especies y trasfondos
- Filtros de tres estados (requerido, excluido, ignorado), búsqueda por nombre, ordenación y paginación en servidor
- Tarjetas expandibles, con varias abiertas a la vez para poder comparar
- Descripciones en Markdown con tablas, renderizadas con `marked`
- Carcasa de tarjeta, tarjeta de hechizo y panel de filtros compartidos entre pantallas

---

## Fase 2: Creador de personajes (Flujo principal hecho, en pulido)

**Objetivo:**
Un asistente multi-paso que guíe al usuario hasta tener un personaje D&D completo guardado en su cuenta.

**Pasos del asistente (en el orden actual):**
1. **Clase**: elegir clase y revisar sus competencias iniciales
2. **Especie**: elegir especie
3. **Trasfondo**: elegir trasfondo
4. **Características**: distribuir las puntuaciones (FUE, DES, CON, INT, SAB, CAR)
5. **Equipo**: elegir el equipo inicial
6. **Hechizos**: elegir trucos y hechizos preparados, si la clase los tiene
7. **Finalización**: revisión antes de dar por terminado el personaje

La **subclase** no es un paso de la creación: se elige durante la subida de nivel.

**Gestión de personajes (hecho):**
- Lista de personajes del usuario
- Vista de personaje
- Subida de nivel, incluida la elección de subclase

**Cómo funciona:**
- El personaje se crea en el servidor y cada paso trabaja sobre su identificador
- La selección de hechizos se guarda al marcar o desmarcar, con rollback si el servidor falla
- Los límites de trucos y de hechizos preparados salen de los recursos del personaje, y la pestaña de trucos se desactiva si la clase no los tiene

**Componentes (`vueComponents/character/`):**
- `creation/`: `AppCharacterClass`, `AppCharacterSpecies`, `AppCharacterBackground`, `AppCharacterAbilities`, `AppCharacterEquipment`, `AppCharacterSpells1`, `AppCharacterFinalize`, `AppCharacterSubclass`
- `management/`: `AppCharacterView`, `AppCharacterLevelUp`

**Pendiente de esta fase:**
- [ ] Revisar la validación en tiempo real de todos los pasos, más allá de los límites de hechizos
- [ ] Revisar el cálculo de modificadores y estadísticas derivadas
- [ ] Exportación del personaje a PDF y JSON

---

## Trabajo transversal (hecho)

- Tema claro y oscuro, con la preferencia guardada
- Diálogo de ajustes sobre la pantalla actual: tema, iconos o letras, consejos del blob y cierre de sesión
- Avatar por usuario (Blobatar), con mirada que sigue el cursor, expresiones y consejos contextuales por pantalla
- Componentes y composables compartidos para no repetir código entre pantallas

---

## Fase 3: Creador de Homebrew (Planificado)

**Objetivo:**
Permitir que los usuarios creen contenido personalizado sin tocar la base de datos principal.

**Funcionalidades:**
- Editor visual para objetos, hechizos, clases y especies personalizadas
- Validación de reglas básicas (rareza frente a precio, equilibrio de estadísticas)
- Almacenamiento local en el navegador (IndexedDB), sin límite de creación
- Exportación a JSON para compartir con otros usuarios
- Importación de contenido homebrew de otros jugadores

**Arquitectura:**
- Contenido homebrew descentralizado por usuario, mediante IndexedDB
- Exportación e importación con JSON, con un UUID por entidad para evitar conflictos
- Versionado dentro del JSON para gestionar actualizaciones del contenido compartido

---

## Fase 4: Foro Homebrew (Planificado)

**Objetivo:**
Una comunidad abierta para compartir y descubrir contenido personalizado.

**Características:**
- Publicaciones con contenido homebrew (objetos, hechizos, clases)
- Valoraciones (me gusta, puntuaciones)
- Comentarios y discusión en cada publicación
- Búsqueda y filtrado por tipo de contenido, popularidad, etc.
- Moderación básica y reportes de contenido
- Descarga del homebrew directamente desde las publicaciones

**Tecnología:**
- Endpoints REST para publicaciones y comentarios
- Sistema de reputación de usuarios
- Caché del contenido popular con Redis (a muy largo plazo)

---

## Fase 5: Gestor de Iniciativas (Planificado)

**Objetivo:**
Una herramienta para gestionar combates durante las sesiones.

**Funcionalidades:**
- Seguimiento de turnos y del orden de iniciativa
- Control de puntos de golpe de enemigos y aliados
- Efectos, ventajas y desventajas con duración de 1 a N turnos
- Notas por turno (acciones realizadas)
- Exportación de un resumen del combate
- Reordenación de turnos arrastrando

**Ejemplo de uso:**
1. El DM añade enemigos (cantidad, tipo, estadísticas)
2. Los jugadores añaden sus personajes
3. Se tiran las iniciativas automáticamente
4. El sistema ordena los turnos
5. Se registran acciones, daño y efectos
6. Se exporta un resumen al terminar

---

## Fase 6: Sistema Colaborativo DM-Players (Futuro, visión)

**Objetivo:**
Convertir D&D Manager en una plataforma de campaña colaborativa en tiempo real.

**Arquitectura de campaña:**

```
Campaña
├── DM (Director de Juego)
│   ├── Crear y gestionar sesiones
│   ├── Controlar la progresión de los jugadores
│   ├── Repartir recompensas
│   └── Ver el estado del grupo en tiempo real
├── Jugadores
│   ├── Ver su personaje y su progresión
│   ├── Recibir avisos de subida de nivel
│   ├── Ver los objetos mágicos que les asigna el DM
│   └── Participar en iniciativas y combates
└── Datos del grupo
    ├── XP acumulada
    ├── Objetos mágicos repartidos
    ├── Misiones completadas
    └── Historia de la campaña
```

**Características principales:**

1. **Sesiones sincronizadas**
   - El DM crea la sesión y los jugadores se unen con un código
   - Sincronización en tiempo real de eventos (combate iniciado, subida de nivel, etc.)
   - WebSocket para comunicación bidireccional
   - Persistencia de las sesiones para continuar entre partidas

2. **Experiencia y progresión**
   - El DM otorga XP tras sesiones o combates
   - Subida de nivel automática, con el cálculo de las nuevas estadísticas
   - Aviso al jugador cuando sube de nivel
   - Historial de XP por sesión

3. **Reparto de recompensas**
   - El DM puede asignar objetos mágicos a un jugador
   - Los objetos aparecen en el inventario del personaje
   - Sistema de aceptación: el jugador ve el objeto ofrecido antes de aceptarlo
   - Historial de recompensas recibidas

4. **Colaboración en combate**
   - El DM inicia el combate y los jugadores ven el rastreador de iniciativa
   - Solo el DM puede modificar a los enemigos
   - Los jugadores ven los PG de sus aliados pero no los de los enemigos
   - Chat integrado durante el combate
   - Turnos sincronizados: todos ven a quién le toca

5. **Panel por rol**
   - **DM**: lista de jugadores, estado del grupo y herramientas de administración
   - **Jugador**: su personaje, XP acumulada, objetos asignados e historial de sesiones

6. **Herramientas de administración**
   - Repetir las tiradas de un jugador si hace falta
   - Ajuste manual de XP y nivel (correcciones)
   - Historial de cambios para auditoría
   - Exportación de los datos de la campaña

**Stack previsto (WebSockets):**
- `Spring WebSocket`: comunicación bidireccional nativa
- `STOMP`: protocolo de mensajería sobre WebSocket
- `SockJS`: alternativa para navegadores sin WebSocket

**Caso de uso:**

```
1. El DM crea la campaña "Culto del Colmillo Blanco"
2. Los jugadores se unen con un código (p. ej. "CdCB314")
3. Tras 3 sesiones, el DM otorga +450 XP al grupo
4. El sistema detecta que alguien alcanza el siguiente nivel
5. Aviso en tiempo real: "¡Has subido a nivel 4!"
6. El jugador ve sus nuevas habilidades
7. El DM encuentra una "Espada +1" en un cofre
8. Se la ofrece a un jugador, que la acepta
9. El objeto aparece en el inventario de su personaje
10. El DM exporta "Campaña - Resumen de la sesión 4" en JSON
```

---

## Deuda técnica

- [ ] Mover la URL de la API (hoy escrita en cada componente) a una variable de entorno de Vite
- [ ] Añadir `vue-router`: ahora las pantallas no tienen URL ni funciona el botón atrás del navegador
- [ ] Unificar el CSS
