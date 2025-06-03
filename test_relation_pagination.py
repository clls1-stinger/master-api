import requests
import json
import sys
import time

# Configuración
API_BASE_URL = "http://localhost:8080"

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

def test_relation_endpoint(base_entity, entity_id, related_entity):
    endpoint = f"{API_BASE_URL}/{base_entity}/{entity_id}/{related_entity}"
    print_colored(f"\nProbando endpoint: {endpoint}", Colors.BOLD)
    
    # Probar diferentes parámetros de paginación
    test_cases = [
        {"page": 0, "size": 2},
        {"page": 0, "size": 5, "sortBy": "id", "sortDir": "desc"},
        {"page": 1, "size": 2}
    ]
    
    for i, params in enumerate(test_cases):
        try:
            print_colored(f"\nCaso de prueba {i+1}: {params}", Colors.OKBLUE)
            response = requests.get(endpoint, params=params)
            
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
                    
                    # Mostrar contenido resumido
                    print_colored("\nContenido de la página:", Colors.OKBLUE)
                    print_json(data["content"])
                    
                    # Verificar que el tamaño de la página sea correcto
                    if len(data['content']) <= params['size']:
                        print_colored(f"✓ Tamaño de página correcto (≤ {params['size']})", Colors.OKGREEN)
                    else:
                        print_colored(f"✗ Tamaño de página incorrecto: {len(data['content'])} > {params['size']}", Colors.FAIL)
                    
                    # Verificar que la paginación funciona correctamente
                    if params['page'] > 0 and data['currentPage'] == params['page']:
                        print_colored(f"✓ Número de página correcto: {data['currentPage']}", Colors.OKGREEN)
                    elif params['page'] == 0 and data['currentPage'] == 0:
                        print_colored(f"✓ Número de página correcto: {data['currentPage']}", Colors.OKGREEN)
                    else:
                        print_colored(f"✗ Número de página incorrecto: {data['currentPage']} != {params['page']}", Colors.FAIL)
                else:
                    print_colored("✗ Estructura de paginación incorrecta", Colors.FAIL)
                    print(f"  Claves encontradas: {list(data.keys())}")
                    print_colored("\nRespuesta recibida:", Colors.WARNING)
                    print_json(data)
            else:
                print_colored(f"✗ Error en la respuesta: {response.status_code}", Colors.FAIL)
                print(response.text)
        except Exception as e:
            print_colored(f"✗ Error en la prueba: {str(e)}", Colors.FAIL)

def get_entity_ids():
    entities = {}
    
    # Obtener ID de una categoría
    try:
        response = requests.get(f"{API_BASE_URL}/categories", params={"page": 0, "size": 1})
        if response.status_code == 200 and response.json()["content"]:
            entities["category"] = response.json()["content"][0]["id"]
            print_colored(f"Categoría encontrada con ID: {entities['category']}", Colors.OKGREEN)
        else:
            print_colored("No se encontraron categorías", Colors.WARNING)
    except Exception as e:
        print_colored(f"Error al obtener categorías: {str(e)}", Colors.FAIL)
    
    # Obtener ID de una tarea
    try:
        response = requests.get(f"{API_BASE_URL}/tasks", params={"page": 0, "size": 1})
        if response.status_code == 200 and response.json()["content"]:
            entities["task"] = response.json()["content"][0]["id"]
            print_colored(f"Tarea encontrada con ID: {entities['task']}", Colors.OKGREEN)
        else:
            print_colored("No se encontraron tareas", Colors.WARNING)
    except Exception as e:
        print_colored(f"Error al obtener tareas: {str(e)}", Colors.FAIL)
    
    # Obtener ID de una nota
    try:
        response = requests.get(f"{API_BASE_URL}/notes", params={"page": 0, "size": 1})
        if response.status_code == 200 and response.json()["content"]:
            entities["note"] = response.json()["content"][0]["id"]
            print_colored(f"Nota encontrada con ID: {entities['note']}", Colors.OKGREEN)
        else:
            print_colored("No se encontraron notas", Colors.WARNING)
    except Exception as e:
        print_colored(f"Error al obtener notas: {str(e)}", Colors.FAIL)
    
    # Obtener ID de un hábito
    try:
        response = requests.get(f"{API_BASE_URL}/habits", params={"page": 0, "size": 1})
        if response.status_code == 200 and response.json()["content"]:
            entities["habit"] = response.json()["content"][0]["id"]
            print_colored(f"Hábito encontrado con ID: {entities['habit']}", Colors.OKGREEN)
        else:
            print_colored("No se encontraron hábitos", Colors.WARNING)
    except Exception as e:
        print_colored(f"Error al obtener hábitos: {str(e)}", Colors.FAIL)
    
    return entities

def main():
    print_colored("=== PRUEBA DE PAGINACIÓN EN ENDPOINTS DE RELACIONES ===\n", Colors.HEADER)
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
    
    # Obtener IDs de entidades para probar
    print_colored("\nObteniendo IDs de entidades para pruebas...", Colors.HEADER)
    entities = get_entity_ids()
    
    # Probar endpoints de relaciones
    if "category" in entities:
        test_relation_endpoint("categories", entities["category"], "tasks")
    
    if "task" in entities:
        test_relation_endpoint("tasks", entities["task"], "categories")
    
    if "note" in entities:
        test_relation_endpoint("notes", entities["note"], "categories")
    
    if "habit" in entities:
        test_relation_endpoint("habits", entities["habit"], "categories")
    
    print_colored("\n=== PRUEBAS COMPLETADAS ===", Colors.HEADER)

if __name__ == "__main__":
    main()