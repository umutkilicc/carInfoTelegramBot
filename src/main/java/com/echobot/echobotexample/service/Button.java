package com.echobot.echobotexample.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class Button {

    public SendMessage createReplyKeyboardMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Bir seçenek seçiniz:");

        // ReplyKeyboardMarkup oluşturuyoruz
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Satırlar için buton listesi
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        // Buton oluşturuyoruz
        row1.add(new KeyboardButton("Yeni liste yükle"));
        row1.add(new KeyboardButton("Yeni araç ekle"));
        row2.add(new KeyboardButton("Mevcut listeyi indir"));
        row2.add(new KeyboardButton("Log kayıtlarını indir"));

        // Satırları genel klavye listesine ekliyoruz
        keyboard.add(row1);
        keyboard.add(row2);

        // Klavyeyi markup'a ekliyoruz
        replyKeyboardMarkup.setKeyboard(keyboard);

        // Mesajın klavyesini ayarlıyoruz
        message.setReplyMarkup(replyKeyboardMarkup);

        return message;
    }
}
