package com.life.master_api.runners;

import com.life.master_api.entities.*;
import com.life.master_api.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
public class HistoryDatabaseInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CategoryHistoryRepository categoryHistoryRepository;
    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final NoteRepository noteRepository;
    private final NoteHistoryRepository noteHistoryRepository;
    private final HabitRepository habitRepository;
    private final HabitHistoryRepository habitHistoryRepository;

    public HistoryDatabaseInitializer(CategoryRepository categoryRepository,
                                        CategoryHistoryRepository categoryHistoryRepository,
                                        TaskRepository taskRepository,
                                        TaskHistoryRepository taskHistoryRepository,
                                        NoteRepository noteRepository,
                                        NoteHistoryRepository noteHistoryRepository,
                                        HabitRepository habitRepository,
                                        HabitHistoryRepository habitHistoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryHistoryRepository = categoryHistoryRepository;
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.noteRepository = noteRepository;
        this.noteHistoryRepository = noteHistoryRepository;
        this.habitRepository = habitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // Deshabilitado: No inicializar historiales automáticamente
        System.out.println("HistoryDatabaseInitializer deshabilitado. Los historiales se crearán por usuario.");
        return;
        
        /*
        System.out.println("Initializing History Tables...");

        initializeCategoryHistory();
        initializeTaskHistory();
        initializeNoteHistory();
        initializeHabitHistory();

        System.out.println("History Tables Initialized.");
        */
    }

    private void initializeCategoryHistory() {
        List<Category> categories = categoryRepository.findAll();
        System.out.println("Creating history for " + categories.size() + " Categories...");
        for (Category category : categories) {
            saveCategoryHistory(category);
        }
        System.out.println("Category History Initialized.");
    }

    private void initializeTaskHistory() {
        List<Task> tasks = taskRepository.findAll();
        System.out.println("Creating history for " + tasks.size() + " Tasks...");
        for (Task task : tasks) {
            saveTaskHistory(task);
        }
        System.out.println("Task History Initialized.");
    }

    private void initializeNoteHistory() {
        List<Note> notes = noteRepository.findAll();
        System.out.println("Creating history for " + notes.size() + " Notes...");
        for (Note note : notes) {
            saveNoteHistory(note);
        }
        System.out.println("Note History Initialized.");
    }

    private void initializeHabitHistory() {
        List<Habit> habits = habitRepository.findAll();
        System.out.println("Creating history for " + habits.size() + " Habits...");
        for (Habit habit : habits) {
            saveHabitHistory(habit);
        }
        System.out.println("Habit History Initialized.");
    }

    private void saveCategoryHistory(Category category) {
        CategoryHistory history = new CategoryHistory();
        history.setCategory(category);
        history.setName(category.getName());
        history.setDescription(category.getDescription());
        history.setCreation(category.getCreation());
        history.setTimestamp(new Date()); // Current timestamp

        categoryHistoryRepository.save(history);
    }

    private void saveTaskHistory(Task task) {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setTitle(task.getTitle());
        history.setDescription(task.getDescription());
        history.setCreation(task.getCreation());
        history.setTimestamp(new Date());

        taskHistoryRepository.save(history);
    }

    private void saveNoteHistory(Note note) {
        NoteHistory history = new NoteHistory();
        history.setNote(note);
        history.setTitle(note.getTitle());
        history.setNoteContent(note.getNote()); // Use setNoteContent here
        history.setCreation(note.getCreation());
        history.setTimestamp(new Date());

        noteHistoryRepository.save(history);
    }

    private void saveHabitHistory(Habit habit) {
        HabitHistory history = new HabitHistory();
        history.setHabit(habit);
        history.setName(habit.getName());
        history.setCreation(habit.getCreation());
        history.setTimestamp(new Date());

        habitHistoryRepository.save(history);
    }
}