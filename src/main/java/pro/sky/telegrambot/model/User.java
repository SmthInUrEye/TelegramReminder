package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
public class User{

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long user_id;

@Column(name="telegram_Id", nullable=false, unique=true)
private Long telegramId;

@Column(name="username")
private String username;

@Column(name="registered_at")
private LocalDateTime registered_at;

public User(){
}

;

public User(Long telegramId,String username,LocalDateTime registered_at){
    this.telegramId=telegramId;
    this.username=username;
    this.registered_at=registered_at;
}

public Long getUser_id(){
    return user_id;
}

public void setUser_id(Long user_id){
    this.user_id=user_id;
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

public LocalDateTime getRegistered_at(){
    return registered_at;
}

public void setRegistered_at(LocalDateTime registered_at){
    this.registered_at=registered_at;
}
}
