import requests
import json
import time
import os
import sys
import random
import string
from io import BytesIO

# Configuración
API_BASE_URL = "http://localhost:8080/api/v1"
AUTH_ENDPOINTS = {
    "register": f"{API_BASE_URL}/auth/register",
    "login": f"{API_BASE_URL}/auth/login"
}
FILE_ENDPOINTS = {
    "upload": f"{API_BASE_URL}/files/upload",
    "download": f"{API_BASE_URL}/files/download",
    "by_entity": f"{API_BASE_URL}/files/entity",
    "delete": f"{API_BASE_URL}/files"
}
ENTITY_ENDPOINTS = {
    "categories": f"{API_BASE_URL}/categories",
    "tasks": f"{API_BASE_URL}/tasks",
    "notes": f"{API_BASE_URL}/notes",
    "habits": f"{API_BASE_URL}/habits"
}

# Colores para la consola
class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

def print_colored(text, color):
    # Reemplazar caracteres Unicode con equivalentes ASCII
    text = text.replace('\u2713', 'OK').replace('\u2717', 'ERROR')
    print(f"{color}{text}{Colors.ENDC}")

def print_json(data):
    print(json.dumps(data, indent=2))

def register_and_login():
    print_colored("\nRegistrando y autenticando usuario de prueba", Colors.BOLD)
    
    # Generar nombres de usuario únicos basados en timestamp
    timestamp = int(time.time())
    username = f"edgetest_{timestamp}"
    email = f"edgetest_{timestamp}@example.com"
    password = "password123"
    
    # Registrar usuario
    register_data = {
        "username": username,
        "email": email,
        "password": password
    }
    
    try:
        response = requests.post(AUTH_ENDPOINTS["register"], json=register_data)
        if response.status_code != 201:
            print_colored(f"✗ Error al registrar usuario: {response.text}", Colors.FAIL)
            return None, None, None
        
        # Iniciar sesión
        login_data = {
            "usernameOrEmail": username,
            "password": password
        }
        
        response = requests.post(AUTH_ENDPOINTS["login"], json=login_data)
        if response.status_code != 200:
            print_colored(f"✗ Error al iniciar sesión: {response.text}", Colors.FAIL)
            return None, None, None
        
        data = response.json()
        print_colored("✓ Usuario registrado e iniciado sesión correctamente", Colors.OKGREEN)
        return data["token"], data["userId"], username
    
    except Exception as e:
        print_colored(f"✗ Error en la autenticación: {str(e)}", Colors.FAIL)
        return None, None, None

