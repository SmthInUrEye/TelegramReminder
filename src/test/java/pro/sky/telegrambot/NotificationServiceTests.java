package test.java.pro.sky.telegrambot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationRepository;
import pro.sky.telegrambot.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest{

@Mock
private NotificationRepository notificationRepository;

@Mock
private ApplicationEventPublisher eventPublisher;

@InjectMocks
private NotificationService notificationService;

private final Long telegramId=12345L;
private final String username="test_user";

@BeforeEach
void setUp(){}

@Test
void parseMessage_validMessage_shouldReturnTask(){
    String message="01.01.2025 12:00 Сделать домашку";

    Optional<NotificationTask> result=notificationService.parseMessage(message,telegramId,username);

    assertTrue(result.isPresent());
    NotificationTask task=result.get();
    assertEquals("Сделать домашку",task.getText());
    assertEquals(telegramId,task.getTelegramId());
    assertEquals(username,task.getUsername());
    assertEquals(LocalDateTime.of(2025,1,1,12,0),task.getDateTime());
}

@Test
void parseMessage_invalidMessage_shouldReturnEmpty(){
    String message="Неверный формат без даты";

    Optional<NotificationTask> result=notificationService.parseMessage(message,telegramId,username);

    assertTrue(result.isEmpty());
}

@Test
void saveNotification_shouldCallRepositorySave(){
    LocalDateTime dateTime=LocalDateTime.of(2025,5,18,15,0);
    String text="Встреча с командой";
    NotificationTask task=new NotificationTask(telegramId,username,dateTime,text);

    when(notificationRepository.save(any(NotificationTask.class))).thenReturn(task);


    NotificationTask result=notificationService.saveNotification(telegramId,username,dateTime,text);

    verify(notificationRepository).save(any(NotificationTask.class));

    assertNotNull(result);
    assertEquals(text,result.getText());

}

@Test
void processIncomingMessage_valid_shouldReturnConfirmation(){
    String message="01.01.2025 10:00 Проверка кода";

    when(notificationRepository.save(any(NotificationTask.class))).thenAnswer(invocation->invocation.getArgument(0));

    Optional<String> response=notificationService.processIncomingMessage(telegramId,username,message);

    assertTrue(response.isPresent());
    assertEquals("✅ Напоминание сохранено!",response.get());
}

@Test
void processIncomingMessage_invalid_shouldReturnEmpty(){
    String message="неправильный ввод";

    Optional<String> response=notificationService.processIncomingMessage(telegramId,username,message);

    assertTrue(response.isEmpty());
}

@Test
void checkAndPublishDueNotifications_shouldPublishAndDelete(){
    LocalDateTime now=LocalDateTime.now().withSecond(0).withNano(0);

    NotificationTask task=new NotificationTask(telegramId,username,now,"Позвонить бабушке");

    when(notificationRepository.findAllByDateTime(eq(now))).thenReturn(List.of(task));

    notificationService.checkAndPublishDueNotifications();

    verify(eventPublisher).publishEvent(task);
    verify(notificationRepository).delete(task);
}
}