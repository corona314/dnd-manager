# D&D Manager

Plataforma de gestión para Dungeons & Dragons: un compendio consultable con filtros, un asistente de creación de personajes y, más adelante, un ecosistema de contenido personalizado (homebrew) y herramientas colaborativas para DMs y jugadores. Toda la información de reglas procede del **SRD 5.2** de D&D (ver [Licencia y atribución](#licencia-y-atribución)).

---

## Estado del proyecto

| Módulo | Estado |
|--------|--------|
| Registro e inicio de sesión (JWT) | Hecho |
| Compendio: hechizos, objetos, clases y subclases, especies, trasfondos | Hecho |
| Asistente de creación de personajes (clase, especie, trasfondo, características, equipo, hechizos, finalización) | En proceso |
| Vista de personaje y subida de nivel | Hecho (En proceso de front) |
| Ajustes de usuario, tema claro/oscuro y avatar | En proceso |
| Exportación de personajes a PDF | Pendiente |
| Gestor de homebrew (IndexedDB, exportar/importar JSON) | Pendiente |
| Foro de comunidad | Pendiente |
| Gestor de iniciativas | Pendiente |
| Sistema colaborativo DM-Players (WebSockets) | Pendiente |

---

## Funcionalidades

### Compendio
- Páginas de **hechizos**, **objetos** (armaduras, armas, escudos y otros), **clases**, **especies** y **trasfondos**, con páginas de detalle para clases, subclases, especies y trasfondos.
- **Filtros de tres estados** (requerido, excluido, ignorado): clic para avanzar, clic derecho para retroceder. Además, búsqueda por nombre, rango de nivel o precio, escuela, rareza, ordenación y paginación en servidor.
- Tarjetas expandibles: se pueden abrir **varias a la vez** para comparar, y el detalle ya cargado se conserva.
- Descripciones en Markdown (con tablas) renderizadas con `marked`.

### Personajes
- Creación guiada paso a paso: **clase → especie → trasfondo → características → equipo → hechizos → finalización**.
- Selección de hechizos con límites por truco y por hechizo preparado, guardada en el servidor al marcar o desmarcar.
- Vista de personaje y flujo de subida de nivel.

### Experiencia de usuario
- **Tema claro/oscuro** (respeta la preferencia del sistema la primera vez).
- **Ajustes** en un diálogo sobre la pantalla actual: modo oscuro, iconos o letras, consejos del blob, cierre de sesión y demás configuraciones.
- **Avatar con personalidad**: cada usuario tiene un blob generado a partir de su nombre ([Blobatar](https://www.npmjs.com/package/blobatar)) que sigue el cursor, cambia de expresión y puede dar consejos contextuales, desde un bocadillo en la cabecera o saliendo "al escenario" en pantalla. Se puede silenciar en Ajustes.

---

## Arquitectura técnica

### Backend (Spring Boot 4.0.6 + Java 21)

**Patrones utilizados:**
- **MVC por capas**: `controller` → `service` → `repository` → `model`
- **DTOs**: separación entre las entidades y lo que expone la API, con Lombok para reducir código repetitivo
- **Repository Pattern**: acceso a datos sobre JPA
- **REST API**: endpoints con validación de entrada y respuestas paginadas

**Herramientas principales:**
- Spring Boot Starter Data JPA (Hibernate)
- Spring Boot Starter WebMVC
- Spring Security con JWT (jjwt 0.11.5)
- MySQL Connector
- Lombok y Spring Boot DevTools

**Estructura:**
```
backend/
├── controller/      # Endpoints REST
├── service/         # Lógica de negocio
├── repository/      # Interfaces JPA
├── model/           # Entidades JPA
├── dto/             # Objetos de transferencia de datos
├── mapper/          # Conversión entre DTOs y modelos
└── config/          # Seguridad, CORS, etc.
```

### Frontend (Vue 3 + Vite)

**Filosofía:**
- **Composition API** con `<script setup>` en JavaScript.
- **Componentes compartidos** para lo que se repite entre pantallas: la carcasa de las tarjetas, las tarjetas y los filtros de hechizos.
- **Lógica reutilizable en composables**, no copiada entre archivos.
- **Sin router**: la navegación se gestiona con estado en `App.vue` (ver [Navegación](#navegación)).

**Estructura** (el alias `@` apunta a `src/components`, no a `src`):
```
frontend/wizard/
├── index.html
├── vite.config            # alias "@" → src/components
└── src/
    ├── main.js            # PrimeVue (tema Aura) y directivas globales
    ├── App.vue            # navegación, cabecera, tema y ajustes
    ├── directives/
    │   ├── noDoubleSelect.js   # evita seleccionar texto con doble clic
    │   └── blobSpot.js         # marca dónde puede ponerse el blob en pantalla
    └── components/        # ← "@"
        ├── composables/   # lógica reutilizable (ver tabla)
        ├── icons/         # iconos SVG como componentes
        ├── styles/        # CSS por pantalla + appCompendium.css (carcasa común)
        │   └── themes/colors.css   # variables de color, tema claro y oscuro
        └── vueComponents/
            ├── main/          # AppLogin, AppMain, AppSettings, AppAvatar, Blob*
            ├── shared/        # CompendiumCard, SpellCard, SpellFilters
            ├── compendiums/   # AppSpells, AppItems, AppClasses, AppSpecies, AppBackgrounds,
            │                  # páginas de detalle (…Expansion) y AppCharacters
            └── character/
                ├── creation/    # pasos del asistente de creación
                └── management/  # vista de personaje y subida de nivel
```

**Composables:**

| Archivo | Para qué sirve |
|---------|----------------|
| `useCardExpansion.js` | Abrir y cerrar tarjetas, con carga del detalle y caché; admite varias abiertas a la vez |
| `spellFilters.js` | Constantes y estado inicial de los filtros de hechizos |
| `renderDescription.js` | Markdown a HTML, arreglando tablas mal formadas |
| `usePreferences.js` | Preferencias del usuario (iconos o letras, consejos del blob) guardadas en `localStorage` |
| `useBlobSay.js` | API para que el blob diga algo: `say`, `sayOnce`, `hush` |
| `useBlobChatter.js` | Consejos de primera visita y frases ocasionales por pantalla |
| `useBlobSpots.js` | Registro de los sitios donde puede ponerse el blob |

### Navegación

No hay `vue-router`. `App.vue` guarda la pantalla actual en `currentPage` y los identificadores seleccionados (clase, especie, personaje…). Los componentes emiten `navigate` con el nombre de la página o con un objeto como `{ page: 'classExtended', classId: 3 }`, y `backMap` define a dónde vuelve el botón "Back". Una consecuencia es que las pantallas no tienen URL propia.

### Datos guardados en el navegador

| Clave de `localStorage` | Contenido |
|-------------------------|-----------|
| `dnd_token`, `dnd_username` | Sesión |
| `dnd_theme` | Tema claro u oscuro |
| `dnd_use_icons` | Iconos o letras en las tarjetas y filtros |
| `dnd_blob_talks` | Si el blob da consejos |
| `dnd_blob_seen` | Consejos de primera visita ya mostrados |

### Base de datos

- **MySQL** (principal): datos canónicos (objetos, hechizos, clases, especies, usuarios, personajes).
- **IndexedDB** (planificado): contenido homebrew en el navegador, sin límite de creación y sin depender del servidor.
- **Docker Compose** para levantar MySQL en desarrollo.
- Seeding de los datos canónicos mediante scripts Python.

---

## Stack

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje backend | Java | 21 |
| Framework backend | Spring Boot | 4.0.6 |
| ORM | JPA + Hibernate | (incluido en Spring Boot) |
| Seguridad | Spring Security + JWT (jjwt) | Spring Security según Spring Boot; jjwt 0.11.5 |
| Base de datos | MySQL | 8.0+ |
| Lenguaje frontend | JavaScript (ES modules) | — |
| Framework frontend | Vue 3 | 3.5.32 |
| Bundler | Vite | 8.0.8 |
| Componentes UI | PrimeVue (tema Aura) | 4.5.5 |
| Markdown | Marked | 18.0.5 |
| Avatares | Blobatar (`blobatar`, `@blobatar/vue`) | 2.7.0 |
| Contenedores | Docker y Docker Compose | — |

---

## Instalación y ejecución

### Requisitos previos
```
Java 21+
Node.js 20.19.0 o 22.12.0+
npm
Docker y Docker Compose
```

### Pasos

**1. Clonar el repositorio**
```bash
git clone https://github.com/corona314/dnd-manager.git
cd dnd-manager
```

**2. Iniciar la base de datos**
```bash
docker-compose up -d
```
Levanta MySQL en `localhost:3306` con credenciales de desarrollo.

**3. Ejecutar el backend**
```bash
cd backend
./mvnw clean package
./mvnw spring-boot:run
```
Disponible en `http://localhost:8080`.

**4. Ejecutar el frontend**
```bash
cd frontend/wizard
npm install
npm run dev
```
Disponible en `http://localhost:5173`. El frontend llama a la API en `http://localhost:8080/api`.

### Build de producción

**Backend:**
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/app-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd frontend/wizard
npm run build
# genera dist/, listo para servir con nginx
```

---

## API

Resumen de los endpoints que usa el frontend. Todos, salvo los de `/auth`, requieren la cabecera `Authorization: Bearer <token>`. Los listados devuelven páginas de Spring (`content`, `totalPages`, `number`).

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/register`, `/auth/login` | Alta e inicio de sesión; devuelven un JWT |
| GET | `/spells`, `/spells/{id}` | Listado filtrado y detalle |
| GET | `/items`, `/items/armor`, `/items/weapons`, `/items/shields` (+ `/{id}`) | Objetos por categoría |
| GET | `/classes`, `/species`, `/backgrounds` (+ `/{id}`) | Compendio |
| GET | `/characters/{id}` | Personaje con clases, recursos, espacios de conjuro y hechizos |
| POST, DELETE | `/characters/{id}/spells/{spellId}` | Añadir o quitar un hechizo del personaje |
| ... | `...` | ... |

**Parámetros de `/spells`:** `page`, `size`, `sort`, `name`, `levelMin`, `levelMax`, `schoolId` (repetible), `components` (`V`, `S`, `M`; con `!` para excluir, como `!M`), `ritual`, `concentration` (`1` o `0`).

**Parámetros de los objetos:** `name`, `sort`, `magic`, `attunement` (`1` o `0`), `priceMin` y `priceMax` (en piezas de cobre), `rarity` (repetible) y filtros propios de cada categoría (`armorType`, `weaponType`, `category`, `itemType`).

...

---

## Hoja de ruta

1. Pulir y terminar el asistente de creación de personajes
2. Exportar personajes a PDF
3. Gestor de homebrew con IndexedDB
4. Foro básico de comunidad
5. Gestor de iniciativas
6. Sistema colaborativo DM-Players con WebSockets

**Mejoras técnicas previstas:**
- Mover la URL de la API (hoy escrita en cada componente) a una variable de entorno de Vite
- Añadir `vue-router` para que las pantallas tengan URL y funcione el botón atrás del navegador
- Unificar el CSS de filtros que comparten hechizos y objetos

---

## Licencia y atribución

Las reglas, hechizos, objetos y demás contenido de juego proceden del System Reference Document 5.2:

> This work includes material from the System Reference Document 5.2 ("SRD 5.2") by Wizards of the Coast LLC, available at https://www.dndbeyond.com/srd. The SRD 5.2 is licensed under the Creative Commons Attribution 4.0 International License, available at https://creativecommons.org/licenses/by/4.0/legalcode.

El código de este repositorio: *licencia por definir*.

---

## Autores

Proyecto de portfolio desarrollado entre dos personas:

- Backend y mejoras puntuales de interfaz: Imanol Eguidón ([corona314](https://github.com/corona314))
- Frontend: Irune Cereijo ([Iruneca](https://github.com/Iruneca))