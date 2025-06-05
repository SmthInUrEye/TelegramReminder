package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="notification_task")
public class NotificationTask{


@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long notification_id;

@Column(name="telegram_Id", nullable=false)
private Long telegramId;

@Column(name="username")
private String username;

@Column(name="date_time")
private LocalDateTime dateTime;

@Column(name="text")
private String text;

public NotificationTask(){
}

public NotificationTask(Long telegramId,String username,LocalDateTime dateTime,String text){
    this.telegramId=telegramId;
    this.username=username;
    this.dateTime=dateTime;
    this.text=text;
}

public String getText(){
    return text;
}

public void setText(String text){
    this.text=text;
}

public Long getNotification_id(){
    return notification_id;
}

public void setNotification_id(Long notification_id){
    this.notification_id=notification_id;
}

public Long getTelegramId(){
    return telegramId;
}

public void setTelegramId(Long telegramId){
    this.telegramId=telegramId;
}

public String getUsername(){
    return username;
}

public void setUsername(String username){
    this.username=username;
}

public LocalDateTime getDateTime(){
    return dateTime;
}

public void setDateTime(LocalDateTime dateTime){
    this.dateTime=dateTime;
}

}
