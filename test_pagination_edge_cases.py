import requests
import json
import sys
import time

# Configuración
API_BASE_URL = "http://localhost:8080"
ENDPOINTS = [
    "/categories",
    "/tasks",
    "/notes",
    "/habits"
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

def test_edge_cases(endpoint):
    print_colored(f"\nProbando casos extremos en {endpoint}", Colors.BOLD)
    
    # Casos de prueba para límites y casos extremos
    test_cases = [
        # Página negativa
        {"name": "Página negativa", "params": {"page": -1, "size": 10}},
        
        # Tamaño de página negativo
        {"name": "Tamaño negativo", "params": {"page": 0, "size": -5}},
        
        # Tamaño de página cero
        {"name": "Tamaño cero", "params": {"page": 0, "size": 0}},
        
        # Tamaño de página muy grande
        {"name": "Tamaño muy grande", "params": {"page": 0, "size": 1000}},
        
        # Página muy grande (que no existe)
        {"name": "Página inexistente", "params": {"page": 9999, "size": 10}},
        
        # Campo de ordenamiento inválido
        {"name": "Campo ordenamiento inválido", "params": {"page": 0, "size": 10, "sortBy": "campo_inexistente"}},
        
        # Dirección de ordenamiento inválida
        {"name": "Dirección ordenamiento inválida", "params": {"page": 0, "size": 10, "sortBy": "id", "sortDir": "invalid"}},
        
        # Sin parámetros (debería usar valores por defecto)
        {"name": "Sin parámetros", "params": {}}
    ]
    
    for case in test_cases:
        try:
            print_colored(f"\nCaso: {case['name']}", Colors.OKBLUE)
            print(f"Parámetros: {case['params']}")
            
            response = requests.get(f"{API_BASE_URL}{endpoint}", params=case['params'])
            
            print_colored(f"Código de respuesta: {response.status_code}", Colors.WARNING)
            
            if response.status_code == 200:
                data = response.json()
                print_colored("✓ Respuesta exitosa (200 OK)", Colors.OKGREEN)
                
                # Verificar estructura de respuesta paginada
                if all(key in data for key in ["content", "currentPage", "totalItems", "totalPages"]):
                    print_colored("✓ Estructura de paginación correcta", Colors.OKGREEN)
                    print(f"  - Página actual: {data['currentPage']}")
                    print(f"  - Elementos en página: {len(data['content'])}")
                    print(f"  - Total de elementos: {data['totalItems']}")
                    print(f"  - Total de páginas: {data['totalPages']}")
                    
                    # Verificaciones específicas según el caso
                    if case['name'] == "Página negativa":
                        if data['currentPage'] >= 0:
                            print_colored("✓ Manejo correcto de página negativa (usa página 0)", Colors.OKGREEN)
                        else:
                            print_colored("✗ Manejo incorrecto de página negativa", Colors.FAIL)
                    
                    elif case['name'] == "Tamaño negativo" or case['name'] == "Tamaño cero":
                        if len(data['content']) > 0:
                            print_colored("✓ Manejo correcto de tamaño inválido (usa tamaño por defecto)", Colors.OKGREEN)
                        else:
                            print_colored("✗ Manejo incorrecto de tamaño inválido", Colors.FAIL)
                    
                    elif case['name'] == "Página inexistente":
                        if len(data['content']) == 0 and data['currentPage'] == case['params']['page']:
                            print_colored("✓ Manejo correcto de página inexistente (devuelve página vacía)", Colors.OKGREEN)
                        else:
                            print_colored("✗ Manejo incorrecto de página inexistente", Colors.FAIL)
                    
                    elif case['name'] == "Sin parámetros":
                        print_colored("✓ Manejo correcto de solicitud sin parámetros (usa valores por defecto)", Colors.OKGREEN)
                else:
                    print_colored("✗ Estructura de paginación incorrecta", Colors.FAIL)
                    print(f"  Claves encontradas: {list(data.keys())}")
            elif response.status_code == 400:
                print_colored("✓ Respuesta 400 Bad Request (esperado para algunos casos inválidos)", Colors.OKGREEN)
                print(f"  Mensaje de error: {response.text}")
            else:
                print_colored(f"✗ Código de respuesta inesperado: {response.status_code}", Colors.FAIL)
                print(response.text)
        except Exception as e:
            print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_parameter_combinations(endpoint):
    print_colored(f"\nProbando combinaciones de parámetros en {endpoint}", Colors.BOLD)
    
    # Combinaciones de parámetros
    test_cases = [
        # Solo página
        {"name": "Solo página", "params": {"page": 1}},
        
        # Solo tamaño
        {"name": "Solo tamaño", "params": {"size": 5}},
        
        # Solo campo de ordenamiento
        {"name": "Solo campo ordenamiento", "params": {"sortBy": "id"}},
        
        # Solo dirección de ordenamiento
        {"name": "Solo dirección ordenamiento", "params": {"sortDir": "desc"}},
        
        # Página y ordenamiento sin tamaño
        {"name": "Página y ordenamiento", "params": {"page": 0, "sortBy": "id", "sortDir": "desc"}}
    ]
    
    for case in test_cases:
        try:
            print_colored(f"\nCaso: {case['name']}", Colors.OKBLUE)
            print(f"Parámetros: {case['params']}")
            
            response = requests.get(f"{API_BASE_URL}{endpoint}", params=case['params'])
            
            if response.status_code == 200:
                data = response.json()
                print_colored("✓ Respuesta exitosa (200 OK)", Colors.OKGREEN)
                
                # Verificar estructura de respuesta paginada
                if all(key in data for key in ["content", "currentPage", "totalItems", "totalPages"]):
                    print_colored("✓ Estructura de paginación correcta", Colors.OKGREEN)
                    print(f"  - Página actual: {data['currentPage']}")
                    print(f"  - Elementos en página: {len(data['content'])}")
                    print(f"  - Total de elementos: {data['totalItems']}")
                    print(f"  - Total de páginas: {data['totalPages']}")
                else:
                    print_colored("✗ Estructura de paginación incorrecta", Colors.FAIL)
                    print(f"  Claves encontradas: {list(data.keys())}")
            else:
                print_colored(f"✗ Error en la respuesta: {response.status_code}", Colors.FAIL)
                print(response.text)
        except Exception as e:
            print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def main():
    print_colored("=== PRUEBA DE CASOS EXTREMOS DE PAGINACIÓN ===\n", Colors.HEADER)
    print_colored("Verificando que la API esté en funcionamiento...", Colors.WARNING)
    
    # Verificar que la API esté en funcionamiento
    max_retries = 5
    for i in range(max_retries):
        try:
            response = requests.get(f"{API_BASE_URL}/categories", params={"page": 0, "size": 1})
            if response.status_code == 200:
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
    
    # Probar casos extremos en endpoints principales
    for endpoint in ENDPOINTS:
        test_edge_cases(endpoint)
    
    # Probar combinaciones de parámetros
    for endpoint in ENDPOINTS:
        test_parameter_combinations(endpoint)
    
    print_colored("\n=== PRUEBAS COMPLETADAS ===", Colors.HEADER)

if __name__ == "__main__":
    main()