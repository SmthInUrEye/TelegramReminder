package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService{

private final UserRepository userRepository;

public UserService(UserRepository userRepository){
    this.userRepository=userRepository;
}

public Optional<User> findByTelegramId(Long telegramId){
    return userRepository.findByTelegramId(telegramId);
}

public List<User> getAllUsers(){
    return userRepository.findAll();

}

public User registerIfNotExists(com.pengrad.telegrambot.model.User tgUser){
    Long telegramId=tgUser.id().longValue();
    return userRepository.findByTelegramId(telegramId).orElseGet(()->{
        User newUser=new User(telegramId,tgUser.username(),LocalDateTime.now());

        return userRepository.save(newUser);

    });
}
}
