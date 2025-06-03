# Scripts de Prueba para Paginación de API

Este conjunto de scripts de Python permite probar la implementación de paginación en la API REST de Spring Boot. Los scripts verifican que la paginación funcione correctamente en todos los endpoints, incluyendo el ordenamiento y la paginación en endpoints de relaciones.

## Requisitos Previos

- Python 3.6 o superior
- Biblioteca `requests` de Python
- API Spring Boot en funcionamiento

Para instalar la biblioteca `requests`, ejecuta:

```bash
pip install requests
```

## Scripts Disponibles

### 1. test_pagination_api.py

Este script principal inicia la aplicación Spring Boot y prueba la paginación en todos los endpoints principales.

**Características:**
- Inicia automáticamente la aplicación Spring Boot
- Prueba la paginación en `/categories`, `/tasks`, `/notes` y `/habits`
- Prueba endpoints de relaciones como `/categories/{id}/tasks`
- Verifica la estructura correcta de la respuesta paginada
- Detiene la aplicación al finalizar

**Uso:**
```bash
python test_pagination_api.py
```

### 2. test_relation_pagination.py

Este script se enfoca en probar la paginación en endpoints de relaciones, como `/categories/{id}/tasks` o `/tasks/{id}/categories`.

**Características:**
- Prueba detallada de endpoints de relaciones
- Muestra el contenido de las respuestas para verificación visual
- Verifica la estructura correcta de la respuesta paginada

**Uso:**
```bash
python test_relation_pagination.py
```

**Nota:** Este script asume que la API ya está en funcionamiento. Ejecuta primero la aplicación Spring Boot antes de usar este script.

### 3. test_sorting_pagination.py

Este script prueba específicamente la funcionalidad de ordenamiento en los endpoints paginados.

**Características:**
- Prueba el ordenamiento por diferentes campos (`id`, `name`, `createdAt`)
- Prueba ordenamiento ascendente y descendente
- Verifica visualmente si el ordenamiento es correcto
- Prueba ordenamiento en endpoints de relaciones

**Uso:**
```bash
python test_sorting_pagination.py
```

**Nota:** Este script asume que la API ya está en funcionamiento. Ejecuta primero la aplicación Spring Boot antes de usar este script.

## Estructura de Respuesta Esperada

Los scripts esperan que las respuestas paginadas tengan la siguiente estructura:

```json
{
  "content": [...],           // Contenido de la página actual
  "currentPage": 0,         // Número de página actual (0-indexed)
  "totalItems": 42,         // Total de elementos
  "totalPages": 5           // Total de páginas
}
```

## Consejos para Pruebas

1. **Prueba Completa:** Ejecuta `test_pagination_api.py` para una prueba completa que incluye iniciar y detener la API.

2. **Pruebas Específicas:** Si la API ya está en funcionamiento, puedes ejecutar `test_relation_pagination.py` o `test_sorting_pagination.py` para pruebas más específicas.

3. **Datos de Prueba:** Asegúrate de tener suficientes datos en la base de datos para probar la paginación efectivamente. Idealmente, deberías tener al menos 10-15 registros de cada entidad y algunas relaciones entre ellas.

4. **Verificación Visual:** Los scripts muestran información detallada sobre las respuestas para permitir una verificación visual del correcto funcionamiento.

5. **Códigos de Color:** Los scripts utilizan códigos de color en la consola para facilitar la lectura de los resultados:
   - Verde: Prueba exitosa
   - Rojo: Error
   - Amarillo: Advertencia
   - Azul: Información