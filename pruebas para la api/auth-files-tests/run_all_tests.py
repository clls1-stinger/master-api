import subprocess
import sys
import time
import os
import signal
import platform

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

def run_script(script_name):
    """Ejecuta un script de Python y muestra la salida en tiempo real"""
    print_colored(f"\n{'='*60}", Colors.HEADER)
    print_colored(f"EJECUTANDO: {script_name}", Colors.HEADER)
    print_colored(f"{'='*60}", Colors.HEADER)
    
    try:
        # Ejecutar el script con Python
        process = subprocess.Popen(
            [sys.executable, script_name],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            universal_newlines=True,
            bufsize=1
        )
        
        # Leer la salida línea por línea
        while True:
            output = process.stdout.readline()
            if output == '' and process.poll() is not None:
                break
            if output:
                print(output.strip())
        
        # Obtener el código de salida
        return_code = process.poll()
        
        if return_code == 0:
            print_colored(f"\n✓ {script_name} completado exitosamente", Colors.OKGREEN)
            return True
        else:
            print_colored(f"\n✗ {script_name} falló con código de salida {return_code}", Colors.FAIL)
            return False
    
    except Exception as e:
        print_colored(f"\n✗ Error al ejecutar {script_name}: {str(e)}", Colors.FAIL)
        return False

def start_spring_boot_app():
    """Inicia la aplicación Spring Boot"""
    print_colored("\nIniciando aplicación Spring Boot...", Colors.WARNING)
    
    # Cambiar al directorio raíz del proyecto
    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    
    try:
        # Determinar el comando según el sistema operativo
        if platform.system() == "Windows":
            mvnw_path = os.path.join(project_root, "mvnw.cmd")
            cmd = [mvnw_path, "spring-boot:run"]
        else:
            mvnw_path = os.path.join(project_root, "mvnw")
            cmd = [mvnw_path, "spring-boot:run"]
        
        # Verificar que el archivo mvnw existe
        if not os.path.exists(mvnw_path):
            print_colored(f"\n✗ No se encontró el archivo Maven wrapper en: {mvnw_path}", Colors.FAIL)
            return None
        
        # Iniciar la aplicación
        process = subprocess.Popen(
            cmd,
            cwd=project_root,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            universal_newlines=True
        )
        
        # Esperar a que la aplicación se inicie
        print_colored("Esperando a que la aplicación se inicie...", Colors.WARNING)
        
        # Leer la salida hasta encontrar indicadores de que la aplicación está lista
        startup_timeout = 120  # 2 minutos de timeout
        start_time = time.time()
        app_started = False
        
        while time.time() - start_time < startup_timeout:
            line = process.stdout.readline()
            if line:
                print(line.strip())
                # Buscar indicadores de que la aplicación está lista
                if "Started MasterApiApplication" in line or "Tomcat started on port" in line:
                    print_colored("\n✓ Aplicación Spring Boot iniciada correctamente", Colors.OKGREEN)
                    app_started = True
                    return process
                # Si vemos BUILD SUCCESS, la aplicación se compiló pero puede tener errores de runtime
                elif "BUILD SUCCESS" in line and not app_started:
                    print_colored("\n⚠️  La aplicación se compiló pero puede tener errores de runtime", Colors.WARNING)
                    print_colored("Intentando continuar con las pruebas...", Colors.WARNING)
                    # Esperar un poco más para ver si se inicia
                    time.sleep(5)
                    # Verificar si hay un servidor corriendo en el puerto 8080
                    try:
                        import socket
                        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                        result = sock.connect_ex(('localhost', 8080))
                        sock.close()
                        if result == 0:
                            print_colored("\n✓ Servidor detectado en puerto 8080", Colors.OKGREEN)
                            return process
                    except:
                        pass
            
            # Verificar si el proceso ha terminado inesperadamente
            if process.poll() is not None:
                print_colored("\n✗ La aplicación Spring Boot terminó inesperadamente", Colors.FAIL)
                print_colored("Esto puede deberse a errores de configuración o dependencias", Colors.WARNING)
                return None
        
        print_colored("\n✗ Timeout al iniciar la aplicación Spring Boot", Colors.FAIL)
        process.terminate()
        return None
    
    except Exception as e:
        print_colored(f"\n✗ Error al iniciar la aplicación Spring Boot: {str(e)}", Colors.FAIL)
        return None

