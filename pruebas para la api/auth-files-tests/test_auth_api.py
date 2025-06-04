import requests
import json
import time
import sys
import os

# Configuración
API_BASE_URL = "http://localhost:8080/api/v1"
AUTH_ENDPOINTS = {
    "register": f"{API_BASE_URL}/auth/register",
    "login": f"{API_BASE_URL}/auth/login"
}
PROTECTED_ENDPOINTS = [
    f"{API_BASE_URL}/categories",
    f"{API_BASE_URL}/tasks",
    f"{API_BASE_URL}/notes",
    f"{API_BASE_URL}/habits"
]

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

def test_register_user(username, email, password):
    print_colored(f"\nProbando registro de usuario: {username}", Colors.BOLD)
    
    # Datos para el registro
    register_data = {
        "username": username,
        "email": email,
        "password": password
    }
    
    try:
        response = requests.post(AUTH_ENDPOINTS["register"], json=register_data)
        
        print_colored(f"Código de respuesta: {response.status_code}", Colors.WARNING)
        
        if response.status_code == 201:
            print_colored("✓ Usuario registrado exitosamente", Colors.OKGREEN)
            return True
        else:
            print_colored(f"✗ Error al registrar usuario: {response.text}", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return False

def test_login_user(username_or_email, password):
    print_colored(f"\nProbando inicio de sesión: {username_or_email}", Colors.BOLD)
    
    # Datos para el login
    login_data = {
        "usernameOrEmail": username_or_email,
        "password": password
    }
    
    try:
        response = requests.post(AUTH_ENDPOINTS["login"], json=login_data)
        
        print_colored(f"Código de respuesta: {response.status_code}", Colors.WARNING)
        
        if response.status_code == 200:
            data = response.json()
            print_colored("✓ Inicio de sesión exitoso", Colors.OKGREEN)
            print("Respuesta:")
            print_json(data)
            
            # Verificar estructura de respuesta
            if all(key in data for key in ["token", "userId", "username"]):
                print_colored("✓ Estructura de respuesta correcta", Colors.OKGREEN)
                return data["token"], data["userId"]
            else:
                print_colored("✗ Estructura de respuesta incorrecta", Colors.FAIL)
                return None, None
        else:
            print_colored(f"✗ Error al iniciar sesión: {response.text}", Colors.FAIL)
            return None, None
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
        return None, None

def test_protected_endpoints(token):
    print_colored("\nProbando acceso a endpoints protegidos", Colors.BOLD)
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    for endpoint in PROTECTED_ENDPOINTS:
        try:
            print_colored(f"\nProbando acceso a {endpoint}", Colors.OKBLUE)
            
            # Probar sin token
            response_no_token = requests.get(endpoint)
            if response_no_token.status_code in [401, 403]:
                print_colored("✓ Acceso denegado sin token (esperado)", Colors.OKGREEN)
            else:
                print_colored(f"✗ Acceso permitido sin token (código: {response_no_token.status_code})", Colors.FAIL)
            
            # Probar con token
            response_with_token = requests.get(endpoint, headers=headers)
            if response_with_token.status_code == 200:
                print_colored("✓ Acceso permitido con token", Colors.OKGREEN)
                data = response_with_token.json()
                print(f"  - Elementos en respuesta: {len(data['content']) if 'content' in data else 'N/A'}")
            else:
                print_colored(f"✗ Acceso denegado con token (código: {response_with_token.status_code})", Colors.FAIL)
                print(response_with_token.text)
        except Exception as e:
            print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_invalid_credentials():
    print_colored("\nProbando credenciales inválidas", Colors.BOLD)
    
    # Casos de prueba para credenciales inválidas
    test_cases = [
        {"name": "Usuario inexistente", "data": {"usernameOrEmail": "noexiste", "password": "password123"}},
        {"name": "Contraseña incorrecta", "data": {"usernameOrEmail": "testuser1", "password": "wrongpassword"}},
        {"name": "Campos vacíos", "data": {"usernameOrEmail": "", "password": ""}}
    ]
    
    for case in test_cases:
        try:
            print_colored(f"\nCaso: {case['name']}", Colors.OKBLUE)
            
            response = requests.post(AUTH_ENDPOINTS["login"], json=case['data'])
            
            if response.status_code != 200:
                print_colored(f"✓ Rechazo correcto (código: {response.status_code})", Colors.OKGREEN)
                print(f"  Mensaje: {response.text}")
            else:
                print_colored("✗ Aceptación incorrecta de credenciales inválidas", Colors.FAIL)
                print_json(response.json())
        except Exception as e:
            print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_duplicate_registration():
    print_colored("\nProbando registro de usuario duplicado", Colors.BOLD)
    
    # Registrar un usuario
    username = "testuser_dup"
    email = "testdup@example.com"
    password = "password123"
    
    # Primer registro (debería ser exitoso)
    success = test_register_user(username, email, password)
    
    if success:
        # Intentar registrar el mismo usuario nuevamente
        print_colored("\nIntentando registrar el mismo usuario nuevamente", Colors.OKBLUE)
        
        register_data = {
            "username": username,
            "email": email,
            "password": password
        }
        
        try:
            response = requests.post(AUTH_ENDPOINTS["register"], json=register_data)
            
            if response.status_code != 201:
                print_colored(f"✓ Rechazo correcto de usuario duplicado (código: {response.status_code})", Colors.OKGREEN)
                print(f"  Mensaje: {response.text}")
            else:
                print_colored("✗ Aceptación incorrecta de usuario duplicado", Colors.FAIL)
        except Exception as e:
            print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)
    
    # Intentar registrar con el mismo email pero diferente username
    print_colored("\nIntentando registrar con el mismo email pero diferente username", Colors.OKBLUE)
    
    register_data = {
        "username": f"{username}_new",
        "email": email,  # Mismo email
        "password": password
    }
    
    try:
        response = requests.post(AUTH_ENDPOINTS["register"], json=register_data)
        
        if response.status_code != 201:
            print_colored(f"✓ Rechazo correcto de email duplicado (código: {response.status_code})", Colors.OKGREEN)
            print(f"  Mensaje: {response.text}")
        else:
            print_colored("✗ Aceptación incorrecta de email duplicado", Colors.FAIL)
    except Exception as e:
        print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def main():
    print_colored("=== PRUEBA DE API DE AUTENTICACIÓN ===\n", Colors.HEADER)
    
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
    
    # Generar nombres de usuario únicos basados en timestamp
    timestamp = int(time.time())
    username1 = f"testuser1_{timestamp}"
    username2 = f"testuser2_{timestamp}"
    email1 = f"test1_{timestamp}@example.com"
    email2 = f"test2_{timestamp}@example.com"
    password = "password123"
    
    # Probar registro de usuarios
    test_register_user(username1, email1, password)
    test_register_user(username2, email2, password)
    
    # Probar inicio de sesión
    token1, user_id1 = test_login_user(username1, password)
    token2, user_id2 = test_login_user(email2, password)  # Probar login con email
    
    # Probar acceso a endpoints protegidos
    if token1:
        test_protected_endpoints(token1)
    
    # Probar credenciales inválidas
    test_invalid_credentials()
    
    # Probar registro de usuario duplicado
    test_duplicate_registration()
    
    print_colored("\n=== PRUEBAS COMPLETADAS ===", Colors.HEADER)

if __name__ == "__main__":
    main()