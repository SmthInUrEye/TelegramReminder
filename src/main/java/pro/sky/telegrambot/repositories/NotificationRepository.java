package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationTask,Long>{

    public List<NotificationTask> findAll();

    Optional<NotificationTask> findByTelegramId(Long telegram_id);

    List<NotificationTask> findAllByDateTime(LocalDateTime dateTime);
}