def stop_spring_boot_app(process):
    """Detiene la aplicación Spring Boot"""
    if process is None:
        return
    
    print_colored("\nDeteniendo aplicación Spring Boot...", Colors.WARNING)
    
    try:
        if platform.system() == "Windows":
            # En Windows, usar taskkill para terminar el proceso y sus hijos
            subprocess.run(["taskkill", "/F", "/T", "/PID", str(process.pid)], 
                         capture_output=True)
        else:
            # En otros sistemas, usar terminate
            process.terminate()
            process.wait(timeout=10)
        
        print_colored("✓ Aplicación Spring Boot detenida", Colors.OKGREEN)
    
    except Exception as e:
        print_colored(f"✗ Error al detener la aplicación: {str(e)}", Colors.FAIL)
        try:
            process.kill()
        except:
            pass

def main():
    print_colored("="*80, Colors.HEADER)
    print_colored("EJECUTOR DE PRUEBAS PARA AUTENTICACIÓN Y GESTIÓN DE ARCHIVOS", Colors.HEADER)
    print_colored("="*80, Colors.HEADER)
    
    # Verificar que estamos en el directorio correcto
    current_dir = os.path.dirname(os.path.abspath(__file__))
    print_colored(f"Directorio actual: {current_dir}", Colors.BOLD)
    
    # Iniciar la aplicación Spring Boot
    spring_boot_process = start_spring_boot_app()
    
    if spring_boot_process is None:
        print_colored("\n✗ No se pudo iniciar la aplicación Spring Boot. Abortando pruebas.", Colors.FAIL)
        return
    
    try:
        # Esperar un poco más para asegurar que la aplicación esté completamente lista
        print_colored("\nEsperando 10 segundos adicionales para asegurar que la API esté lista...", Colors.WARNING)
        time.sleep(10)
        
        # Lista de scripts de prueba a ejecutar
        test_scripts = [
            "test_auth_api.py",
            "test_file_api.py",
            "test_user_isolation.py",
            "test_edge_cases.py"
        ]
        
        # Ejecutar cada script de prueba
        results = {}
        
        for script in test_scripts:
            script_path = os.path.join(current_dir, script)
            
            if os.path.exists(script_path):
                results[script] = run_script(script_path)
            else:
                print_colored(f"\n✗ Script no encontrado: {script_path}", Colors.FAIL)
                results[script] = False
        
        # Mostrar resumen de resultados
        print_colored("\n" + "="*80, Colors.HEADER)
        print_colored("RESUMEN DE RESULTADOS", Colors.HEADER)
        print_colored("="*80, Colors.HEADER)
        
        successful_tests = 0
        total_tests = len(test_scripts)
        
        for script, success in results.items():
            if success:
                print_colored(f"✓ {script}: EXITOSO", Colors.OKGREEN)
                successful_tests += 1
            else:
                print_colored(f"✗ {script}: FALLÓ", Colors.FAIL)
        
        print_colored(f"\nResultado final: {successful_tests}/{total_tests} pruebas exitosas", Colors.BOLD)
        
        if successful_tests == total_tests:
            print_colored("\n🎉 ¡TODAS LAS PRUEBAS PASARON CORRECTAMENTE!", Colors.OKGREEN)
        else:
            print_colored(f"\n⚠️  {total_tests - successful_tests} prueba(s) fallaron", Colors.WARNING)
    
    except KeyboardInterrupt:
        print_colored("\n\n⚠️  Ejecución interrumpida por el usuario", Colors.WARNING)
    
    except Exception as e:
        print_colored(f"\n✗ Error durante la ejecución de las pruebas: {str(e)}", Colors.FAIL)
    
    finally:
        # Detener la aplicación Spring Boot
        stop_spring_boot_app(spring_boot_process)
        
        print_colored("\n" + "="*80, Colors.HEADER)
        print_colored("EJECUCIÓN DE PRUEBAS COMPLETADA", Colors.HEADER)
        print_colored("="*80, Colors.HEADER)

if __name__ == "__main__":
    main()