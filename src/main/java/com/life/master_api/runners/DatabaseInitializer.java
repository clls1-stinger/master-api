package com.life.master_api.runners;

import com.life.master_api.entities.Category;
import com.life.master_api.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component // <-- ¡IMPORTANTE!  Marca la clase como un COMPONENTE DE SPRING
public class DatabaseInitializer implements CommandLineRunner { // <-- ¡IMPLEMENTA CommandLineRunner!

    private final CategoryRepository categoryRepository; // <-- Inyecta el Repositorio de Categorías

    // CONSTRUCTOR PARA INYECCIÓN DE DEPENDENCIAS (para que Spring Boot inyecte el CategoryRepository)
    public DatabaseInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception { // <-- ¡MÉTODO run() SE EJECUTA AL INICIAR LA APP!
        System.out.println("¡Inicializando la base de datos con datos de prueba!"); // Mensaje en consola

        // 1. CREAR UNA CATEGORÍA DE PRUEBA
        Category category = new Category(); // Crea una NUEVA ENTIDAD Category (objeto Java)
        category.setName("Libres"); // Establece el nombre de la categoría
        category.setDescription("Categorías de libros"); // Establece la descripción
        category.setCreation(new Date()); // Establece la fecha de creación (fecha actual)

        Category savedCategory = categoryRepository.save(category); // ¡USA EL REPOSITORIO PARA GUARDAR LA CATEGORÍA EN LA BASE DE DATOS!
        System.out.println("Categoría creada con ID: " + savedCategory.getId()); // Imprime el ID de la categoría creada

        // 2. LEER LA CATEGORÍA DE PRUEBA DESDE LA BASE DE DATOS
        Category retrievedCategory = categoryRepository.findById(savedCategory.getId()).orElse(null); // ¡USA EL REPOSITORIO PARA BUSCAR LA CATEGORÍA POR ID!
        if (retrievedCategory != null) {
            System.out.println("Categoría recuperada de la base de datos: " + retrievedCategory); // Imprime la categoría recuperada
        } else {
            System.out.println("¡No se encontró la categoría con ID: " + savedCategory.getId() + "!"); // Mensaje de error si no se encuentra
        }

        System.out.println("¡Base de datos inicializada!"); // Mensaje final
    }
}