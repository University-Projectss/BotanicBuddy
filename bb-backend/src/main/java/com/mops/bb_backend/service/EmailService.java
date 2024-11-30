package com.mops.bb_backend.service;

import com.mops.bb_backend.model.Plant;
import com.mops.bb_backend.utils.ApplicationProperties;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final ApplicationProperties applicationProperties;
    private final PlantService plantService;

    @Scheduled(cron = "${sendgrid.water.reminder.scheduler}")
    public void sendWaterReminder() {
        var plantsToWater = plantService.getPlantsToWater();

        if (!plantsToWater.isEmpty()) {
            var userPlantMap = plantsToWater.stream().collect(Collectors.groupingBy(Plant::getUser));

            userPlantMap.forEach((user, plants) -> {
                try {
                    var templatePath = "src/main/resources/templates/water-reminder-template.html";
                    var plantListHtml = plants.stream()
                            .map(plant -> "<li>" + plant.getCommonName() + "(" + plant.getUploadDate().toString() + ")" + "</li>")
                            .collect(Collectors.joining());

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("username", user.getUsername());
                    placeholders.put("plant_list", plantListHtml);
                    placeholders.put("date", LocalDate.now().toString());

                    var emailContent = loadHtmlTemplate(templatePath, placeholders);
                    sendEmail(user.getAccount().getEmail(), "BotanicBuddy Reminder: Water Your Plants!", emailContent);
                } catch (Exception exception) {
                    log.error("Error occurred while running the water reminder job at " + LocalDateTime.now() + ": " + exception.getMessage());
                }
            });
        }
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        var apiKey = applicationProperties.getSendgridApiKey();
        var from = applicationProperties.getSendgridFromEmail();
        var name = applicationProperties.getSendgridFromName();

        var sendGrid = new SendGrid(apiKey);
        var fromEmail = new Email(from, name);
        var toEmail = new Email(to);
        var content = new Content("text/html", htmlContent);
        var mail = new Mail(fromEmail, subject, toEmail, content);

        try {
            var request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            var response = sendGrid.api(request);
            log.info("Email sent with status code: " + response.getStatusCode());
        } catch (Exception exception) {
            log.error("Error while sending email: " + exception.getMessage());
        }
    }

    public String loadHtmlTemplate(String templatePath, Map<String, String> placeholders) throws IOException {
        var template = new String(Files.readAllBytes(Paths.get(templatePath)));
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }
}
