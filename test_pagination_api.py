import requests
import subprocess
import time
import json
import os
import signal
import sys

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
    print(f"{color}{text}{Colors.ENDC}")

def start_spring_boot_app():
    print_colored("Iniciando la aplicación Spring Boot...", Colors.HEADER)
    
    # Usar mvnw para sistemas Windows
    if os.name == 'nt':
        process = subprocess.Popen(["mvnw.cmd", "spring-boot:run"], 
                                  cwd="d:\\Windows\\Desktop\\Programacion\\code\\master-api")
    else:
        process = subprocess.Popen(["./mvnw", "spring-boot:run"], 
                                  cwd="d:/Windows/Desktop/Programacion/code/master-api")
    
    # Esperar a que la aplicación se inicie
    print_colored("Esperando a que la aplicación se inicie (30 segundos)...", Colors.WARNING)
    time.sleep(30)
    return process

def stop_spring_boot_app(process):
    print_colored("\nDeteniendo la aplicación Spring Boot...", Colors.HEADER)
    if os.name == 'nt':
        process.terminate()
    else:
        process.send_signal(signal.SIGTERM)
    process.wait()
    print_colored("Aplicación detenida.", Colors.OKGREEN)

def test_pagination(endpoint):
    print_colored(f"\nProbando paginación en {endpoint}", Colors.BOLD)
    
    # Probar diferentes parámetros de paginación
    test_cases = [
        {"page": 0, "size": 5},
        {"page": 1, "size": 3},
        {"page": 0, "size": 10, "sortBy": "id", "sortDir": "desc"}
    ]
    
    for i, params in enumerate(test_cases):
        try:
            print_colored(f"\nCaso de prueba {i+1}: {params}", Colors.OKBLUE)
            response = requests.get(f"{API_BASE_URL}{endpoint}", params=params)
            
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
                    
                    # Verificar que el tamaño de la página sea correcto
                    if len(data['content']) <= params['size']:
                        print_colored(f"✓ Tamaño de página correcto (≤ {params['size']})", Colors.OKGREEN)
                    else:
                        print_colored(f"✗ Tamaño de página incorrecto: {len(data['content'])} > {params['size']}", Colors.FAIL)
                else:
                    print_colored("✗ Estructura de paginación incorrecta", Colors.FAIL)
                    print(f"  Claves encontradas: {list(data.keys())}")
            else:
                print_colored(f"✗ Error en la respuesta: {response.status_code}", Colors.FAIL)
                print(response.text)
        except Exception as e:
            print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_related_endpoints():
    print_colored("\nProbando endpoints de relaciones con paginación", Colors.BOLD)
    
    # Primero obtener algunos IDs de entidades
    try:
        # Obtener ID de una categoría
        response = requests.get(f"{API_BASE_URL}/categories", params={"page": 0, "size": 1})
        if response.status_code == 200 and response.json()["content"]:
            category_id = response.json()["content"][0]["id"]
            print_colored(f"Usando categoría con ID: {category_id}", Colors.OKBLUE)
            
            # Probar endpoint de tareas de una categoría
            test_pagination(f"/categories/{category_id}/tasks")
        else:
            print_colored("No se encontraron categorías para probar endpoints relacionados", Colors.WARNING)
        
        # Obtener ID de una tarea
        response = requests.get(f"{API_BASE_URL}/tasks", params={"page": 0, "size": 1})
        if response.status_code == 200 and response.json()["content"]:
            task_id = response.json()["content"][0]["id"]
            print_colored(f"Usando tarea con ID: {task_id}", Colors.OKBLUE)
            
            # Probar endpoint de categorías de una tarea
            test_pagination(f"/tasks/{task_id}/categories")
        else:
            print_colored("No se encontraron tareas para probar endpoints relacionados", Colors.WARNING)
            
    except Exception as e:
        print_colored(f"Error al probar endpoints relacionados: {str(e)}", Colors.FAIL)

def main():
    try:
        # Iniciar la aplicación Spring Boot
        process = start_spring_boot_app()
        
        print_colored("\n=== INICIANDO PRUEBAS DE PAGINACIÓN ===\n", Colors.BOLD)
        
        # Probar endpoints principales
        for endpoint in ENDPOINTS:
            test_pagination(endpoint)
        
        # Probar endpoints de relaciones
        test_related_endpoints()
        
        print_colored("\n=== PRUEBAS COMPLETADAS ===\n", Colors.BOLD)
        
    except KeyboardInterrupt:
        print_colored("\nPrueba interrumpida por el usuario.", Colors.WARNING)
    except Exception as e:
        print_colored(f"\nError en la prueba: {str(e)}", Colors.FAIL)
    finally:
        # Detener la aplicación Spring Boot
        if 'process' in locals():
            stop_spring_boot_app(process)

if __name__ == "__main__":
    main()