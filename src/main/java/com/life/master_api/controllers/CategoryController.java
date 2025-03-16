package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.repositories.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // <- ANOTACIÓN CLAVE: ES UN CONTROLADOR REST
@RequestMapping("/categories") // <- URL BASE PARA ESTE CONTROLADOR
public class CategoryController {

    private final CategoryRepository categoryRepository; // <- REPOSITORIO

    public CategoryController(CategoryRepository categoryRepository) { // <- INYECCIÓN DE DEPENDENCIAS
        this.categoryRepository = categoryRepository;
    }

    @GetMapping // <- ENDPOINT GET PARA /categories (LISTAR TODAS)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/search/by-name") // <- ¡NUEVO ENDPOINT GET PARA /categories/search/by-name!
    public List<Category> getCategoriesByName(@RequestParam String name) { // <- ¡RECIBE "name" COMO PARÁMETRO DE QUERY!
        System.out.println("Buscando categorías por nombre: " + name); // LOG PARA VER LA BÚSQUEDA EN CONSOLA
        return categoryRepository.findByNameContains(name); // <- ¡USA EL REPOSITORIO PARA BUSCAR POR NOMBRE (CONTENIENDO)!
    }
}