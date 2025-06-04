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
    username = f"filetest_{timestamp}"
    email = f"filetest_{timestamp}@example.com"
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
            return None, None
        
        # Iniciar sesión
        login_data = {
            "usernameOrEmail": username,
            "password": password
        }
        
        response = requests.post(AUTH_ENDPOINTS["login"], json=login_data)
        if response.status_code != 200:
            print_colored(f"✗ Error al iniciar sesión: {response.text}", Colors.FAIL)
            return None, None
        
        data = response.json()
        print_colored("✓ Usuario registrado e iniciado sesión correctamente", Colors.OKGREEN)
        return data["token"], data["userId"]
    
    except Exception as e:
        print_colored(f"✗ Error en la autenticación: {str(e)}", Colors.FAIL)
        return None, None

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

def test_file_upload(token, entity_type, entity_id):
    print_colored(f"\nProbando carga de archivo para {entity_type} con ID {entity_id}", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    # Generar archivo de prueba
    file_obj, file_name = generate_test_file()
    
    # Preparar datos para la carga
    files = {
        "file": (file_name, file_obj, "text/plain")
    }
    
    data = {
        "entityType": entity_type.upper()[:-1],  # Convertir "categories" a "CATEGORY", etc.
        "entityId": entity_id
    }
    
    try:
        response = requests.post(
            FILE_ENDPOINTS["upload"],
            headers=headers,
            files=files,
            data=data
        )
        
        if response.status_code in [200, 201]:
            file_data = response.json()
            print_colored("✓ Archivo cargado correctamente", Colors.OKGREEN)
            print("Respuesta:")
            print_json(file_data)
            return file_data["id"]
        else:
            print_colored(f"✗ Error al cargar archivo: {response.text}", Colors.FAIL)
            return None
    except Exception as e:
        print_colored(f"✗ Error en la carga de archivo: {str(e)}", Colors.FAIL)
        return None

def test_file_download(token, file_id):
    print_colored(f"\nProbando descarga de archivo con ID {file_id}", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    try:
        response = requests.get(
            f"{FILE_ENDPOINTS['download']}/{file_id}",
            headers=headers
        )
        
        if response.status_code == 200:
            print_colored("✓ Archivo descargado correctamente", Colors.OKGREEN)
            print(f"  Content-Type: {response.headers.get('Content-Type')}")
            print(f"  Content-Disposition: {response.headers.get('Content-Disposition')}")
            print(f"  Tamaño del archivo: {len(response.content)} bytes")
            return True
        else:
            print_colored(f"✗ Error al descargar archivo: {response.text}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la descarga de archivo: {str(e)}", Colors.FAIL)
        return False

def test_list_files_by_entity(token, entity_type, entity_id):
    print_colored(f"\nProbando listado de archivos para {entity_type} con ID {entity_id}", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    try:
        response = requests.get(
            f"{FILE_ENDPOINTS['by_entity']}/{entity_type.upper()[:-1]}/{entity_id}",
            headers=headers
        )
        
        if response.status_code == 200:
            files = response.json()
            print_colored("✓ Listado de archivos obtenido correctamente", Colors.OKGREEN)
            print(f"  Número de archivos: {len(files)}")
            if files:
                print("  Primer archivo:")
                print_json(files[0])
            return files
        else:
            print_colored(f"✗ Error al obtener listado de archivos: {response.text}", Colors.FAIL)
            return []
    except Exception as e:
        print_colored(f"✗ Error al obtener listado de archivos: {str(e)}", Colors.FAIL)
        return []

def test_delete_file(token, file_id):
    print_colored(f"\nProbando eliminación de archivo con ID {file_id}", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    try:
        response = requests.delete(
            f"{FILE_ENDPOINTS['delete']}/{file_id}",
            headers=headers
        )
        
        if response.status_code == 200:
            print_colored("✓ Archivo eliminado correctamente", Colors.OKGREEN)
            return True
        else:
            print_colored(f"✗ Error al eliminar archivo: {response.text}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error al eliminar archivo: {str(e)}", Colors.FAIL)
        return False

def test_file_isolation(token1, token2, entity_type, entity_id, file_id):
    print_colored(f"\nProbando aislamiento de archivos entre usuarios", Colors.BOLD)
    
    headers1 = {"Authorization": f"Bearer {token1}"}
    headers2 = {"Authorization": f"Bearer {token2}"}
    
    # Usuario 1 debería poder acceder a su propio archivo
    try:
        response1 = requests.get(
            f"{FILE_ENDPOINTS['by_entity']}/{entity_type.upper()[:-1]}/{entity_id}",
            headers=headers1
        )
        
        if response1.status_code == 200 and any(file["id"] == file_id for file in response1.json()):
            print_colored("✓ Usuario 1 puede acceder a su propio archivo (esperado)", Colors.OKGREEN)
        else:
            print_colored("✗ Usuario 1 no puede acceder a su propio archivo (inesperado)", Colors.FAIL)
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
    
    # Usuario 2 NO debería poder acceder al archivo del usuario 1
    try:
        response2 = requests.get(
            f"{FILE_ENDPOINTS['download']}/{file_id}",
            headers=headers2
        )
        
        if response2.status_code in [403, 404]:
            print_colored("✓ Usuario 2 no puede acceder al archivo del usuario 1 (esperado)", Colors.OKGREEN)
        else:
            print_colored("✗ Usuario 2 puede acceder al archivo del usuario 1 (inesperado)", Colors.FAIL)
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def main():
    print_colored("=== PRUEBA DE API DE GESTIÓN DE ARCHIVOS ===\n", Colors.HEADER)
    
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
    
    # Registrar y autenticar dos usuarios para pruebas
    token1, user_id1 = register_and_login()
    token2, user_id2 = register_and_login()
    
    if not token1 or not token2:
        print_colored("✗ No se pudieron crear los usuarios de prueba", Colors.FAIL)
        return
    
    # Crear entidades para cada tipo
    entity_ids = {}
    file_ids = {}
    
    for entity_type in ENTITY_ENDPOINTS.keys():
        # Crear entidad
        entity_id = create_entity(token1, entity_type)
        if entity_id:
            entity_ids[entity_type] = entity_id
            
            # Cargar archivo para la entidad
            file_id = test_file_upload(token1, entity_type, entity_id)
            if file_id:
                file_ids[entity_type] = file_id
                
                # Listar archivos de la entidad
                test_list_files_by_entity(token1, entity_type, entity_id)
                
                # Descargar archivo
                test_file_download(token1, file_id)
                
                # Probar aislamiento entre usuarios
                test_file_isolation(token1, token2, entity_type, entity_id, file_id)
                
                # Eliminar archivo
                test_delete_file(token1, file_id)
                
                # Verificar que el archivo ya no existe
                files_after_delete = test_list_files_by_entity(token1, entity_type, entity_id)
                if not any(file["id"] == file_id for file in files_after_delete):
                    print_colored("✓ Archivo eliminado correctamente (no aparece en el listado)", Colors.OKGREEN)
                else:
                    print_colored("✗ El archivo sigue apareciendo en el listado después de eliminarlo", Colors.FAIL)
    
    print_colored("\n=== PRUEBAS COMPLETADAS ===", Colors.HEADER)

if __name__ == "__main__":
    main()