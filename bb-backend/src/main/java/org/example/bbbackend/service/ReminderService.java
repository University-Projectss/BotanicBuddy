package org.example.bbbackend.service;

import org.example.bbbackend.model.Reminder;
import org.example.bbbackend.repository.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {
    private final ReminderRepository reminderRepository;

    @Autowired
    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public List<Reminder> getAllReminders() {
        return reminderRepository.findAll();
    }

    public Optional<Reminder> getReminderById(String reminderId) {
        return reminderRepository.findById(reminderId);
    }

    public Reminder createReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public Reminder updateReminderStatus(String reminderId, String status) {
        return reminderRepository.findById(reminderId).map(reminder -> {
            reminder.setStatus(status);
            return reminderRepository.save(reminder);
        }).orElseThrow(() -> new RuntimeException("Reminder not found"));
    }

    public void deleteReminder(String reminderId) {
        reminderRepository.deleteById(reminderId);
    }
}
