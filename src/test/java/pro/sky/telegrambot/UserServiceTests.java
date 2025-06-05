package test.java.pro.sky.telegrambot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repositories.UserRepository;
import pro.sky.telegrambot.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest{

@Mock
private UserRepository userRepository;

@InjectMocks
private UserService userService;

private final Long telegramId=123L;
private final String username="test_user";

@Test
void findByTelegramId_shouldReturnUserIfExists(){
    User user=new User(telegramId,username,LocalDateTime.now());
    when(userRepository.findByTelegramId(telegramId)).thenReturn(Optional.of(user));

    Optional<User> result=userService.findByTelegramId(telegramId);

    assertTrue(result.isPresent());
    assertEquals(user,result.get());
}

@Test
void getAllUsers_shouldReturnAllUsers(){
    List<User> users=List.of(new User(1L,"user1",LocalDateTime.now()),new User(2L,"user2",LocalDateTime.now()));
    when(userRepository.findAll()).thenReturn(users);

    List<User> result=userService.getAllUsers();

    assertEquals(2,result.size());
    assertEquals("user1",result.get(0).getUsername());
}

@Test
void registerIfNotExists_shouldReturnExistingUser(){

    User existingUser=new User(telegramId,username,LocalDateTime.now());

    com.pengrad.telegrambot.model.User tgUser=mock(com.pengrad.telegrambot.model.User.class);
    when(tgUser.id()).thenReturn(telegramId);
    when(userRepository.findByTelegramId(telegramId)).thenReturn(Optional.of(existingUser));

    User result=userService.registerIfNotExists(tgUser);

    assertNotNull(result);
    assertEquals(existingUser,result);
    verify(userRepository,never()).save(any());
}

@Test
void registerIfNotExists_shouldCreateNewUserIfNotPresent(){
    com.pengrad.telegrambot.model.User tgUser=mock(com.pengrad.telegrambot.model.User.class);
    when(tgUser.id()).thenReturn((telegramId));
    when(tgUser.username()).thenReturn(username);

    when(userRepository.findByTelegramId(telegramId)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(invocation->invocation.getArgument(0));

    User result=userService.registerIfNotExists(tgUser);

    assertNotNull(result);
    assertEquals(telegramId,result.getTelegramId());
    assertEquals(username,result.getUsername());
    verify(userRepository).save(any(User.class));
}
}