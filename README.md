# D&D Manager

Una plataforma de gestión integral para Dungeons & Dragons que combinará un compendio exhaustivo, herramientas de creación de personajes y un ecosistema de contenido personalizado (homebrew). Diseñada con arquitectura escalable para soportar campañas colaborativas en tiempo real. Toda la información está sacada del SRD 5.2 de D&D.

## Descripción General

**D&D Manager** es un proyecto full-stack que busca consolidarse como una solución profesional de referencia para la comunidad D&D. El proyecto implementará patrones de arquitectura moderna y una experiencia de usuario intuitiva.

La plataforma estará estructurada en módulos independientes que funcionan de manera cohesiva:

1. **Compendio centralizado**: Base de datos exhaustiva y searchable de reglas, items, hechizos, clases y razas
2. **Asistente de creación de personajes**: Wizard interactivo multi-paso con validación en tiempo real
3. **Gestor de homebrew descentralizado**: Contenido personalizado gestionado localmente con capacidad de exportar/importar
4. **Foro de comunidad**: Plataforma abierta para compartir y descubrir creaciones homebrew
5. **Sistema colaborativo DM-Players**: Gestión de sesiones, sincronización de experiencia y control de progresión

---

## 🏗️ Arquitectura Técnica

### Backend (Spring Boot 4.0.6 + Java 21)

**Patrones utilizados:**
- **MVC + Capas**: Separación clara entre `controller` → `service` → `repository` → `model`
- **DTOs (Data Transfer Objects)**: Mapeo de entidades con Lombok para reducir boilerplate
- **Repository Pattern**: Acceso a datos abstrayendo la complejidad de JPA
- **REST API**: Endpoints RESTful con validación de entrada y manejo de errores consistente

**Herramientas principales:**
- `Spring Boot Starter Data JPA`: ORM con Hibernate para persistencia relacional
- `Spring Boot Starter WebMVC`: Construcción de API REST y manejo de HTTP
- `MySQL Connector`: Driver nativo para bases de datos MySQL
- `Lombok`: Generación automática de getters, setters y constructores
- `Spring Boot DevTools`: Hot reload para desarrollo ágil

**Estructura de carpetas backend:**
```
backend/
├── controller/      # Endpoints REST y mapping de requests
├── service/         # Lógica de negocio e integración de entidades
├── repository/      # Interfaces JPA para consultas a BD
├── model/           # Entidades JPA mapeadas a tablas
├── dto/             # Objetos de transferencia de datos (segregación por tipo)
├── mapper/          # Conversión entre DTOs y modelos
└── config/          # Configuración de Spring Security, CORS, etc.
```

### Frontend (Vue 3 + TypeScript + Vite)

**Filosofía de diseño:**
- **Componentes reutilizables**: SFCs (Single File Components) con scoped styling
- **Type Safety**: TypeScript strict para evitar errores en tiempo de compilación
- **Experiencia moderna**: Vite para desarrollo ultra-rápido con HMR (Hot Module Replacement)

**Stack tecnológico:**
- **Vue 3 (Composition API)**: Framework progresivo con reactividad granular
- **TypeScript 6.0**: Type checking completo en frontend
- **Vite**: Bundler de próxima generación (desarrollo <100ms, build optimizado)
- **PrimeVue 4.5**: Librería de componentes profesionales pre-estilizados (DataTable, Dialog, Forms, etc.)
- **Marked**: Parser de Markdown para renderizar documentación de hechizos/items

**Estructura de carpetas frontend:**
```
frontend/wizard/
├── src/
│   ├── components/      # Componentes Vue reutilizables
│   ├── api/             # Cliente HTTP y servicios de integración
│   ├── assets/          # Estilos CSS modularizados
│   └── App.vue          # Componente raíz
├── vite.config.ts       # Configuración de bundler y plugins
└── tsconfig.json        # Configuración TypeScript strict
```

### Base de Datos

**Gestión:**
- **MySQL (Principal)**: Almacena datos canónicos (items, hechizos, clases, razas, users)
- **BD Local (Homebrew)**: Bases de datos descentralizadas por usuario/grupo para contenido personalizado
- **Docker Compose**: Orquestación de contenedores para levantar MySQL en desarrollo

**Características:**
- Soporte para exportación/importación de datos en formato estándar (JSON/SQL)
- Diseño de schema escalable para soportar relaciones complejas entre entidades
- Seeding automático de datos canónicos mediante scripts Python

---

## Stack Detallado

