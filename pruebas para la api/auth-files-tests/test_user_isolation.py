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
ENTITY_ENDPOINTS = {
    "categories": f"{API_BASE_URL}/categories",
    "tasks": f"{API_BASE_URL}/tasks",
    "notes": f"{API_BASE_URL}/notes",
    "habits": f"{API_BASE_URL}/habits"
}
FILE_ENDPOINTS = {
    "upload": f"{API_BASE_URL}/files/upload",
    "download": f"{API_BASE_URL}/files/download",
    "by_entity": f"{API_BASE_URL}/files/entity",
    "delete": f"{API_BASE_URL}/files"
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
    username = f"isoltest_{timestamp}"
    email = f"isoltest_{timestamp}@example.com"
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
        print_colored(f"✓ Usuario {username} registrado e iniciado sesión correctamente", Colors.OKGREEN)
        return data["token"], data["userId"], username
    
    except Exception as e:
        print_colored(f"✗ Error en la autenticación: {str(e)}", Colors.FAIL)
        return None, None, None

def create_entity(token, entity_type, name_prefix="Test"):
    print_colored(f"\nCreando entidad de tipo {entity_type}", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    # Generar nombre aleatorio para la entidad
    entity_name = f"{name_prefix} {entity_type} {int(time.time())}"
    
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
            print_colored(f"✓ Entidad {entity_type} creada correctamente con ID {data['id']}", Colors.OKGREEN)
            return data["id"], entity_name
        else:
            print_colored(f"✗ Error al crear entidad {entity_type}: {response.text}", Colors.FAIL)
            return None, None
    except Exception as e:
        print_colored(f"✗ Error en la creación de entidad: {str(e)}", Colors.FAIL)
        return None, None

def upload_file(token, entity_type, entity_id):
    print_colored(f"\nSubiendo archivo para {entity_type} con ID {entity_id}", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    # Generar archivo de prueba
    content = ''.join(random.choices(string.ascii_letters + string.digits, k=1024))  # 1KB
    file_obj = BytesIO(content.encode('utf-8'))
    file_name = f"test_file_{int(time.time())}.txt"
    
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
            return file_data["id"]
        else:
            print_colored(f"✗ Error al cargar archivo: {response.text}", Colors.FAIL)
            return None
    except Exception as e:
        print_colored(f"✗ Error en la carga de archivo: {str(e)}", Colors.FAIL)
        return None

def test_entity_isolation(entity_type):
    print_colored(f"\n=== PRUEBA DE AISLAMIENTO: {entity_type.upper()} ===", Colors.HEADER)
    
    # Crear dos usuarios
    token1, user_id1, username1 = register_and_login()
    token2, user_id2, username2 = register_and_login()
    
    if not token1 or not token2:
        print_colored("✗ No se pudieron crear los usuarios para la prueba", Colors.FAIL)
        return False
    
    print_colored(f"Usuario 1: {username1} (ID: {user_id1})", Colors.BOLD)
    print_colored(f"Usuario 2: {username2} (ID: {user_id2})", Colors.BOLD)
    
    # Usuario 1 crea entidades
    entity_id1, entity_name1 = create_entity(token1, entity_type, name_prefix="User1")
    if not entity_id1:
        print_colored(f"✗ No se pudo crear entidad para el usuario 1", Colors.FAIL)
        return False
    
    # Usuario 2 crea entidades
    entity_id2, entity_name2 = create_entity(token2, entity_type, name_prefix="User2")
    if not entity_id2:
        print_colored(f"✗ No se pudo crear entidad para el usuario 2", Colors.FAIL)
        return False
    
    # Prueba 1: Usuario 1 lista sus entidades
    print_colored("\nPrueba 1: Usuario 1 lista sus entidades", Colors.BOLD)
    headers1 = {"Authorization": f"Bearer {token1}"}
    
    try:
        response = requests.get(ENTITY_ENDPOINTS[entity_type], headers=headers1)
        
        if response.status_code == 200:
            data = response.json()
            content = data.get("content", [])
            
            # Verificar que la entidad del usuario 1 está en la lista
            user1_entity_found = any(str(item.get("id")) == str(entity_id1) for item in content)
            
            # Verificar que la entidad del usuario 2 NO está en la lista
            user2_entity_not_found = not any(str(item.get("id")) == str(entity_id2) for item in content)
            
            if user1_entity_found:
                print_colored("✓ La entidad del usuario 1 aparece en su propia lista", Colors.OKGREEN)
            else:
                print_colored("✗ La entidad del usuario 1 no aparece en su propia lista", Colors.FAIL)
            
            if user2_entity_not_found:
                print_colored("✓ La entidad del usuario 2 NO aparece en la lista del usuario 1", Colors.OKGREEN)
            else:
                print_colored("✗ La entidad del usuario 2 aparece en la lista del usuario 1 (fallo de aislamiento)", Colors.FAIL)
                return False
        else:
            print_colored(f"✗ Error al listar entidades: {response.text}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 2: Usuario 2 lista sus entidades
    print_colored("\nPrueba 2: Usuario 2 lista sus entidades", Colors.BOLD)
    headers2 = {"Authorization": f"Bearer {token2}"}
    
    try:
        response = requests.get(ENTITY_ENDPOINTS[entity_type], headers=headers2)
        
        if response.status_code == 200:
            data = response.json()
            content = data.get("content", [])
            
            # Verificar que la entidad del usuario 2 está en la lista
            user2_entity_found = any(str(item.get("id")) == str(entity_id2) for item in content)
            
            # Verificar que la entidad del usuario 1 NO está en la lista
            user1_entity_not_found = not any(str(item.get("id")) == str(entity_id1) for item in content)
            
            if user2_entity_found:
                print_colored("✓ La entidad del usuario 2 aparece en su propia lista", Colors.OKGREEN)
            else:
                print_colored("✗ La entidad del usuario 2 no aparece en su propia lista", Colors.FAIL)
            
            if user1_entity_not_found:
                print_colored("✓ La entidad del usuario 1 NO aparece en la lista del usuario 2", Colors.OKGREEN)
            else:
                print_colored("✗ La entidad del usuario 1 aparece en la lista del usuario 2 (fallo de aislamiento)", Colors.FAIL)
                return False
        else:
            print_colored(f"✗ Error al listar entidades: {response.text}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 3: Usuario 1 intenta acceder directamente a la entidad del usuario 2
    print_colored("\nPrueba 3: Usuario 1 intenta acceder a la entidad del usuario 2", Colors.BOLD)
    
    try:
        response = requests.get(f"{ENTITY_ENDPOINTS[entity_type]}/{entity_id2}", headers=headers1)
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 1 no puede acceder a la entidad del usuario 2", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 1 puede acceder a la entidad del usuario 2 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 4: Usuario 2 intenta acceder directamente a la entidad del usuario 1
    print_colored("\nPrueba 4: Usuario 2 intenta acceder a la entidad del usuario 1", Colors.BOLD)
    
    try:
        response = requests.get(f"{ENTITY_ENDPOINTS[entity_type]}/{entity_id1}", headers=headers2)
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 2 no puede acceder a la entidad del usuario 1", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 2 puede acceder a la entidad del usuario 1 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 5: Usuario 1 intenta modificar la entidad del usuario 2
    print_colored("\nPrueba 5: Usuario 1 intenta modificar la entidad del usuario 2", Colors.BOLD)
    
    update_data = {}
    if entity_type == "categories":
        update_data = {"name": "Modificado por usuario 1", "description": "Descripción modificada", "color": "#00FF00"}
    elif entity_type == "tasks":
        update_data = {"title": "Modificado por usuario 1", "description": "Descripción modificada", "completed": True}
    elif entity_type == "notes":
        update_data = {"title": "Modificado por usuario 1", "content": "Contenido modificado"}
    elif entity_type == "habits":
        update_data = {"name": "Modificado por usuario 1", "description": "Descripción modificada"}
    
    try:
        response = requests.put(
            f"{ENTITY_ENDPOINTS[entity_type]}/{entity_id2}",
            headers={**headers1, "Content-Type": "application/json"},
            json=update_data
        )
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 1 no puede modificar la entidad del usuario 2", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 1 puede modificar la entidad del usuario 2 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 6: Usuario 1 intenta eliminar la entidad del usuario 2
    print_colored("\nPrueba 6: Usuario 1 intenta eliminar la entidad del usuario 2", Colors.BOLD)
    
    try:
        response = requests.delete(f"{ENTITY_ENDPOINTS[entity_type]}/{entity_id2}", headers=headers1)
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 1 no puede eliminar la entidad del usuario 2", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 1 puede eliminar la entidad del usuario 2 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    print_colored(f"\n✓ Todas las pruebas de aislamiento para {entity_type} pasaron correctamente", Colors.OKGREEN)
    return True

def test_file_isolation():
    print_colored("\n=== PRUEBA DE AISLAMIENTO DE ARCHIVOS ===", Colors.HEADER)
    
    # Crear dos usuarios
    token1, user_id1, username1 = register_and_login()
    token2, user_id2, username2 = register_and_login()
    
    if not token1 or not token2:
        print_colored("✗ No se pudieron crear los usuarios para la prueba", Colors.FAIL)
        return False
    
    print_colored(f"Usuario 1: {username1} (ID: {user_id1})", Colors.BOLD)
    print_colored(f"Usuario 2: {username2} (ID: {user_id2})", Colors.BOLD)
    
    # Usuario 1 crea una entidad y sube un archivo
    entity_id1, _ = create_entity(token1, "categories")
    if not entity_id1:
        print_colored("✗ No se pudo crear entidad para el usuario 1", Colors.FAIL)
        return False
    
    file_id1 = upload_file(token1, "categories", entity_id1)
    if not file_id1:
        print_colored("✗ No se pudo subir archivo para el usuario 1", Colors.FAIL)
        return False
    
    # Usuario 2 crea una entidad y sube un archivo
    entity_id2, _ = create_entity(token2, "categories")
    if not entity_id2:
        print_colored("✗ No se pudo crear entidad para el usuario 2", Colors.FAIL)
        return False
    
    file_id2 = upload_file(token2, "categories", entity_id2)
    if not file_id2:
        print_colored("✗ No se pudo subir archivo para el usuario 2", Colors.FAIL)
        return False
    
    headers1 = {"Authorization": f"Bearer {token1}"}
    headers2 = {"Authorization": f"Bearer {token2}"}
    
    # Prueba 1: Usuario 1 intenta descargar el archivo del usuario 2
    print_colored("\nPrueba 1: Usuario 1 intenta descargar el archivo del usuario 2", Colors.BOLD)
    
    try:
        response = requests.get(f"{FILE_ENDPOINTS['download']}/{file_id2}", headers=headers1)
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 1 no puede descargar el archivo del usuario 2", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 1 puede descargar el archivo del usuario 2 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 2: Usuario 2 intenta descargar el archivo del usuario 1
    print_colored("\nPrueba 2: Usuario 2 intenta descargar el archivo del usuario 1", Colors.BOLD)
    
    try:
        response = requests.get(f"{FILE_ENDPOINTS['download']}/{file_id1}", headers=headers2)
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 2 no puede descargar el archivo del usuario 1", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 2 puede descargar el archivo del usuario 1 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 3: Usuario 1 intenta eliminar el archivo del usuario 2
    print_colored("\nPrueba 3: Usuario 1 intenta eliminar el archivo del usuario 2", Colors.BOLD)
    
    try:
        response = requests.delete(f"{FILE_ENDPOINTS['delete']}/{file_id2}", headers=headers1)
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 1 no puede eliminar el archivo del usuario 2", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 1 puede eliminar el archivo del usuario 2 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    # Prueba 4: Usuario 1 intenta listar archivos de la entidad del usuario 2
    print_colored("\nPrueba 4: Usuario 1 intenta listar archivos de la entidad del usuario 2", Colors.BOLD)
    
    try:
        response = requests.get(f"{FILE_ENDPOINTS['by_entity']}/CATEGORY/{entity_id2}", headers=headers1)
        
        if response.status_code in [403, 404]:
            print_colored("✓ Usuario 1 no puede listar archivos de la entidad del usuario 2", Colors.OKGREEN)
        else:
            print_colored(f"✗ Usuario 1 puede listar archivos de la entidad del usuario 2 (fallo de aislamiento): {response.status_code}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False
    
    print_colored("\n✓ Todas las pruebas de aislamiento de archivos pasaron correctamente", Colors.OKGREEN)
    return True

def main():
    print_colored("=== PRUEBAS DE AISLAMIENTO DE DATOS ENTRE USUARIOS ===\n", Colors.HEADER)
    
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
    
    # Pruebas de aislamiento para cada tipo de entidad
    results = {}
    for entity_type in ENTITY_ENDPOINTS.keys():
        results[entity_type] = test_entity_isolation(entity_type)
    
    # Prueba de aislamiento para archivos
    results["files"] = test_file_isolation()
    
    # Resumen de resultados
    print_colored("\n=== RESUMEN DE RESULTADOS ===", Colors.HEADER)
    all_passed = True
    
    for test_name, passed in results.items():
        if passed:
            print_colored(f"✓ {test_name}: PASÓ", Colors.OKGREEN)
        else:
            print_colored(f"✗ {test_name}: FALLÓ", Colors.FAIL)
            all_passed = False
    
    if all_passed:
        print_colored("\n✓ TODAS LAS PRUEBAS DE AISLAMIENTO PASARON CORRECTAMENTE", Colors.OKGREEN)
    else:
        print_colored("\n✗ ALGUNAS PRUEBAS DE AISLAMIENTO FALLARON", Colors.FAIL)

if __name__ == "__main__":
    main()