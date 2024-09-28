package com.echobot.echobotexample.service;

import com.echobot.echobotexample.entity.Log;
import com.echobot.echobotexample.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Transactional
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void saveLog(Update update) {

        long timestampLong = update.getMessage().getDate().longValue();
        Instant instant = Instant.ofEpochSecond(timestampLong);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Istanbul")).plusHours(3);

        Log log = new Log();
        log.setName(update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
        log.setDate(zonedDateTime);
        log.setUserId(update.getMessage().getFrom().getId());
        log.setMessage(update.getMessage().getText());

        logRepository.save(log);

    }
}