| Componente | Tecnología | Versión | Propósito |
|-----------|-----------|---------|----------|
| **Lenguaje Backend** | Java | 21 | Performance, type safety, ecosystem maduro |
| **Framework Backend** | Spring Boot | 4.0.6 | Abstracción de boilerplate, producción-ready |
| **ORM** | JPA + Hibernate | Built-in | Mapeo objeto-relacional, queries optimizadas |
| **Seguridad** | Spring Security | 4.0.6 | Autenticación, autorización, filtros |
| **Autenticación** | JWT | 0.11.5 | Tokens stateless, escalable |
| **BD Principal** | MySQL | 8.0+ | ACID, confiable, escalable |
| **Lenguaje Frontend** | TypeScript | 6.0 | Type safety en frontend |
| **Framework Frontend** | Vue 3 | 3.5.32 | Reactivo, componentes, DX excelente |
| **Bundler** | Vite | 8.0.8 | Desarrollo rápido, build optimizado |
| **Componentes UI** | PrimeVue | 4.5.5 | Componentes profesionales, accesibles |
| **Parseo Markdown** | Marked | 18.0.5 | Renderizado de documentación en-app |
| **Contenedores** | Docker | Latest | Reproducibilidad de entorno |
| **Orquestación** | Docker Compose | - | Levantar stack completo en desarrollo |

---

## Instalación y Ejecución

### Requisitos previos
```
Java 21+
Node.js 20.19.0 o 22.12.0+
npm / pnpm
Docker & Docker Compose
MySQL 8.0+ (o levantar con Docker)
```

### Pasos de instalación actual

**1. Clonar repositorio**
```bash
git clone https://github.com/corona314/dnd-manager.git
cd dnd-manager
```

**2. Iniciar base de datos con Docker**
```bash
docker-compose up -d
```
Esto levanta MySQL en `localhost:3306` con credenciales de desarrollo.

**3. Ejecutar backend**
```bash
cd backend

# Compilar proyecto (descarga dependencias automáticamente)
./mvnw clean package

# Ejecutar en desarrollo (con hot reload)
./mvnw spring-boot:run
```
Backend disponible en `http://localhost:8080`

**4. Ejecutar frontend**
```bash
cd frontend/wizard

# Instalar dependencias
npm install

# Ejecutar en desarrollo (Vite HMR activo)
npm run dev
```
Frontend disponible en `http://localhost:5173`

### Build para producción

**Backend (JAR empaquetado):**
```bash
cd backend
./mvnw clean package -DskipTests
# Genera: target/app-0.0.1-SNAPSHOT.jar
java -jar target/app-0.0.1-SNAPSHOT.jar
```

**Frontend (Build estático):**
```bash
cd frontend/wizard
npm run build
# Genera: dist/ (listo para servir con nginx)
```

---

## Flujos de Desarrollo

### Ejemplo: Agregar nuevo endpoint de items

1. **Backend (Spring Boot)**
   - Crear método en `ItemService` con lógica
   - Crear `@RestController` endpoint en `ItemController`
   - Retornar `ItemSummaryDto` (no exposer entidad directa)

2. **Frontend (Vue 3)**
   - Crear función en `api/api.js` que llame al endpoint
   - Crear componente Vue que use la API
   - Agregar al router si es una nueva página

3. **Validación**
   - Probar manualmente lo desarrollado
   - Verificar hot reload en frontend
   - Revisar logs de Spring Boot para errores

---

## Notas de Desarrollo

### Por qué estas tecnologías

- **Spring Boot 4.0.6**: Versión más reciente, soporte a Java 21, nuevas optimizaciones
- **Vue 3 + TypeScript**: Balance entre curva de aprendizaje y potencia; type safety sin verbosidad de Java
- **Vite**: Desarrollo ultra-rápido (vs Webpack); tiempo de compilación <100ms
- **PrimeVue**: Componentes enterprise-grade sin necesidad de construir UI from scratch
- **MySQL**: ACID compliant, familiar para equipos, excelente soporte

### Decisiones de arquitectura

- **DTO segregation**: DTOs separados para cada tipo de entidad evita conflictos y hace explícito qué datos se exponen
- **Service layer**: Centraliza lógica de negocio, facilita testing
- **Local homebrew DB**: Evita centralización, permite que usuarios controlen su contenido

---

## Próximos Pasos

1. Completar wizard de creación de personajes (Q3 2026)
2. Implementar exportación de personajes a PDF
3. Iniciar construcción del creador de homebrew
4. Agregar foro básico de comunidad
5. Gestor de iniciativas
6. Sistema colaborativo DM-Players con WebSockets

---

## Autores

Desarrollado como proyecto portfolio serio entre dos devs: 
- Backend: Imanol Eguidón (corona314 en GitHub)
- Frontend: Irune Cereijo (Iruneca en GitHub)
