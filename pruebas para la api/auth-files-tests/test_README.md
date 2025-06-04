# Scripts de Prueba para Autenticación y Gestión de Archivos

Este conjunto de scripts de Python permite probar la implementación de las nuevas funcionalidades de la API REST de Spring Boot:

1. **Soporte Multi-Usuario**: Autenticación, registro y aislamiento de datos entre usuarios.
2. **Soporte para Archivos e Imágenes**: Carga, descarga y gestión de archivos adjuntos a entidades.

## Requisitos Previos

- Python 3.6 o superior
- Biblioteca `requests` de Python
- API Spring Boot en funcionamiento

Para instalar la biblioteca `requests`, ejecuta:

```bash
pip install requests
```

## Scripts Disponibles

### 1. test_auth_api.py

Este script prueba la funcionalidad de autenticación y registro de usuarios.

**Características:**
- Prueba el registro de nuevos usuarios
- Prueba el inicio de sesión y obtención de tokens JWT
- Prueba el acceso a recursos protegidos con autenticación
- Prueba casos de error (credenciales inválidas, usuarios duplicados)

**Uso:**
```bash
python test_auth_api.py
```

### 2. test_file_api.py

Este script prueba la funcionalidad de gestión de archivos.

**Características:**
- Prueba la carga de archivos para diferentes entidades
- Prueba la descarga de archivos
- Prueba la obtención de listados de archivos por entidad
- Prueba la eliminación de archivos
- Prueba el aislamiento de archivos entre usuarios

**Uso:**
```bash
python test_file_api.py
```

### 3. test_user_isolation.py

Este script prueba el aislamiento de datos entre usuarios.

**Características:**
- Crea múltiples usuarios
- Prueba que cada usuario solo puede acceder a sus propios datos
- Prueba que las operaciones CRUD respetan el aislamiento de usuarios

**Uso:**
```bash
python test_user_isolation.py
```

### 4. test_edge_cases.py

Este script prueba casos extremos y manejo de errores.

**Características:**
- Prueba límites de tamaño de archivos
- Prueba tipos de archivos no permitidos
- Prueba acceso a recursos inexistentes
- Prueba tokens JWT inválidos o expirados

**Uso:**
```bash
python test_edge_cases.py
```

### 5. run_all_tests.py

Este script ejecuta todas las pruebas en secuencia.

**Uso:**
```bash
python run_all_tests.py
```

## Estructura de Respuesta Esperada

### Para autenticación:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "username": "usuario1"
}
```

### Para archivos:

```json
{
  "id": 1,
  "originalFileName": "imagen.jpg",
  "fileType": "image/jpeg",
  "fileSize": 12345,
  "uploadDate": "2023-05-20T14:30:45",
  "entityType": "CATEGORY",
  "entityId": 5,
  "userId": 1
}
```

## Consejos para Pruebas

1. **Prueba Completa:** Ejecuta `run_all_tests.py` para una prueba completa que incluye iniciar y detener la API.

2. **Pruebas Específicas:** Si la API ya está en funcionamiento, puedes ejecutar scripts individuales para pruebas más específicas.

3. **Datos de Prueba:** Los scripts crean sus propios datos de prueba, incluyendo usuarios y archivos.

4. **Verificación Visual:** Los scripts muestran información detallada sobre las respuestas para permitir una verificación visual del correcto funcionamiento.

5. **Códigos de Color:** Los scripts utilizan códigos de color en la consola para facilitar la lectura de los resultados:
   - Verde: Prueba exitosa
   - Rojo: Error
   - Amarillo: Advertencia
   - Azul: Información