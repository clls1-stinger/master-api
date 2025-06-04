package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import java.util.List;

public class HabitRepositoryCustomImpl implements HabitRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Habit> findByCategories(Category category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Habit> query = cb.createQuery(Habit.class);
        Root<Habit> habit = query.from(Habit.class);
        
        // Join con la tabla de categorías
        Join<Object, Object> categoryJoin = habit.join("categories", JoinType.INNER);
        
        // Condición: la categoría es igual al parámetro
        query.where(cb.equal(categoryJoin, category));
        
        return entityManager.createQuery(query).getResultList();
    }
}