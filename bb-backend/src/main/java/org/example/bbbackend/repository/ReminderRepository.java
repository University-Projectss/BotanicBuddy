package org.example.bbbackend.repository;

import org.example.bbbackend.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, String> {
}