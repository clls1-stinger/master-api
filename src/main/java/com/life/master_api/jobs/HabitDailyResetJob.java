package com.life.master_api.jobs;

import com.life.master_api.entities.Habit;
import com.life.master_api.repositories.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitDailyResetJob {

    private final HabitRepository habitRepository;

    /**
     * Job que se ejecuta diariamente a las 00:00 para reiniciar el seguimiento diario de hábitos
     * y actualizar las rachas de los hábitos activos.
     */
    @Scheduled(cron = "0 0 0 * * *") // Ejecutar a las 00:00 todos los días
    @Transactional
    public void resetDailyHabitTracking() {
        log.info("Iniciando reinicio diario de seguimiento de hábitos...");
        
        try {
            // Obtener todos los hábitos activos
            List<Habit> activeHabits = habitRepository.findByActiveTrue();
            
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate today = LocalDate.now();
            
            for (Habit habit : activeHabits) {
                // Actualizar rachas basándose en el seguimiento de ayer
                updateStreaks(habit, yesterday);
                
                // Reiniciar campos de seguimiento diario
                habit.setTodayCompleted(false);
                habit.setTodayQuantity(0);
                habit.setLastTrackedDate(null);
                
                habitRepository.save(habit);
            }
            
            log.info("Reinicio diario completado para {} hábitos activos", activeHabits.size());
            
        } catch (Exception e) {
            log.error("Error durante el reinicio diario de hábitos: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza las rachas del hábito basándose en si fue completado ayer
     */
    private void updateStreaks(Habit habit, LocalDate yesterday) {
        boolean wasCompletedYesterday = false;
        
        // Verificar si el hábito fue completado ayer
        if (habit.getLastTrackedDate() != null && habit.getLastTrackedDate().equals(yesterday)) {
            if (habit.getTrackingType() == com.life.master_api.entities.TrackingType.BOOLEAN) {
                wasCompletedYesterday = habit.getTodayCompleted();
            } else {
                // Para hábitos de cantidad, verificar si se alcanzó la meta diaria
                wasCompletedYesterday = habit.getDailyGoal() != null && 
                                      habit.getTodayQuantity() != null && 
                                      habit.getTodayQuantity() >= habit.getDailyGoal();
            }
        }
        
        if (wasCompletedYesterday) {
            // Incrementar racha actual
            int newStreak = habit.getCurrentStreak() + 1;
            habit.setCurrentStreak(newStreak);
            
            // Actualizar mejor racha si es necesario
            if (newStreak > habit.getBestStreak()) {
                habit.setBestStreak(newStreak);
            }
        } else {
            // Reiniciar racha actual si no fue completado ayer
            habit.setCurrentStreak(0);
        }
    }
}