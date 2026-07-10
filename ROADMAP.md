# Estado Actual del Desarrollo

### Fase 1: Compendio (Completado)

**Qué se hizo:**
- API REST completa con búsqueda y filtrado avanzado para items, hechizos, clases, razas y trasfondos
- Endpoints paginados para manejo eficiente de grandes volúmenes de datos
- Filtros multi-criterio: rarity, attunement, magic, armor type, price range, weight, range, etc.
- Seeding de datos canónicos desde scripts Python (seed_*.py)
- Autenticación básica durante desarrollo con JWT

**Endpoints implementados:**
- `GET /api/items` - Items con filtros completos
- `GET /api/items/armor` - Armaduras específicamente
- `GET /api/items/weapons` - Armas
- `GET /api/classes` - Clases de personajes
- `GET /api/species` - Razas (species)
- `GET /api/spells` - Hechizos
- `GET /api/backgrounds` - Trasfondos de personaje
- `POST /api/auth/login` y `POST /api/auth/register` - Autenticación

**Frontend del compendio:**
- Interfaz de búsqueda con filtros dinámicos
- Vistas detalladas de cada entidad
- Renderizado de Markdown para descripciones de hechizos
- Responsive design con PrimeVue DataTable

### Fase 2: Creador de Personajes (En Desarrollo)

**Objetivo:**
Implementar un wizard multi-step que guíe al usuario a través de la creación de un personaje D&D completo.

**Pasos del wizard:**
1. **Selección de raza** - Elegir species y revisar bonificadores de atributos
2. **Selección de clase** - Escoger clase y revisar habilidades iniciales
3. **Selección de subclase** - Elección especializada dentro de la clase
4. **Distribución de atributos** - Asignar puntos a STR, DEX, CON, INT, WIS, CHA
5. **Selección de hechizos** - Si la clase lo permite, elegir spell list
6. **Selección de equipo** - Escoger armadura, arma y items iniciales
7. **Revisión final** - Preview del personaje antes de guardar

**Características técnicas:**
- Validación en tiempo real de restricciones (ej: hechizos solo para clases mágicas)
- Estado persistente en frontend durante el wizard
- Cálculo automático de modificadores y stats derivados
- Guardado del personaje en BD asociado al usuario autenticado
- Exportación del personaje en múltiples formatos (PDF, JSON)

**Componentes Vue a implementar:**
- `AppClasses.vue` - Selección de clase
- `AppClassExpansion.vue` - Detalles de clase expandible
- `AppSpells.vue` - Selector de hechizos con filtrado
- Componente de distribución de atributos
- Preview y confirmación final

### Fase 3: Creador de Homebrew (Planificado)

**Objetivo:**
Permitir que usuarios creen contenido personalizado sin tocar la BD principal.

**Funcionalidades:** (Puede que un SQLite local no sea la solución, se verá durante desarrollo)
- Editor visual para items, hechizos, clases y razas personalizadas
- Validación de reglas básicas (ej: rareza vs precio, balanceo de stats)
- Almacenamiento local en BD local a nivel usuario para no limitar la creación de Hombrew
- Exportación a JSON para compartir con otros usuarios
- Importación de contenido homebrew de otros jugadores

**Arquitectura:**
- BD local descentralizada por usuario (Quizás SQLite con sync)
- Versionado de cambios para auditoría
- Sistema de plantillas para acelerar creación de nuevos items

### Fase 4: Foro Homebrew (Planificado)

**Objetivo:**
Comunidad abierta para compartir y descubrir contenido personalizado.

**Características:**
- Posts de usuario con contenido homebrew (items, hechizos, clases)
- Sistema de valoraciones (likes, ratings)
- Comentarios y discusión en cada post
- Búsqueda y filtrado por tipo de contenido, popularidad...
- Moderación básica y reportes de contenido
- Descarga de homebrew directamente desde posts

**Tecnología:**
- Endpoint REST para CRUD de posts y comentarios
- Sistema de reputación de usuarios
- Caching de contenido popular con Redis (muy a futuro)

### Fase 5: Gestor de Iniciativas (Planificado)

