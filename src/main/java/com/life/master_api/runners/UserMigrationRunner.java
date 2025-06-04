package com.life.master_api.runners;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.User;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Order(1) // Ejecutar antes que otros CommandLineRunner
public class UserMigrationRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;
    private final PasswordEncoder passwordEncoder;

    public UserMigrationRunner(UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              TaskRepository taskRepository,
                              NoteRepository noteRepository,
                              HabitRepository habitRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Verificando usuario administrador...\n");
        
        // Verificar si ya existe el usuario admin
        if (userRepository.findByUsername("admin").isPresent()) {
            System.out.println("Usuario administrador ya existe. Omitiendo creación.\n");
            return;
        }
        
        // Crear usuario administrador por defecto solo si no existe
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@masterapi.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setCreatedAt(new Date());
        
        adminUser = userRepository.save(adminUser);
        System.out.println("Usuario administrador creado: " + adminUser.getUsername());
        System.out.println("Nota: Cada usuario tendrá sus propios datos. No se asignan datos existentes.\n");
    }
}