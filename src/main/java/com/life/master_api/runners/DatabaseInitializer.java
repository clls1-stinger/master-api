package com.life.master_api.runners;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
<<<<<<< Updated upstream
=======
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.relationships.*;
>>>>>>> Stashed changes
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.relationships.*;
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
    private final TaskCategoryRelationshipRepository taskCategoryRelationshipRepository;
    private final HabitCategoryRelationshipRepository habitCategoryRelationshipRepository;
    private final NoteTaskRelationshipRepository noteTaskRelationshipRepository;
    private final HabitNoteRelationshipRepository habitNoteRelationshipRepository;
    private final NoteCategoryRelationshipRepository noteCategoryRelationshipRepository;
    private final HabitTaskRelationshipRepository habitTaskRelationshipRepository;


    public DatabaseInitializer(CategoryRepository categoryRepository, TaskRepository taskRepository, NoteRepository noteRepository, HabitRepository habitRepository, TaskCategoryRelationshipRepository taskCategoryRelationshipRepository, HabitCategoryRelationshipRepository habitCategoryRelationshipRepository, NoteTaskRelationshipRepository noteTaskRelationshipRepository, HabitNoteRelationshipRepository habitNoteRelationshipRepository, NoteCategoryRelationshipRepository noteCategoryRelationshipRepository, HabitTaskRelationshipRepository habitTaskRelationshipRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
        this.taskCategoryRelationshipRepository = taskCategoryRelationshipRepository;
        this.habitCategoryRelationshipRepository = habitCategoryRelationshipRepository;
        this.noteTaskRelationshipRepository = noteTaskRelationshipRepository;
        this.habitNoteRelationshipRepository = habitNoteRelationshipRepository;
        this.noteCategoryRelationshipRepository = noteCategoryRelationshipRepository;
        this.habitTaskRelationshipRepository = habitTaskRelationshipRepository;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
<<<<<<< Updated upstream
        System.out.println("¡Inicializando la base de datos con datos de prueba para Habit-Category!");
=======
        System.out.println("¡Inicializando la base de datos con datos de prueba para todas las entidades y relaciones!");
>>>>>>> Stashed changes

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

        // 5. RELACIONES TaskCategory
        System.out.println("\n--- Relaciones TaskCategory ---");
        TaskCategoryRelationship taskCategoryRel1 = new TaskCategoryRelationship(savedTask1, savedCategory1);
        TaskCategoryRelationship savedTaskCategoryRel1 = taskCategoryRelationshipRepository.save(taskCategoryRel1);
        System.out.println("Relación TaskCategory creada: " + savedTaskCategoryRel1);

<<<<<<< Updated upstream
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
=======
        TaskCategoryRelationship taskCategoryRel2 = new TaskCategoryRelationship(savedTask2, savedCategory2);
        TaskCategoryRelationship savedTaskCategoryRel2 = taskCategoryRelationshipRepository.save(taskCategoryRel2);
        System.out.println("Relación TaskCategory creada: " + savedTaskCategoryRel2);

        // 6. RELACIONES HabitCategory
        System.out.println("\n--- Relaciones HabitCategory ---");
        HabitCategoryRelationship habitCategoryRel1 = new HabitCategoryRelationship(savedHabit1, savedCategory2);
        HabitCategoryRelationship savedHabitCategoryRel1 = habitCategoryRelationshipRepository.save(habitCategoryRel1);
        System.out.println("Relación HabitCategory creada: " + savedHabitCategoryRel1);

        HabitCategoryRelationship habitCategoryRel2 = new HabitCategoryRelationship(savedHabit2, savedCategory1);
        HabitCategoryRelationship savedHabitCategoryRel2 = habitCategoryRelationshipRepository.save(habitCategoryRel2);
        System.out.println("Relación HabitCategory creada: " + savedHabitCategoryRel2);

        // 7. RELACIONES NoteTask
        System.out.println("\n--- Relaciones NoteTask ---");
        NoteTaskRelationship noteTaskRel1 = new NoteTaskRelationship(savedNote1, savedTask1);
        NoteTaskRelationship savedNoteTaskRel1 = noteTaskRelationshipRepository.save(noteTaskRel1);
        System.out.println("Relación NoteTask creada: " + savedNoteTaskRel1);

        NoteTaskRelationship noteTaskRel2 = new NoteTaskRelationship(savedNote2, savedTask2);
        NoteTaskRelationship savedNoteTaskRel2 = noteTaskRelationshipRepository.save(noteTaskRel2);
        System.out.println("Relación NoteTask creada: " + savedNoteTaskRel2);

        // 8. RELACIONES HabitNote
        System.out.println("\n--- Relaciones HabitNote ---");
        HabitNoteRelationship habitNoteRel1 = new HabitNoteRelationship(savedHabit1, savedNote1);
        HabitNoteRelationship savedHabitNoteRel1 = habitNoteRelationshipRepository.save(habitNoteRel1);
        System.out.println("Relación HabitNote creada: " + savedHabitNoteRel1);

        HabitNoteRelationship habitNoteRel2 = new HabitNoteRelationship(savedHabit2, savedNote2);
        HabitNoteRelationship savedHabitNoteRel2 = habitNoteRelationshipRepository.save(habitNoteRel2);
        System.out.println("Relación HabitNote creada: " + savedHabitNoteRel2);

        // 9. RELACIONES NoteCategory
        System.out.println("\n--- Relaciones NoteCategory ---");
        NoteCategoryRelationship noteCategoryRel1 = new NoteCategoryRelationship(savedNote1, savedCategory1);
        NoteCategoryRelationship savedNoteCategoryRel1 = noteCategoryRelationshipRepository.save(noteCategoryRel1);
        System.out.println("Relación NoteCategory creada: " + savedNoteCategoryRel1);

        NoteCategoryRelationship noteCategoryRel2 = new NoteCategoryRelationship(savedNote2, savedCategory2);
        NoteCategoryRelationship savedNoteCategoryRel2 = noteCategoryRelationshipRepository.save(noteCategoryRel2);
        System.out.println("Relación NoteCategory creada: " + savedNoteCategoryRel2);

        // 10. RELACIONES HabitTask
        System.out.println("\n--- Relaciones HabitTask ---");
        HabitTaskRelationship habitTaskRel1 = new HabitTaskRelationship(savedHabit1, savedTask2);
        HabitTaskRelationship savedHabitTaskRel1 = habitTaskRelationshipRepository.save(habitTaskRel1);
        System.out.println("Relación HabitTask creada: " + savedHabitTaskRel1);

        HabitTaskRelationship habitTaskRel2 = new HabitTaskRelationship(savedHabit2, savedTask1);
        HabitTaskRelationship savedHabitTaskRel2 = habitTaskRelationshipRepository.save(habitTaskRel2);
        System.out.println("Relación HabitTask creada: " + savedHabitTaskRel2);


        System.out.println("\n¡Base de datos inicializada con datos de prueba para todas las entidades y relaciones!");
>>>>>>> Stashed changes
    }
}