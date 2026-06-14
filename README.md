# Backend API Guide

Esta guía describe las APIs disponibles en el backend de `dnd-manager` para que el equipo de frontend pueda consumirlas.

## Base URL

Cuando el backend está en ejecución, la ruta base es:

- `http://localhost:8080/api`


## Autenticación

### `POST /api/auth/login`

Request body:
```json
{
  "username": "usuario",
  "password": "clave"
}
```

Response:
```json
{
  "token": "OK_1"
}
```

### `POST /api/auth/register`

Request body:
```json
{
  "username": "usuario",
  "password": "clave"
}
```

Response:
```json
{
  "token": "OK<id>"
}
```

> Nota: el filtro de autenticación actual escucha tokens que comienzan con `Bearer OK_`. Por eso, para pruebas de frontend es más seguro usar `login`.

### Header para endpoints autenticados

Enviar el token así:

```
Authorization: Bearer OK_{userId}
```


## Endpoints de Items

### `GET /api/items`

Obtiene items generales.

Query params opcionales:
- `name`
- `weightMin`, `weightMax`
- `priceMin`, `priceMax`
- `itemType`
- `magic`
- `attunement`
- `rarity`
- `page` (default `0`)
- `size` (default `20`)

Respuesta: paginada (`Page<ItemSummaryDto>`).

### `GET /api/items/{id}`

Devuelve detalle de un item específico.

### `GET /api/items/armor`

Obtiene armaduras.

Query params opcionales:
- `name`
- `weightMin`, `weightMax`
- `priceMin`, `priceMax`
- `magic`
- `attunement`
- `rarity`
- `acMin`, `acMax`
- `str`
- `stealthDis`
- `armorType`
- `page`, `size`

### `GET /api/items/armor/{id}`

Detalle de armadura por id.

### `GET /api/items/weapons`

Obtiene armas.

Query params opcionales:
- `name`
- `weightMin`, `weightMax`
- `priceMin`, `priceMax`
- `magic`
- `attunement`
- `rarity`
- `rangeMin`, `rangeMax`
- `rangeNormalMin`, `rangeNormalMax`
- `rangeLongMin`, `rangeLongMax`
- `mastery`
- `damageTypes` (lista de strings)
- `page`, `size`

### `GET /api/items/weapons/{id}`

Detalle de arma por id.

### `GET /api/items/shields`

Obtiene escudos.

Query params opcionales:
- `name`
- `weightMin`, `weightMax`
- `priceMin`, `priceMax`
- `magic`
- `attunement`
- `rarity`
- `acBonus`
- `page`, `size`

### `GET /api/items/shield/{id}`

Detalle de escudo por id.


## Endpoints de Spells

### `GET /api/spells`

Obtiene hechizos.

Query params opcionales:
- `name`
- `levelMin`, `levelMax`
- `schoolId`
- `components`
- `concentration`
- `ritual`
- `savingThrowAbility`
- `attackRoll`
- `damageTypes` (lista de strings)
- `page`, `size`

### `GET /api/spells/{id}`

Detalle de hechizo por id.


## Endpoints de Characters

Estos endpoints requieren autenticación.

### `GET /api/characters/me`

Devuelve la lista de personajes del usuario autenticado.

### `GET /api/characters/{id}`

Devuelve detalle de un personaje del usuario autenticado.

### `POST /api/characters`

Crea un personaje.

Request body mínimo:
```json
{
  "name": "Mi Personaje"
}
```

Nota: aunque el DTO contiene `userId`, el backend usa el usuario autenticado.

### `PATCH /api/characters/{id}`

Actualiza un personaje.

Ejemplo de body:
```json
{
  "name": "Nuevo Nombre",
  "maxHp": 30,
  "currentHp": 28,
  "walkSpeed": 30,
  "flySpeed": 0,
  "speciesId": 1,
  "classId": 1,
  "subclassId": 2,
  "backgroundId": 3
}
```

### `PATCH /api/characters/{id}/abilities`

Reemplaza la lista de abilities del personaje.

Request body:
```json
[
  { "abilityId": 1, "baseValue": 15 },
  { "abilityId": 2, "baseValue": 14 }
]
```

### `DELETE /api/characters/{id}`

Elimina un personaje.


## Estructuras importantes

### ItemSummaryDto
- `id`
- `name`
- `weight`
- `price`
- `itemType`
- `magic`
- `attunement`
- `rarity`

### ItemResponseDto
- `name`
- `weight`
- `price`
- `itemType`
- `magic`
- `attunement`
- `rarity`
- `description`
- `features`
- `armorDto`
- `weaponDto`
- `shieldDto`

### SpellSummaryDto
- `id`
- `name`
- `level`
- `school`
- `components`
- `concentration`
- `ritual`
- `savingThrowAbility`
- `attackRoll`
- `damageTypes`

### SpellResponseDto
- `name`
- `level`
- `school`
- `castingTime`
- `range`
- `duration`
- `components`
- `material`
- `concentration`
- `ritual`
- `description`
- `savingThrowAbility`
- `attackRoll`
- `damageRoll`
- `damageTypes`

### CharacterSummaryDto
- `id`
- `name`
- `level`
- `status`
- `updatedAt`

### CharacterResponseDto
- `id`
- `name`
- `level`
- `maxHp`
- `currentHp`
- `walkSpeed`
- `flySpeed`
- `speciesId`
- `classId`
- `subclassId`
- `backgroundId`
- `status`
- `createdAt`
- `updatedAt`
- `finalizedAt`
- `abilities`
- `skills`
- `items`


## Nota sobre paginación

Los endpoints de listados usan `Page<T>`, por lo que la respuesta incluye campos de paginación y `content` con los registros.


## CORS

Permite `http://localhost:5173` con todos los métodos y headers.
