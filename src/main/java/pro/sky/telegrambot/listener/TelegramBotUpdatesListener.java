package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationService;
import pro.sky.telegrambot.service.UserService;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener{

private final Logger logger=LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

@Autowired
private UserService userService;

@Autowired
private NotificationService notificationService;

@Autowired
private TelegramBot telegramBot;

@PostConstruct
public void init(){
    telegramBot.setUpdatesListener(this);
}

@Override
public int process(List<Update> updates){
    updates.forEach(update->{
        logger.info("Processing update: {}",update);

        com.pengrad.telegrambot.model.User tgUser=update.message().from();

        Message message=update.message();
        String text=message.text();
        Long chatId=message.chat().id();

        userService.registerIfNotExists(tgUser);

        String response="Кажется я что-то упустил";

        if(text.equals("/start")){
            response="Привет, "+tgUser.username()+"\nЯ бот-напоминальщик.\n"+"Чтобы создать напоминание, напиши его в формате:\n"+"01.01.2026 20:00 Сделать домашнюю работу";
        }else{

            Optional<String> saveResult=notificationService.processIncomingMessage(tgUser.id(),tgUser.username(),text);

            response=saveResult.orElse("⚠️ Неверный формат. Пример:\n01.01.2026 20:00 Сделать домашнюю работу");
        }


        telegramBot.execute(new SendMessage(chatId,response));
    });
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
}

@EventListener
public void handleNotificationTask(NotificationTask task){
    String text="🔔 Напоминание: "+task.getText();
    try{
        telegramBot.execute(new SendMessage(task.getTelegramId(),text));
        logger.info("Напоминание отправлено пользователю {}: {}",task.getUsername(),text);
    }catch(Exception e){
        logger.error("Не удалось отправить напоминание для пользователя {}: {}",task.getUsername(),text,e);
    }
}
}