def create_entity(token, entity_type):
    print_colored(f"\nCreando entidad de tipo {entity_type}", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    # Generar nombre aleatorio para la entidad
    entity_name = f"Test {entity_type} {int(time.time())}"
    
    # Datos para cada tipo de entidad
    entity_data = {}
    if entity_type == "categories":
        entity_data = {
            "name": entity_name,
            "description": f"Descripción de prueba para {entity_name}",
            "color": "#FF5733"
        }
    elif entity_type == "tasks":
        entity_data = {
            "title": entity_name,
            "description": f"Descripción de prueba para {entity_name}",
            "completed": False,
            "dueDate": "2023-12-31T23:59:59"
        }
    elif entity_type == "notes":
        entity_data = {
            "title": entity_name,
            "content": f"Contenido de prueba para {entity_name}"
        }
    elif entity_type == "habits":
        entity_data = {
            "name": entity_name,
            "description": f"Descripción de prueba para {entity_name}",
            "frequency": "DAILY",
            "targetValue": 1
        }
    
    try:
        response = requests.post(ENTITY_ENDPOINTS[entity_type], json=entity_data, headers=headers)
        
        if response.status_code in [200, 201]:
            data = response.json()
            print_colored(f"✓ Entidad {entity_type} creada correctamente", Colors.OKGREEN)
            return data["id"]
        else:
            print_colored(f"✗ Error al crear entidad {entity_type}: {response.text}", Colors.FAIL)
            return None
    except Exception as e:
        print_colored(f"✗ Error en la creación de entidad: {str(e)}", Colors.FAIL)
        return None

def generate_test_file(size_kb=10, file_type="txt"):
    print_colored(f"\nGenerando archivo de prueba de {size_kb}KB de tipo {file_type}", Colors.BOLD)
    
    # Generar contenido aleatorio
    content = ''.join(random.choices(string.ascii_letters + string.digits, k=size_kb * 1024))
    
    # Crear archivo en memoria
    file_obj = BytesIO(content.encode('utf-8'))
    
    # Generar nombre aleatorio
    file_name = f"test_file_{int(time.time())}.{file_type}"
    
    print_colored(f"✓ Archivo generado: {file_name}", Colors.OKGREEN)
    return file_obj, file_name

# CASOS DE PRUEBA PARA AUTENTICACIÓN

def test_auth_invalid_credentials():
    print_colored("\n=== PRUEBA: Inicio de sesión con credenciales inválidas ===", Colors.HEADER)
    
    login_data = {
        "usernameOrEmail": "usuario_inexistente",
        "password": "contraseña_incorrecta"
    }
    
    try:
        response = requests.post(AUTH_ENDPOINTS["login"], json=login_data)
        
        if response.status_code == 401:
            print_colored("✓ Prueba exitosa: Se rechazó el inicio de sesión con credenciales inválidas", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 401, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_auth_duplicate_registration():
    print_colored("\n=== PRUEBA: Registro de usuario duplicado ===", Colors.HEADER)
    
    # Crear un usuario
    timestamp = int(time.time())
    username = f"duplicate_{timestamp}"
    email = f"duplicate_{timestamp}@example.com"
    password = "password123"
    
    register_data = {
        "username": username,
        "email": email,
        "password": password
    }
    
    try:
        # Primer registro (debería ser exitoso)
        response1 = requests.post(AUTH_ENDPOINTS["register"], json=register_data)
        
        if response1.status_code != 201:
            print_colored(f"✗ Error en la configuración de la prueba: {response1.text}", Colors.FAIL)
            return
        
        # Intentar registrar el mismo usuario nuevamente
        response2 = requests.post(AUTH_ENDPOINTS["register"], json=register_data)
        
        if response2.status_code == 400:
            print_colored("✓ Prueba exitosa: Se rechazó el registro de usuario duplicado", Colors.OKGREEN)
            print(f"  Código de estado: {response2.status_code}")
            print(f"  Respuesta: {response2.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 400, se recibió {response2.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response2.text}")
        
        # Intentar registrar con el mismo email pero diferente username
        register_data["username"] = f"different_{timestamp}"
        response3 = requests.post(AUTH_ENDPOINTS["register"], json=register_data)
        
        if response3.status_code == 400:
            print_colored("✓ Prueba exitosa: Se rechazó el registro con email duplicado", Colors.OKGREEN)
            print(f"  Código de estado: {response3.status_code}")
            print(f"  Respuesta: {response3.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 400, se recibió {response3.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response3.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_auth_invalid_token():
    print_colored("\n=== PRUEBA: Acceso con token inválido ===", Colors.HEADER)
    
    # Token inválido
    invalid_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
    
    headers = {
        "Authorization": f"Bearer {invalid_token}"
    }
    
    try:
        response = requests.get(ENTITY_ENDPOINTS["categories"], headers=headers)
        
        if response.status_code in [401, 403]:
            print_colored("✓ Prueba exitosa: Se rechazó el acceso con token inválido", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 401 o 403, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

# CASOS DE PRUEBA PARA GESTIÓN DE ARCHIVOS

def test_file_upload_no_auth():
    print_colored("\n=== PRUEBA: Carga de archivo sin autenticación ===", Colors.HEADER)
    
    # Generar archivo de prueba
    file_obj, file_name = generate_test_file()
    
    # Preparar datos para la carga sin token
    files = {
        "file": (file_name, file_obj, "text/plain")
    }
    
    data = {
        "entityType": "CATEGORY",
        "entityId": "1"
    }
    
    try:
        response = requests.post(
            FILE_ENDPOINTS["upload"],
            files=files,
            data=data
        )
        
        if response.status_code in [401, 403]:
            print_colored("✓ Prueba exitosa: Se rechazó la carga de archivo sin autenticación", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 401 o 403, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_file_upload_invalid_entity():
    print_colored("\n=== PRUEBA: Carga de archivo con entidad inválida ===", Colors.HEADER)
    
    # Obtener token válido
    token, user_id, _ = register_and_login()
    if not token:
        print_colored("✗ No se pudo obtener token para la prueba", Colors.FAIL)
        return
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    # Generar archivo de prueba
    file_obj, file_name = generate_test_file()
    
    # Preparar datos para la carga con ID de entidad inexistente
    files = {
        "file": (file_name, file_obj, "text/plain")
    }
    
    data = {
        "entityType": "CATEGORY",
        "entityId": "999999999"  # ID que probablemente no existe
    }
    
    try:
        response = requests.post(
            FILE_ENDPOINTS["upload"],
            headers=headers,
            files=files,
            data=data
        )
        
        if response.status_code == 404:
            print_colored("✓ Prueba exitosa: Se rechazó la carga de archivo con entidad inválida", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 404, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_file_upload_invalid_type():
    print_colored("\n=== PRUEBA: Carga de archivo con tipo de entidad inválido ===", Colors.HEADER)
    
    # Obtener token válido
    token, user_id, _ = register_and_login()
    if not token:
        print_colored("✗ No se pudo obtener token para la prueba", Colors.FAIL)
        return
    
    # Crear una entidad válida
    entity_id = create_entity(token, "categories")
    if not entity_id:
        print_colored("✗ No se pudo crear entidad para la prueba", Colors.FAIL)
        return
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    # Generar archivo de prueba
    file_obj, file_name = generate_test_file()
    
    # Preparar datos para la carga con tipo de entidad inválido
    files = {
        "file": (file_name, file_obj, "text/plain")
    }
    
    data = {
        "entityType": "INVALID_TYPE",  # Tipo inválido
        "entityId": entity_id
    }
    
    try:
        response = requests.post(
            FILE_ENDPOINTS["upload"],
            headers=headers,
            files=files,
            data=data
        )
        
        if response.status_code == 400:
            print_colored("✓ Prueba exitosa: Se rechazó la carga de archivo con tipo de entidad inválido", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 400, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_file_upload_large_file():
    print_colored("\n=== PRUEBA: Carga de archivo grande ===", Colors.HEADER)
    
    # Obtener token válido
    token, user_id, _ = register_and_login()
    if not token:
        print_colored("✗ No se pudo obtener token para la prueba", Colors.FAIL)
        return
    
    # Crear una entidad válida
    entity_id = create_entity(token, "categories")
    if not entity_id:
        print_colored("✗ No se pudo crear entidad para la prueba", Colors.FAIL)
        return
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    # Generar archivo de prueba grande (10 MB)
    file_obj, file_name = generate_test_file(size_kb=10240)  # 10 MB
    
    # Preparar datos para la carga
    files = {
        "file": (file_name, file_obj, "text/plain")
    }
    
    data = {
        "entityType": "CATEGORY",
        "entityId": entity_id
    }
    
    try:
        response = requests.post(
            FILE_ENDPOINTS["upload"],
            headers=headers,
            files=files,
            data=data
        )
        
        # La API debería rechazar archivos demasiado grandes
        if response.status_code == 413 or (response.status_code == 400 and "tamaño" in response.text.lower()):
            print_colored("✓ Prueba exitosa: Se rechazó la carga de archivo demasiado grande", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 413 o 400, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_file_download_nonexistent():
    print_colored("\n=== PRUEBA: Descarga de archivo inexistente ===", Colors.HEADER)
    
    # Obtener token válido
    token, user_id, _ = register_and_login()
    if not token:
        print_colored("✗ No se pudo obtener token para la prueba", Colors.FAIL)
        return
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    # ID de archivo que probablemente no existe
    nonexistent_file_id = "999999999"
    
    try:
        response = requests.get(
            f"{FILE_ENDPOINTS['download']}/{nonexistent_file_id}",
            headers=headers
        )
        
        if response.status_code == 404:
            print_colored("✓ Prueba exitosa: Se rechazó la descarga de archivo inexistente", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 404, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_file_delete_nonexistent():
    print_colored("\n=== PRUEBA: Eliminación de archivo inexistente ===", Colors.HEADER)
    
    # Obtener token válido
    token, user_id, _ = register_and_login()
    if not token:
        print_colored("✗ No se pudo obtener token para la prueba", Colors.FAIL)
        return
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    # ID de archivo que probablemente no existe
    nonexistent_file_id = "999999999"
    
    try:
        response = requests.delete(
            f"{FILE_ENDPOINTS['delete']}/{nonexistent_file_id}",
            headers=headers
        )
        
        if response.status_code == 404:
            print_colored("✓ Prueba exitosa: Se rechazó la eliminación de archivo inexistente", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 404, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_cross_user_access():
    print_colored("\n=== PRUEBA: Acceso cruzado entre usuarios ===", Colors.HEADER)
    
    # Crear dos usuarios
    token1, user_id1, username1 = register_and_login()
    token2, user_id2, username2 = register_and_login()
    
    if not token1 or not token2:
        print_colored("✗ No se pudieron crear los usuarios para la prueba", Colors.FAIL)
        return
    
    print_colored(f"Usuario 1: {username1} (ID: {user_id1})", Colors.BOLD)
    print_colored(f"Usuario 2: {username2} (ID: {user_id2})", Colors.BOLD)
    
    # Usuario 1 crea una entidad y sube un archivo
    entity_id = create_entity(token1, "categories")
    if not entity_id:
        print_colored("✗ No se pudo crear entidad para la prueba", Colors.FAIL)
        return
    
    # Subir archivo como usuario 1
    headers1 = {"Authorization": f"Bearer {token1}"}
    file_obj, file_name = generate_test_file()
    
    files = {"file": (file_name, file_obj, "text/plain")}
    data = {"entityType": "CATEGORY", "entityId": entity_id}
    
    try:
        response = requests.post(
            FILE_ENDPOINTS["upload"],
            headers=headers1,
            files=files,
            data=data
        )
        
        if response.status_code not in [200, 201]:
            print_colored(f"✗ Error al subir archivo para la prueba: {response.text}", Colors.FAIL)
            return
        
        file_data = response.json()
        file_id = file_data["id"]
        
        print_colored(f"Archivo subido por usuario 1 con ID: {file_id}", Colors.BOLD)
        
        # Usuario 2 intenta acceder al archivo del usuario 1
        headers2 = {"Authorization": f"Bearer {token2}"}
        
        # Intento de descarga
        response = requests.get(
            f"{FILE_ENDPOINTS['download']}/{file_id}",
            headers=headers2
        )
        
        if response.status_code in [403, 404]:
            print_colored("✓ Prueba exitosa: Usuario 2 no puede descargar el archivo del usuario 1", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 403 o 404, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
        
        # Intento de eliminación
        response = requests.delete(
            f"{FILE_ENDPOINTS['delete']}/{file_id}",
            headers=headers2
        )
        
        if response.status_code in [403, 404]:
            print_colored("✓ Prueba exitosa: Usuario 2 no puede eliminar el archivo del usuario 1", Colors.OKGREEN)
            print(f"  Código de estado: {response.status_code}")
            print(f"  Respuesta: {response.text}")
        else:
            print_colored(f"✗ Prueba fallida: Se esperaba código 403 o 404, se recibió {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
        
        # Verificar que el usuario 1 todavía puede acceder a su archivo
        response = requests.get(
            f"{FILE_ENDPOINTS['download']}/{file_id}",
            headers=headers1
        )
        
        if response.status_code == 200:
            print_colored("✓ Prueba exitosa: Usuario 1 todavía puede acceder a su archivo", Colors.OKGREEN)
        else:
            print_colored(f"✗ Prueba fallida: Usuario 1 no puede acceder a su archivo, código {response.status_code}", Colors.FAIL)
            print(f"  Respuesta: {response.text}")
        
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def main():
    print_colored("=== PRUEBAS DE CASOS LÍMITE Y ERRORES ===\n", Colors.HEADER)
    
    print_colored("Verificando que la API esté en funcionamiento...", Colors.WARNING)
    
    # Verificar que la API esté en funcionamiento
    max_retries = 5
    for i in range(max_retries):
        try:
            response = requests.get(f"{API_BASE_URL}/categories", params={"page": 0, "size": 1})
            if response.status_code in [200, 401, 403]:  # Cualquiera de estos códigos indica que la API está funcionando
                print_colored("✓ API en funcionamiento", Colors.OKGREEN)
                break
        except requests.exceptions.ConnectionError:
            if i < max_retries - 1:
                print_colored(f"Intento {i+1}/{max_retries}: API no disponible, reintentando en 5 segundos...", Colors.WARNING)
                time.sleep(5)
            else:
                print_colored("✗ No se pudo conectar con la API después de varios intentos", Colors.FAIL)
                print_colored("  Asegúrate de que la API esté en funcionamiento antes de ejecutar este script", Colors.FAIL)
                return
    
    # Pruebas de autenticación
    test_auth_invalid_credentials()
    test_auth_duplicate_registration()
    test_auth_invalid_token()
    
    # Pruebas de gestión de archivos
    test_file_upload_no_auth()
    test_file_upload_invalid_entity()
    test_file_upload_invalid_type()
    test_file_upload_large_file()
    test_file_download_nonexistent()
    test_file_delete_nonexistent()
    test_cross_user_access()
    
    print_colored("\n=== PRUEBAS COMPLETADAS ===", Colors.HEADER)

if __name__ == "__main__":
    main()