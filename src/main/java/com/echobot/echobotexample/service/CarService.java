package com.echobot.echobotexample.service;

import com.echobot.echobotexample.entity.Car;
import com.echobot.echobotexample.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Locale;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public SendMessage getCar(Update update) {
        SendMessage message = new SendMessage();
        List<Car> carList = carRepository.findByPlaka(update.getMessage().getText().toUpperCase(Locale.ROOT));
        if (carList.size() != 0) {
            carList.forEach(car -> {
                message.setChatId(update.getMessage().getChatId().toString());
                message.setReplyToMessageId(update.getMessage().getMessageId());
                message.setText("Başlangıç: " + car.getStartDate() + "\n" +
                        "Bitiş: " + car.getEndDate() + "\n" +
                        "Araç: " + car.getCar() + "\n" +
                        "Firma: " + car.getFirma());
            });
        } else {
            return null;
        }
        return message;

    }
}
