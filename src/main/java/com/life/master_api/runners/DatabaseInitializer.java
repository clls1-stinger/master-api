package com.life.master_api.runners;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public DatabaseInitializer(CategoryRepository categoryRepository,
                               TaskRepository taskRepository,
                               NoteRepository noteRepository,
                               HabitRepository habitRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        System.out.println("¡Inicializando la base de datos con MUCHAS relaciones entre entidades!");

        // ==================== CATEGORÍAS ====================
        System.out.println("\n--- Creando Categorías ---");
        List<Category> categories = new ArrayList<>();

        // Categorías principales
        String[] categoryNames = {
                "Trabajo", "Personal", "Salud", "Finanzas", "Educación",
                "Proyectos", "Familia", "Viajes", "Tecnología", "Hobbies"
        };

        for (int i = 0; i < categoryNames.length; i++) {
            Category category = new Category();
            category.setName(categoryNames[i]);
            category.setDescription("Categoría para gestionar todo lo relacionado con " + categoryNames[i].toLowerCase());
            category.setCreation(new Date());
            categories.add(categoryRepository.save(category));
            System.out.println("Categoría creada: " + category.getName());
        }

        // Subcategorías
        Map<String, String[]> subcategories = new HashMap<>();
        subcategories.put("Trabajo", new String[]{"Reuniones", "Proyectos", "Administrativo"});
        subcategories.put("Personal", new String[]{"Ejercicio", "Meditación", "Lectura"});
        subcategories.put("Salud", new String[]{"Nutrición", "Ejercicio", "Médico"});
        subcategories.put("Educación", new String[]{"Cursos", "Libros", "Tutoriales"});

        List<Category> subCats = new ArrayList<>();
        for (Category mainCat : categories) {
            if (subcategories.containsKey(mainCat.getName())) {
                for (String subName : subcategories.get(mainCat.getName())) {
                    Category subCategory = new Category();
                    subCategory.setName(subName + " (" + mainCat.getName() + ")");
                    subCategory.setDescription("Subcategoría de " + mainCat.getName());
                    subCategory.setCreation(new Date());
                    subCats.add(categoryRepository.save(subCategory));
                    System.out.println("Subcategoría creada: " + subCategory.getName());
                }
            }
        }

        // Añadir todas las categorías a la lista
        categories.addAll(subCats);

        // ==================== TAREAS ====================
        System.out.println("\n--- Creando Tareas ---");
        List<Task> tasks = new ArrayList<>();

        // Crear 20 tareas con diferentes categorías
        String[] taskTitles = {
                "Completar informe trimestral", "Llamar al cliente principal",
                "Preparar presentación", "Revisar contrato", "Actualizar sitio web",
                "Comprar víveres", "Pagar facturas", "Reservar cita médica",
                "Estudiar para certificación", "Leer libro pendiente",
                "Planificar vacaciones", "Organizar fotos", "Limpiar garage",
                "Actualizar CV", "Investigar nuevo software", "Hacer ejercicio",
                "Meditar 15 minutos", "Llamar a mamá", "Revisar inversiones",
                "Planificar menú semanal"
        };

        Random random = new Random();

        for (int i = 0; i < taskTitles.length; i++) {
            Task task = new Task();
            task.setTitle(taskTitles[i]);
            task.setDescription("Descripción detallada para la tarea: " + taskTitles[i]);
            task.setCreation(new Date());

            // Asignar entre 1 y 3 categorías aleatorias a cada tarea
            int numCategories = random.nextInt(3) + 1;
            Set<Integer> catIndices = new HashSet<>();
            while (catIndices.size() < numCategories) {
                catIndices.add(random.nextInt(categories.size()));
            }

            for (Integer index : catIndices) {
                task.getCategories().add(categories.get(index));
            }

            tasks.add(taskRepository.save(task));
            System.out.println("Tarea creada: " + task.getTitle() + " con " + task.getCategories().size() + " categorías");
        }

        // ==================== NOTAS ====================
        System.out.println("\n--- Creando Notas ---");
        List<Note> notes = new ArrayList<>();

        String[] noteTitles = {
                "Ideas para el proyecto X", "Notas de la reunión con el cliente",
                "Lluvia de ideas marketing", "Requisitos del nuevo sistema",
                "Retroalimentación del equipo", "Lista de compras",
                "Recetas saludables", "Síntomas para consulta médica",
                "Recursos para estudiar", "Citas del libro",
                "Itinerario de viaje", "Películas recomendadas",
                "Tareas pendientes casa", "Logros profesionales",
                "Configuración del servidor", "Rutina de ejercicios",
                "Técnicas de meditación", "Cumpleaños familiares",
                "Estrategia de inversión", "Plan de comidas"
        };

        for (int i = 0; i < noteTitles.length; i++) {
            Note note = new Note();
            note.setTitle(noteTitles[i]);
            note.setNote("Contenido detallado para la nota: " + noteTitles[i] + "\n" +
                    "Esta nota contiene información importante relacionada con el tema.");
            note.setCreation(new Date());

            // Asignar entre 1 y 3 categorías aleatorias
            int numCategories = random.nextInt(3) + 1;
            Set<Integer> catIndices = new HashSet<>();
            while (catIndices.size() < numCategories) {
                catIndices.add(random.nextInt(categories.size()));
            }

            for (Integer index : catIndices) {
                note.getCategories().add(categories.get(index));
            }

            // Asignar entre 0 y 2 tareas aleatorias
            int numTasks = random.nextInt(3);
            Set<Integer> taskIndices = new HashSet<>();
            while (taskIndices.size() < numTasks && taskIndices.size() < tasks.size()) {
                taskIndices.add(random.nextInt(tasks.size()));
            }

            for (Integer index : taskIndices) {
                note.getTasks().add(tasks.get(index));
            }

            notes.add(noteRepository.save(note));
            System.out.println("Nota creada: " + note.getTitle() +
                    " con " + note.getCategories().size() + " categorías y " +
                    note.getTasks().size() + " tareas");
        }

        // ==================== HÁBITOS ====================
        System.out.println("\n--- Creando Hábitos ---");
        List<Habit> habits = new ArrayList<>();

        String[] habitNames = {
                "Ejercicio diario", "Meditación matutina", "Leer 30 minutos",
                "Beber 2L de agua", "Escribir en diario", "Practicar idioma",
                "Revisar finanzas", "Planificar día siguiente", "Caminar 10,000 pasos",
                "Estiramientos", "Tomar vitaminas", "Contactar un amigo",
                "Practicar instrumento", "Aprender algo nuevo", "Cocinar en casa"
        };

        for (int i = 0; i < habitNames.length; i++) {
            Habit habit = new Habit();
            habit.setName(habitNames[i]);
            habit.setCreation(new Date());

            // Asignar entre 1 y 3 categorías aleatorias
            int numCategories = random.nextInt(3) + 1;
            Set<Integer> catIndices = new HashSet<>();
            while (catIndices.size() < numCategories) {
                catIndices.add(random.nextInt(categories.size()));
            }

            for (Integer index : catIndices) {
                habit.getCategories().add(categories.get(index));
            }

            // Asignar entre 0 y 3 notas aleatorias
            int numNotes = random.nextInt(4);
            Set<Integer> noteIndices = new HashSet<>();
            while (noteIndices.size() < numNotes && noteIndices.size() < notes.size()) {
                noteIndices.add(random.nextInt(notes.size()));
            }

            for (Integer index : noteIndices) {
                habit.getNotes().add(notes.get(index));
            }

            // Asignar entre 0 y 2 tareas aleatorias
            int numTasks = random.nextInt(3);
            Set<Integer> taskIndices = new HashSet<>();
            while (taskIndices.size() < numTasks && taskIndices.size() < tasks.size()) {
                taskIndices.add(random.nextInt(tasks.size()));
            }

            for (Integer index : taskIndices) {
                habit.getTasks().add(tasks.get(index));
            }

            habits.add(habitRepository.save(habit));
            System.out.println("Hábito creado: " + habit.getName() +
                    " con " + habit.getCategories().size() + " categorías, " +
                    habit.getNotes().size() + " notas y " +
                    habit.getTasks().size() + " tareas");
        }

        // ==================== RELACIONES ADICIONALES ====================
        System.out.println("\n--- Creando relaciones adicionales ---");

        // Crear relaciones circulares para probar la robustez
        // Conectar el último hábito con la primera nota
        if (!habits.isEmpty() && !notes.isEmpty()) {
            Habit lastHabit = habits.get(habits.size() - 1);
            Note firstNote = notes.get(0);
            lastHabit.getNotes().add(firstNote);
            habitRepository.save(lastHabit);
            System.out.println("Relación circular creada: Hábito '" + lastHabit.getName() +
                    "' ahora está relacionado con la nota '" + firstNote.getTitle() + "'");
        }

        // Conectar la última tarea con todas las categorías
        if (!tasks.isEmpty()) {
            Task lastTask = tasks.get(tasks.size() - 1);
            lastTask.getCategories().addAll(categories);
            taskRepository.save(lastTask);
            System.out.println("Tarea '" + lastTask.getTitle() + "' ahora está relacionada con TODAS las categorías");
        }

        // Conectar la primera categoría con todas las tareas
        if (!categories.isEmpty()) {
            Category firstCategory = categories.get(0);
            for (Task task : tasks) {
                task.getCategories().add(firstCategory);
                taskRepository.save(task);
            }
            System.out.println("Categoría '" + firstCategory.getName() + "' ahora está relacionada con TODAS las tareas");
        }

        // Estadísticas finales
        System.out.println("\n=== ESTADÍSTICAS DE RELACIONES ===");
        System.out.println("Total de categorías: " + categories.size());
        System.out.println("Total de tareas: " + tasks.size());
        System.out.println("Total de notas: " + notes.size());
        System.out.println("Total de hábitos: " + habits.size());

        // Contar relaciones totales
        int totalRelations = 0;
        for (Task task : tasks) {
            totalRelations += task.getCategories().size();
        }
        for (Note note : notes) {
            totalRelations += note.getCategories().size();
            totalRelations += note.getTasks().size();
        }
        for (Habit habit : habits) {
            totalRelations += habit.getCategories().size();
            totalRelations += habit.getNotes().size();
            totalRelations += habit.getTasks().size();
        }

        System.out.println("Total de relaciones creadas: " + totalRelations);
        System.out.println("\n¡Base de datos inicializada con una gran cantidad de relaciones entre entidades!");
    }
}