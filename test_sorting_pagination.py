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

def test_sorting(endpoint):
    print_colored(f"\nProbando ordenamiento en {endpoint}", Colors.BOLD)
    
    # Campos comunes por los que ordenar
    sort_fields = ["id", "name", "createdAt"]
    
    # Probar diferentes parámetros de ordenamiento
    for field in sort_fields:
        for direction in ["asc", "desc"]:
            params = {
                "page": 0,
                "size": 10,
                "sortBy": field,
                "sortDir": direction
            }
            
            try:
                print_colored(f"\nOrdenando por {field} en orden {direction}", Colors.OKBLUE)
                response = requests.get(f"{API_BASE_URL}{endpoint}", params=params)
                
                if response.status_code == 200:
                    data = response.json()
                    print_colored("✓ Respuesta exitosa (200 OK)", Colors.OKGREEN)
                    
                    # Verificar que hay contenido para analizar
                    if data["content"] and len(data["content"]) > 1:
                        # Intentar verificar el ordenamiento
                        try:
                            # Verificar si el campo existe en la respuesta
                            if field in data["content"][0]:
                                # Extraer valores del campo para verificar ordenamiento
                                values = [item[field] for item in data["content"] if field in item]
                                
                                # Verificar si los valores están ordenados
                                is_sorted = all(values[i] <= values[i+1] for i in range(len(values)-1)) if direction == "asc" else \
                                            all(values[i] >= values[i+1] for i in range(len(values)-1))
                                
                                if is_sorted:
                                    print_colored(f"✓ Ordenamiento correcto por {field} en orden {direction}", Colors.OKGREEN)
                                else:
                                    print_colored(f"✗ Ordenamiento incorrecto por {field} en orden {direction}", Colors.FAIL)
                                    print_colored("Valores encontrados:", Colors.WARNING)
                                    print(values)
                            else:
                                print_colored(f"Campo {field} no encontrado en la respuesta", Colors.WARNING)
                        except Exception as e:
                            print_colored(f"No se pudo verificar el ordenamiento: {str(e)}", Colors.WARNING)
                    else:
                        print_colored("No hay suficientes elementos para verificar el ordenamiento", Colors.WARNING)
                    
                    # Mostrar primeros elementos para verificación visual
                    print_colored("\nPrimeros elementos de la respuesta:", Colors.OKBLUE)
                    for i, item in enumerate(data["content"][:3]):
                        print(f"Elemento {i+1}:")
                        if field in item:
                            print(f"  {field}: {item[field]}")
                        else:
                            print(f"  {field}: No disponible")
                else:
                    print_colored(f"✗ Error en la respuesta: {response.status_code}", Colors.FAIL)
                    print(response.text)
            except Exception as e:
                print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def test_relation_sorting(base_entity, entity_id, related_entity):
    endpoint = f"{API_BASE_URL}/{base_entity}/{entity_id}/{related_entity}"
    print_colored(f"\nProbando ordenamiento en endpoint de relación: {endpoint}", Colors.BOLD)
    
    # Campos comunes por los que ordenar en relaciones
    sort_fields = ["id", "name"]
    
    # Probar diferentes parámetros de ordenamiento
    for field in sort_fields:
        for direction in ["asc", "desc"]:
            params = {
                "page": 0,
                "size": 10,
                "sortBy": field,
                "sortDir": direction
            }
            
            try:
                print_colored(f"\nOrdenando por {field} en orden {direction}", Colors.OKBLUE)
                response = requests.get(endpoint, params=params)
                
                if response.status_code == 200:
                    data = response.json()
                    print_colored("✓ Respuesta exitosa (200 OK)", Colors.OKGREEN)
                    
                    # Verificar que hay contenido para analizar
                    if data["content"] and len(data["content"]) > 1:
                        # Mostrar primeros elementos para verificación visual
                        print_colored("\nPrimeros elementos de la respuesta:", Colors.OKBLUE)
                        for i, item in enumerate(data["content"][:3]):
                            print(f"Elemento {i+1}:")
                            if field in item:
                                print(f"  {field}: {item[field]}")
                            else:
                                print(f"  {field}: No disponible")
                    else:
                        print_colored("No hay suficientes elementos para verificar el ordenamiento", Colors.WARNING)
                else:
                    print_colored(f"✗ Error en la respuesta: {response.status_code}", Colors.FAIL)
                    print(response.text)
            except Exception as e:
                print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def get_entity_id(entity_type):
    try:
        response = requests.get(f"{API_BASE_URL}/{entity_type}", params={"page": 0, "size": 1})
        if response.status_code == 200 and response.json()["content"]:
            return response.json()["content"][0]["id"]
    except Exception:
        pass
    return None

def main():
    print_colored("=== PRUEBA DE ORDENAMIENTO EN ENDPOINTS PAGINADOS ===\n", Colors.HEADER)
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
    
    # Probar ordenamiento en endpoints principales
    for endpoint in ENDPOINTS:
        test_sorting(endpoint)
    
    # Probar ordenamiento en endpoints de relaciones
    print_colored("\n=== PROBANDO ORDENAMIENTO EN ENDPOINTS DE RELACIONES ===\n", Colors.HEADER)
    
    # Obtener IDs para pruebas de relaciones
    category_id = get_entity_id("categories")
    task_id = get_entity_id("tasks")
    note_id = get_entity_id("notes")
    habit_id = get_entity_id("habits")
    
    if category_id:
        test_relation_sorting("categories", category_id, "tasks")
    else:
        print_colored("No se encontraron categorías para probar", Colors.WARNING)
    
    if task_id:
        test_relation_sorting("tasks", task_id, "categories")
    else:
        print_colored("No se encontraron tareas para probar", Colors.WARNING)
    
    if note_id:
        test_relation_sorting("notes", note_id, "categories")
    else:
        print_colored("No se encontraron notas para probar", Colors.WARNING)
    
    if habit_id:
        test_relation_sorting("habits", habit_id, "categories")
    else:
        print_colored("No se encontraron hábitos para probar", Colors.WARNING)
    
    print_colored("\n=== PRUEBAS COMPLETADAS ===", Colors.HEADER)

if __name__ == "__main__":
    main()