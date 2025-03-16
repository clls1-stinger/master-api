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
import java.util.HashSet;
import java.util.Set;

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
        System.out.println("¡Inicializando la base de datos con datos de prueba para todas las entidades y relaciones!");

        // --- Crear Categorías ---
        System.out.println("\n--- Categorías ---");
        Category category1 = new Category();
        category1.setName("Trabajo");
        category1.setDescription("Categorías relacionadas con el trabajo.");
        category1.setCreation(new Date());
        Category savedCategory1 = categoryRepository.save(category1);
        System.out.println("Categoría creada: " + savedCategory1);

        Category category2 = new Category();
        category2.setName("Personal");
        category2.setDescription("Categorías personales.");
        category2.setCreation(new Date());
        Category savedCategory2 = categoryRepository.save(category2);
        System.out.println("Categoría creada: " + savedCategory2);

        // --- Crear Tareas ---
        System.out.println("\n--- Tareas ---");
        Task task1 = new Task();
        task1.setTitle("Tarea 1");
        task1.setDescription("Descripción Tarea 1");
        task1.setCreation(new Date());
        Task savedTask1 = taskRepository.save(task1);
        System.out.println("Tarea creada: " + savedTask1);

        Task task2 = new Task();
        task2.setTitle("Tarea 2");
        task2.setDescription("Descripción Tarea 2");
        task2.setCreation(new Date());
        Task savedTask2 = taskRepository.save(task2);
        System.out.println("Tarea creada: " + savedTask2);

        // --- Crear Notas ---
        System.out.println("\n--- Notas ---");
        Note note1 = new Note();
        note1.setTitle("Nota 1");
        note1.setNote("Contenido Nota 1");
        note1.setCreation(new Date());
        Note savedNote1 = noteRepository.save(note1);
        System.out.println("Nota creada: " + savedNote1);

        Note note2 = new Note();
        note2.setTitle("Nota 2");
        note2.setNote("Contenido Nota 2");
        note2.setCreation(new Date());
        Note savedNote2 = noteRepository.save(note2);
        System.out.println("Nota creada: " + savedNote2);

        // --- Crear Hábitos ---
        System.out.println("\n--- Hábitos ---");
        Habit habit1 = new Habit();
        habit1.setName("Hábito 1");
        habit1.setCreation(new Date());
        Habit savedHabit1 = habitRepository.save(habit1);
        System.out.println("Hábito creado: " + savedHabit1);

        Habit habit2 = new Habit();
        habit2.setName("Hábito 2");
        habit2.setCreation(new Date());
        Habit savedHabit2 = habitRepository.save(habit2);
        System.out.println("Hábito creado: " + savedHabit2);

        // --- Establecer Relaciones ---
        System.out.println("\n--- Establecer Relaciones ---");

        // Categoria 1 relaciona con Task 1 y Note 1
        Category retrievedCategory1 = categoryRepository.findById(savedCategory1.getId()).orElse(null);
        Task retrievedTask1 = taskRepository.findById(savedTask1.getId()).orElse(null);
        Note retrievedNote1 = noteRepository.findById(savedNote1.getId()).orElse(null);

        if (retrievedCategory1 != null && retrievedTask1 != null && retrievedNote1 != null) {
            Set<Task> tasksCategory1 = new HashSet<>();
            tasksCategory1.add(retrievedTask1);
            retrievedCategory1.setTasks(tasksCategory1);

            Set<Note> notesCategory1 = new HashSet<>();
            notesCategory1.add(retrievedNote1);
            retrievedCategory1.setNotes(notesCategory1);

            categoryRepository.save(retrievedCategory1);
            System.out.println("Categoría 1 relacionada con Tarea 1 y Nota 1");
        }

        // Hábito 1 relaciona con Categoria 2 y Nota 2
        Habit retrievedHabit1 = habitRepository.findById(savedHabit1.getId()).orElse(null);
        Category retrievedCategory2 = categoryRepository.findById(savedCategory2.getId()).orElse(null);
        Note retrievedNote2 = noteRepository.findById(savedNote2.getId()).orElse(null);

        if (retrievedHabit1 != null && retrievedCategory2 != null && retrievedNote2 != null) {
            Set<Category> categoriesHabit1 = new HashSet<>();
            categoriesHabit1.add(retrievedCategory2);
            retrievedHabit1.setCategories(categoriesHabit1);

            Set<Note> notesHabit1 = new HashSet<>();
            notesHabit1.add(retrievedNote2);
            retrievedHabit1.setNotes(notesHabit1);

            habitRepository.save(retrievedHabit1);
            System.out.println("Hábito 1 relacionado con Categoría 2 y Nota 2");
        }

        // Nota 1 relaciona con Hábito 2 y Task 2
        Note retrievedNote_1 = noteRepository.findById(savedNote1.getId()).orElse(null); // Usamos _1 para no confundir con retrievedNote1 anterior
        Habit retrievedHabit2 = habitRepository.findById(savedHabit2.getId()).orElse(null);
        Task retrievedTask2 = taskRepository.findById(savedTask2.getId()).orElse(null);

        if (retrievedNote_1 != null && retrievedHabit2 != null && retrievedTask2 != null) {
            Set<Habit> habitsNote1 = new HashSet<>();
            habitsNote1.add(retrievedHabit2);
            retrievedNote_1.setHabits(habitsNote1);

            Set<Task> tasksNote1 = new HashSet<>();
            tasksNote1.add(retrievedTask2);
            retrievedNote_1.setTasks(tasksNote1);

            noteRepository.save(retrievedNote_1);
            System.out.println("Nota 1 relacionada con Hábito 2 y Tarea 2");
        }


        System.out.println("\n¡Base de datos inicializada con datos de prueba y relaciones!");
    }
}