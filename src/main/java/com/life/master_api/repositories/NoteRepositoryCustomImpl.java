package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class NoteRepositoryCustomImpl implements NoteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Note> findByCategory(Category category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> query = cb.createQuery(Note.class);
        Root<Note> note = query.from(Note.class);
        
        // Join con la tabla de categorías
        Join<Object, Object> categoryJoin = note.join("categories", JoinType.INNER);
        
        // Condición: la categoría es igual al parámetro
        query.where(cb.equal(categoryJoin, category));
        
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Page<Note> findByCategoryAndUser(Category category, User user, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Consulta para obtener los resultados paginados
        CriteriaQuery<Note> query = cb.createQuery(Note.class);
        Root<Note> note = query.from(Note.class);
        
        // Join con la tabla de categorías
        Join<Object, Object> categoryJoin = note.join("categories", JoinType.INNER);
        
        // Condiciones: la categoría es igual al parámetro Y el usuario es igual al parámetro
        Predicate categoryPredicate = cb.equal(categoryJoin, category);
        Predicate userPredicate = cb.equal(note.get("user"), user);
        query.where(cb.and(categoryPredicate, userPredicate));
        
        // Aplicar ordenamiento si está presente en el Pageable
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(sort -> {
                if (sort.isAscending()) {
                    orders.add(cb.asc(note.get(sort.getProperty())));
                } else {
                    orders.add(cb.desc(note.get(sort.getProperty())));
                }
            });
            query.orderBy(orders);
        }
        
        // Ejecutar la consulta con paginación
        List<Note> notes = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        
        // Consulta para contar el total de resultados
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Note> noteCount = countQuery.from(Note.class);
        Join<Object, Object> categoryJoinCount = noteCount.join("categories", JoinType.INNER);
        
        countQuery.select(cb.count(noteCount));
        Predicate categoryPredicateCount = cb.equal(categoryJoinCount, category);
        Predicate userPredicateCount = cb.equal(noteCount.get("user"), user);
        countQuery.where(cb.and(categoryPredicateCount, userPredicateCount));
        
        Long total = entityManager.createQuery(countQuery).getSingleResult();
        
        return new PageImpl<>(notes, pageable, total);
    }
}