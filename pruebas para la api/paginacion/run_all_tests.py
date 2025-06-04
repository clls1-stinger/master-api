import subprocess
import os
import time
import sys

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

def run_script(script_name, description):
    print_colored(f"\n{'='*80}", Colors.HEADER)
    print_colored(f"Ejecutando: {script_name}", Colors.HEADER)
    print_colored(f"Descripción: {description}", Colors.HEADER)
    print_colored(f"{'='*80}\n", Colors.HEADER)
    
    try:
        # Ejecutar el script y mostrar su salida en tiempo real
        process = subprocess.Popen([sys.executable, script_name], 
                                  stdout=subprocess.PIPE,
                                  stderr=subprocess.STDOUT,
                                  universal_newlines=True,
                                  bufsize=1)
        
        # Mostrar la salida en tiempo real
        for line in process.stdout:
            print(line, end='')
        
        # Esperar a que termine el proceso
        process.wait()
        
        if process.returncode == 0:
            print_colored(f"\nOK Script {script_name} completado exitosamente\n", Colors.OKGREEN)
            return True
        else:
            print_colored(f"\nERROR Script {script_name} falló con código de salida {process.returncode}\n", Colors.FAIL)
            return False
    except Exception as e:
        print_colored(f"\nERROR Error al ejecutar {script_name}: {str(e)}\n", Colors.FAIL)
        return False

def start_spring_boot_app():
    print_colored("\nIniciando la aplicación Spring Boot...", Colors.HEADER)
    
    # Usar mvnw para sistemas Windows
    if os.name == 'nt':
        process = subprocess.Popen(["mvnw.cmd", "spring-boot:run"], 
                                  cwd="d:\\Windows\\Desktop\\Programacion\\code\\master-api",
                                  stdout=subprocess.PIPE,
                                  stderr=subprocess.STDOUT,
                                  universal_newlines=True)
    else:
        process = subprocess.Popen(["./mvnw", "spring-boot:run"], 
                                  cwd="d:/Windows/Desktop/Programacion/code/master-api",
                                  stdout=subprocess.PIPE,
                                  stderr=subprocess.STDOUT,
                                  universal_newlines=True)
    
    # Esperar a que la aplicación se inicie
    print_colored("Esperando a que la aplicación se inicie (30 segundos)...", Colors.WARNING)
    
    # Mostrar los primeros mensajes de inicio
    start_time = time.time()
    while time.time() - start_time < 10:  # Mostrar los primeros 10 segundos de logs
        line = process.stdout.readline()
        if not line:
            break
        print(line, end='')
    
    # Esperar el resto del tiempo sin mostrar logs
    remaining_time = 30 - (time.time() - start_time)
    if remaining_time > 0:
        print_colored(f"Esperando {int(remaining_time)} segundos más...", Colors.WARNING)
        time.sleep(remaining_time)
    
    return process

def stop_spring_boot_app(process):
    print_colored("\nDeteniendo la aplicación Spring Boot...", Colors.HEADER)
    if os.name == 'nt':
        subprocess.run(["taskkill", "/F", "/PID", str(process.pid), "/T"])
    else:
        process.terminate()
    process.wait()
    print_colored("Aplicación detenida.", Colors.OKGREEN)

def main():
    print_colored("=== EJECUTANDO TODAS LAS PRUEBAS DE PAGINACIÓN ===\n", Colors.BOLD)
    
    # Iniciar la aplicación Spring Boot
    spring_boot_process = start_spring_boot_app()
    
    try:
        # Lista de scripts a ejecutar con sus descripciones
        scripts = [
            ("test_relation_pagination.py", "Prueba la paginación en endpoints de relaciones"),
            ("test_sorting_pagination.py", "Prueba el ordenamiento en endpoints paginados"),
            ("test_pagination_edge_cases.py", "Prueba casos extremos de paginación")
        ]
        
        # Ejecutar cada script
        results = []
        for script, description in scripts:
            success = run_script(script, description)
            results.append((script, success))
        
        # Mostrar resumen de resultados
        print_colored("\n=== RESUMEN DE RESULTADOS ===\n", Colors.BOLD)
        all_success = True
        for script, success in results:
            status = "OK ÉXITO" if success else "ERROR FALLÓ"
            color = Colors.OKGREEN if success else Colors.FAIL
            print_colored(f"{status}: {script}", color)
            if not success:
                all_success = False
        
        if all_success:
            print_colored("\nOK TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE", Colors.OKGREEN)
        else:
            print_colored("\nERROR ALGUNAS PRUEBAS FALLARON", Colors.FAIL)
    
    except KeyboardInterrupt:
        print_colored("\nPruebas interrumpidas por el usuario.", Colors.WARNING)
    except Exception as e:
        print_colored(f"\nError durante la ejecución de las pruebas: {str(e)}", Colors.FAIL)
    finally:
        # Detener la aplicación Spring Boot
        stop_spring_boot_app(spring_boot_process)

if __name__ == "__main__":
    main()