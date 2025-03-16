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

import java.util.Date;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public DatabaseInitializer(CategoryRepository categoryRepository, TaskRepository taskRepository, NoteRepository noteRepository, HabitRepository habitRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("¡Inicializando la base de datos con datos de prueba para todas las entidades!");

        // 1. CATEGORÍAS
        System.out.println("\n--- Categorías ---");
        Category category1 = new Category();
        category1.setName("Trabajo");
        category1.setDescription("Categorías relacionadas con el trabajo y proyectos profesionales.");
        category1.setCreation(new Date());
        Category savedCategory1 = categoryRepository.save(category1);
        System.out.println("Categoría creada: " + savedCategory1);

        Category category2 = new Category();
        category2.setName("Personal");
        category2.setDescription("Categorías para asuntos personales y hobbies.");
        category2.setCreation(new Date());
        Category savedCategory2 = categoryRepository.save(category2);
        System.out.println("Categoría creada: " + savedCategory2);

        // 2. TAREAS
        System.out.println("\n--- Tareas ---");
        Task task1 = new Task();
        task1.setTitle("Completar informe semanal");
        task1.setDescription("Redactar y enviar el informe de progreso semanal al equipo.");
        task1.setCreation(new Date());
        Task savedTask1 = taskRepository.save(task1);
        System.out.println("Tarea creada: " + savedTask1);

        Task task2 = new Task();
        task2.setTitle("Llamar al cliente");
        task2.setDescription("Contactar al cliente para discutir los requisitos del nuevo proyecto.");
        task2.setCreation(new Date());
        Task savedTask2 = taskRepository.save(task2);
        System.out.println("Tarea creada: " + savedTask2);

        // 3. NOTAS
        System.out.println("\n--- Notas ---");
        Note note1 = new Note();
        note1.setTitle("Ideas para el nuevo proyecto");
        note1.setNote("Lluvia de ideas inicial para el proyecto X. Considerar enfoque en la nube y microservicios.");
        note1.setCreation(new Date());
        Note savedNote1 = noteRepository.save(note1);
        System.out.println("Nota creada: " + savedNote1);

        Note note2 = new Note();
        note2.setTitle("Recordatorio de reunión");
        note2.setNote("Reunión con el equipo de diseño mañana a las 10:00 AM en la sala de conferencias.");
        note2.setCreation(new Date());
        Note savedNote2 = noteRepository.save(note2);
        System.out.println("Nota creada: " + savedNote2);

        // 4. HÁBITOS
        System.out.println("\n--- Hábitos ---");
        Habit habit1 = new Habit();
        habit1.setName("Ejercicio diario");
        habit1.setCreation(new Date());
        Habit savedHabit1 = habitRepository.save(habit1);
        System.out.println("Hábito creado: " + savedHabit1);

        Habit habit2 = new Habit();
        habit2.setName("Leer 30 minutos");
        habit2.setCreation(new Date());
        Habit savedHabit2 = habitRepository.save(habit2);
        System.out.println("Hábito creado: " + savedHabit2);


        System.out.println("\n¡Base de datos inicializada con datos de prueba para todas las entidades!");
    }
}