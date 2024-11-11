package org.example.bbbackend.controller;

import org.example.bbbackend.dto.ReminderDTO;
import org.example.bbbackend.model.Reminder;
import org.example.bbbackend.repository.ReminderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {
    private final ReminderRepository reminderRepository;

    public ReminderController(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @GetMapping
    public List<Reminder> getAllReminders() {
        return reminderRepository.findAll();
    }

    @PostMapping
    public Reminder createReminder(@RequestBody ReminderDTO reminderDTO) {
        Reminder reminder = new Reminder();
        reminder.setId(reminderDTO.getId());
        reminder.setReminderDate(reminderDTO.getReminderDate());
        reminder.setCareType(reminderDTO.getCareType());
        reminder.setStatus(reminderDTO.getStatus());
        return reminderRepository.save(reminder);
    }
}