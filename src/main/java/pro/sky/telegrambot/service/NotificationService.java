package pro.sky.telegrambot.service;

import liquibase.exception.DateParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationService{

private static final Logger logger=LoggerFactory.getLogger(NotificationService.class);

private final NotificationRepository notificationRepository;
private final ApplicationEventPublisher eventPublisher;

public NotificationService(NotificationRepository notificationRepository, ApplicationEventPublisher eventPublisher){
    this.notificationRepository=notificationRepository;
    this.eventPublisher = eventPublisher;
}

public Optional<NotificationTask> parseMessage(String message,Long telegramId,String username){
    String regex="(\\d{2}\\.\\d{2}\\.\\d{4}) (\\d{2}:\\d{2}) (.+)";
    Pattern pattern=Pattern.compile(regex);
    Matcher matcher=pattern.matcher(message);

    if(matcher.matches()){
        try{
            String datePart=matcher.group(1);
            String timePart=matcher.group(2);
            String text=matcher.group(3);

            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime dateTime=LocalDateTime.parse(datePart+" "+timePart,formatter);

            NotificationTask task=new NotificationTask(telegramId,username,dateTime,text);
            return Optional.of(task);
        }catch(DateTimeParseException e){
            logger.warn("Ошибка парсинга даты: {}",e.getMessage());
        }catch(Exception e){
            logger.error("Неизвестная ошибка при парсинге напоминания",e);
        }
    }
    return Optional.empty();
}

public NotificationTask saveNotification(Long telegramId,String username,LocalDateTime dateTime,String text){
    NotificationTask task=new NotificationTask();
    task.setTelegramId(telegramId);
    task.setDateTime(dateTime);
    task.setUsername(username);
    task.setText(text);
    return notificationRepository.save(task);
}

public Optional<String> processIncomingMessage(Long telegramId, String username, String message) {
    Optional<NotificationTask> maybeTask = parseMessage(message, telegramId, username);

    if (maybeTask.isPresent()) {
        saveNotification(telegramId, username, maybeTask.get().getDateTime(), maybeTask.get().getText());
        return Optional.of("✅ Напоминание сохранено!");
    } else {
        return Optional.empty();
    }
}

public List<NotificationTask> getAllNotificationTasks(){
    return notificationRepository.findAll();
}

@Scheduled(cron = "0 * * * * *")
public void checkAndPublishDueNotifications() {
    LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
    logger.info("Проверка задач на: {}", now);

    List<NotificationTask> dueTasks = notificationRepository.findAllByDateTime(now);

    for (NotificationTask task : dueTasks) {
        logger.info("Публикую задачу: {}", task.getText());
        eventPublisher.publishEvent(task);
        notificationRepository.delete(task);
    }
}

}