**Objetivo:**
Herramienta para gestionar combates en tiempo real durante sesiones.

**Funcionalidades:**
- Rastreo de turno y orden de iniciativa
- Tracker de HP (hit points) de enemigos y aliados
- Gestión de effects y buffs/debuffs (duración de 1-N turnos)
- Notas por turno (acciones realizadas)
- Exportación de resumen de combate
- Interfaz intuitiva con drag-drop para reordenar turnos

**Ejemplo de uso:**
1. DM añade enemigos (cantidad, tipo, stats)
2. Jugadores añaden sus personajes
3. Se lanzan iniciativas automáticamente
4. Sistema ordena el turno (DEX-based)
5. Se rastrean acciones, daño, effects
6. Se exporta resumen al finalizar

### Fase 6: Sistema Colaborativo DM-Players (Futuro - Visión)

**Objetivo:**
Convertir D&D Manager en plataforma de campaña colaborativa en tiempo real.

**Arquitectura de campaña:**

```
Campaign (Campaña)
├── DM (Director de Juego)
│   ├── Crear/gestionar sesiones
│   ├── Controlar progresión de jugadores
│   ├── Distribuir recompensas
│   └── Ver estado de la party en tiempo real
├── Players (Jugadores)
│   ├── Ver su personaje y progresión
│   ├── Recibir notificaciones de level-up
│   ├── Ver items mágicos asignados por DM
│   └── Participar en iniciativas/combates
└── Party Data (Datos compartidos)
    ├── XP acumulada
    ├── Items mágicos distribuidos
    ├── Misiones completadas
    └── Historia de la campaña
```

**Características principales:**

1. **Gestión de sesiones sincronizadas:**
   - DM crea sesión y players se unen con código
   - Sincronización en tiempo real de eventos (combate iniciado, level-up, etc.)
   - WebSocket para comunicación bidireccional instantánea
   - Persistencia de sesiones para continuidad entre reuniones

2. **Sistema de experiencia y progresión:**
   - DM otorga XP después de sesiones/combates
   - Sistema automático de nivel-up con cálculo de nuevos stats
   - Notificaciones al jugador de ganancia de nivel
   - Historial de ganancia de XP por sesión

3. **Distribución de recompensas:**
   - DM puede asignar items mágicos al jugador
   - Items aparecen en inventario del personaje del jugador
   - Sistema de aprobación (el jugador ve el item ofrecido antes de aceptar)
   - Historial de recompensas recibidas

4. **Colaboración en combate:**
   - DM inicia combate y players ven el rastreador de iniciativas
   - Solo el DM puede modificar enemigos
   - Jugadores pueden ver HP de aliados pero no de enemigos
   - Chat integrado para comunicación durante combate
   - Sincronización de turnos: todos ven quién está en turno

5. **Dashboard por rol:**
   - **DM Dashboard**: Lista de jugadores, estado de party, herramientas de administración
   - **Player Dashboard**: Mi personaje, XP acumulada, items asignados, historial de sesiones

6. **Herramientas de administración:**
   - Reroll de stats de jugador si es necesario
   - Ajuste manual de XP/nivel (correcciones)
   - Historial de cambios para auditoría
   - Exportación de datos de campaña

**Idea de stack para esta etapa de desarrollo (WebSockets):**
- `Spring WebSocket`: Soporte nativo para comunicación bidireccional
- `STOMP Protocol`: Protocolo sobre WebSocket para mensajería
- `SockJS`: Fallback para navegadores sin WebSocket

**Caso de uso:**

```
1. DM crea campaña "Culto del Colmillo Blanco"
2. Players se unen con código (e.g., "CdCB314")
3. Después de 3 sesiones, DM otorga +450 XP al grupo
4. Sistema automático detecta que alguien llega a siguiente nivel
5. Notificación en tiempo real: "¡Has subido a Nivel 4!"
6. Player ve nuevas habilidades disponibles
7. DM encuentra "Espada +1" en cofre
8. Ofrece item a player → Player lo acepta
9. Item aparece en inventario del personaje
10. DM puede exportar "Campaña - Session 4 Summary" en JSON
```