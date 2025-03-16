package com.life.master_api.runners;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        System.out.println("¡Inicializando la base de datos con datos de prueba para Habit-Category!");

        // --- Crear Categorías ---
        System.out.println("\n--- Categorías ---");
        Category category2 = new Category();
        category2.setName("Personal");
        category2.setDescription("Categorías personales.");
        category2.setCreation(new Date());


        // --- Crear Hábitos ---
        System.out.println("\n--- Hábitos ---");
        Habit habit1 = new Habit();
        habit1.setName("Hábito 1");
        habit1.setCreation(new Date());


        // --- Establecer Relaciones ---
        System.out.println("\n--- Establecer Relaciones ---");

        // Hábito 1 relaciona con Categoria 2
        Habit retrievedHabit1 = habit1;
        Category retrievedCategory2 = category2;

        retrievedHabit1.getCategories().add(retrievedCategory2);
        retrievedCategory2.getHabits().add(retrievedHabit1);

        // --- Guardar entidades (en el orden correcto) ---
        categoryRepository.save(category2); // Save Category FIRST this time
        habitRepository.save(habit1);       // Then save Habit


        System.out.println("\n¡Base de datos inicializada con datos de prueba para Habit-Category!");
    }
}