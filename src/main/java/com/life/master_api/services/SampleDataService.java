package com.life.master_api.services;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.User;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SampleDataService {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public SampleDataService(CategoryRepository categoryRepository,
                           TaskRepository taskRepository,
                           NoteRepository noteRepository,
                           HabitRepository habitRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
    }

    @Transactional
    public void createSampleDataForUser(User user) {
        System.out.println("Creando datos de ejemplo para el usuario: " + user.getUsername());
        
        // Crear categorías de ejemplo
        List<Category> categories = createSampleCategories(user);
        
        // Crear tareas de ejemplo
        List<Task> tasks = createSampleTasks(user, categories);
        
        // Crear notas de ejemplo
        List<Note> notes = createSampleNotes(user, categories, tasks);
        
        // Crear hábitos de ejemplo
        createSampleHabits(user, categories, notes, tasks);
        
        System.out.println("Datos de ejemplo creados exitosamente para: " + user.getUsername());
    }

    private List<Category> createSampleCategories(User user) {
        List<Category> categories = new ArrayList<>();
        
        String[] categoryNames = {
            "Personal", "Trabajo", "Salud", "Educación", "Hobbies"
        };
        
        String[] descriptions = {
            "Tareas y actividades personales",
            "Proyectos y responsabilidades laborales",
            "Cuidado de la salud y bienestar",
            "Aprendizaje y desarrollo personal",
            "Actividades de entretenimiento y pasatiempos"
        };
        
        for (int i = 0; i < categoryNames.length; i++) {
            Category category = new Category();
            category.setName(categoryNames[i]);
            category.setDescription(descriptions[i]);
            category.setCreation(new Date());
            category.setUser(user);
            categories.add(categoryRepository.save(category));
        }
        
        return categories;
    }

    private List<Task> createSampleTasks(User user, List<Category> categories) {
        List<Task> tasks = new ArrayList<>();
        Random random = new Random();
        
        String[] taskTitles = {
            "Revisar correos electrónicos",
            "Hacer ejercicio",
            "Leer un libro",
            "Planificar la semana",
            "Llamar a un amigo",
            "Organizar el escritorio",
            "Estudiar nuevo tema",
            "Preparar comida saludable"
        };
        
        for (String title : taskTitles) {
            Task task = new Task();
            task.setTitle(title);
            task.setDescription("Descripción para: " + title);
            task.setCreation(new Date());
            task.setUser(user);
            
            // Asignar 1-2 categorías aleatorias
            int numCategories = random.nextInt(2) + 1;
            Set<Integer> catIndices = new HashSet<>();
            while (catIndices.size() < numCategories) {
                catIndices.add(random.nextInt(categories.size()));
            }
            
            for (Integer index : catIndices) {
                task.getCategories().add(categories.get(index));
            }
            
            tasks.add(taskRepository.save(task));
        }
        
        return tasks;
    }

    private List<Note> createSampleNotes(User user, List<Category> categories, List<Task> tasks) {
        List<Note> notes = new ArrayList<>();
        Random random = new Random();
        
        String[] noteTitles = {
            "Ideas para proyectos",
            "Lista de compras",
            "Notas de reunión",
            "Recordatorios importantes",
            "Objetivos del mes"
        };
        
        for (String title : noteTitles) {
            Note note = new Note();
            note.setTitle(title);
            note.setNote("Contenido de la nota: " + title + "\n\nEsta es una nota de ejemplo que puedes editar o eliminar.");
            note.setCreation(new Date());
            note.setUser(user);
            
            // Asignar 1-2 categorías aleatorias
            int numCategories = random.nextInt(2) + 1;
            Set<Integer> catIndices = new HashSet<>();
            while (catIndices.size() < numCategories) {
                catIndices.add(random.nextInt(categories.size()));
            }
            
            for (Integer index : catIndices) {
                note.getCategories().add(categories.get(index));
            }
            
            // Asignar 0-1 tareas aleatorias
            if (random.nextBoolean() && !tasks.isEmpty()) {
                int taskIndex = random.nextInt(tasks.size());
                note.getTasks().add(tasks.get(taskIndex));
            }
            
            notes.add(noteRepository.save(note));
        }
        
        return notes;
    }

    private void createSampleHabits(User user, List<Category> categories, List<Note> notes, List<Task> tasks) {
        Random random = new Random();
        
        String[] habitNames = {
            "Ejercicio diario",
            "Leer 30 minutos",
            "Meditar",
            "Beber agua",
            "Escribir en diario"
        };
        
        for (String name : habitNames) {
            Habit habit = new Habit();
            habit.setName(name);
            habit.setCreation(new Date());
            habit.setUser(user);
            
            // Asignar 1-2 categorías aleatorias
            int numCategories = random.nextInt(2) + 1;
            Set<Integer> catIndices = new HashSet<>();
            while (catIndices.size() < numCategories) {
                catIndices.add(random.nextInt(categories.size()));
            }
            
            for (Integer index : catIndices) {
                habit.getCategories().add(categories.get(index));
            }
            
            // Asignar 0-1 notas aleatorias
            if (random.nextBoolean() && !notes.isEmpty()) {
                int noteIndex = random.nextInt(notes.size());
                habit.getNotes().add(notes.get(noteIndex));
            }
            
            // Asignar 0-1 tareas aleatorias
            if (random.nextBoolean() && !tasks.isEmpty()) {
                int taskIndex = random.nextInt(tasks.size());
                habit.getTasks().add(tasks.get(taskIndex));
            }
            
            habitRepository.save(habit);
        }
    }
}