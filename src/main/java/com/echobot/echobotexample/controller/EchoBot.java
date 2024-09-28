package com.echobot.echobotexample.controller;

import com.echobot.echobotexample.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Component
class EchoBot extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
    private final String BOT_TOKEN;
    private final String BOT_USERNAME;
    private final LogService logService;
    private final ExcelDowloadService excelDowloadService;
    private final CarService carService;
    private final ReadExcelService readExcelService;
    private final Button button;
    private Map<String, String> userLastMessages = new HashMap<>();

    EchoBot(@Value("${bot.BOT_TOKEN}") String BOT_TOKEN, @Value("${bot.BOT_USERNAME}") String BOT_USERNAME, LogService logService, ExcelDowloadService excelDowloadService, CarService carService, ReadExcelService readExcelService, Button button) {
        this.BOT_TOKEN = BOT_TOKEN;
        this.BOT_USERNAME = BOT_USERNAME;
        this.logService = logService;
        this.excelDowloadService = excelDowloadService;
        this.carService = carService;
        this.readExcelService = readExcelService;
        this.button = button;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasDocument()) {
            Document document = update.getMessage().getDocument();
            String fileId = document.getFileId();

            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);

            try {
                File file = execute(getFile);
                String filePath = file.getFilePath();
                String url = "https://api.telegram.org/file/bot" + BOT_TOKEN + "/" + filePath;

                String fileName = document.getFileName();  // Dosya adını alıyoruz
                String downloadedFilePath = "src/" + fileName;

                try (InputStream in = new URL(url).openStream()) {
                    Files.copy(in, Paths.get(downloadedFilePath), StandardCopyOption.REPLACE_EXISTING);
                } catch (MalformedURLException e) {
                    System.err.println("URL formatında bir hata var: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Dosya indirme sırasında bir hata oluştu: " + e.getMessage());
                }

                if (userLastMessages.containsKey(update.getMessage().getChatId().toString())) {
                    String previousMessage = userLastMessages.get(update.getMessage().getChatId().toString());
                    if (previousMessage.equals("/yukle")) {
                        readExcelService.readExcelFile(downloadedFilePath, previousMessage);
                        try {
                            sendMessage(update.getMessage().getChatId().toString(), "Excel dosyası başarıyla yüklenmiştir. Yüklenen araç sayısı: " + readExcelService.readExcelFile(downloadedFilePath, previousMessage));
                        } catch (Exception e) {
                            sendMessage(update.getMessage().getChatId().toString(), "Excel dosyası yüklenirken hata ile karşılaşıldı. Lütfen Excel dosyasını ve başlıkları kontrol ediniz.");
                        }
                    } else if (previousMessage.equals("/ekle")) {
                        readExcelService.readExcelFile(downloadedFilePath, previousMessage);
                        try {
                            sendMessage(update.getMessage().getChatId().toString(), "Excel dosyası başarıyla yüklenmiştir. Kayıtlı araç sayısı: " + readExcelService.readExcelFile(downloadedFilePath, previousMessage));
                        } catch (Exception e) {
                            sendMessage(update.getMessage().getChatId().toString(), "Excel dosyası yüklenirken hata ile karşılaşıldı. Lütfen Excel dosyasını ve başlıkları kontrol ediniz.");
                        }
                    }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            logService.saveLog(update);
            if (update.getMessage().getText().equals("Yeni liste yükle")) {
                userLastMessages.put(update.getMessage().getChatId().toString(), update.getMessage().getText());
                try {
                    sendMessage(update.getMessage().getChatId().toString(), "Lütfen bir Excel dosyası yükleniyiniz. Bu işlem sonucunda tüm veriler silinecek ve yüklediğiniz Excel içerisindeki veriler kaydedilecektir.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().equals("Yeni araç ekle")) {
                userLastMessages.put(update.getMessage().getChatId().toString(), update.getMessage().getText());
                try {
                    sendMessage(update.getMessage().getChatId().toString(), "Lütfen bir Excel dosyası yükleniyiniz. Bu işlem sonucunda mevcut verilerinize ek olarak yeni kayıtlar eklenecektir.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().equals("Mevcut listeyi indir")) {
                if (excelDowloadService.dateExcelDownload() != null) {
                    sendDocument(update.getMessage().getChatId().toString(), excelDowloadService.dateExcelDownload());
                } else {
                    sendMessage(update.getMessage().getChatId().toString(), "Excel dosyası oluşturulurken bir hata oluştu.");
                }
            } else if (update.getMessage().getText().equals("Log kayıtlarını indir")) {

                if (excelDowloadService.logExcelDownload() != null) {
                    sendDocument(update.getMessage().getChatId().toString(), excelDowloadService.logExcelDownload());
                } else {
                    sendMessage(update.getMessage().getChatId().toString(), "Excel dosyası oluşturulurken bir hata oluştu.");
                }
            } else if (update.getMessage().getText().equals("/start")) {
                SendMessage message = new SendMessage();
                message = button.createReplyKeyboardMessage(update.getMessage().getChatId());
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else {
                if (carService.getCar(update) != null) {
                    SendMessage message = carService.getCar(update);
                    try {
                        execute(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    sendMessage(update.getMessage().getChatId().toString(), "Böyle bir araç bulunmamaktadır. Lütfen tekrar deneyiniz.");
                }
            }
        }
    }

    private void sendDocument(String chatId, String filePath) {
        java.io.File file = new java.io.File(filePath);
        if (file.exists()) {
            SendDocument sendDocumentRequest = new SendDocument();
            sendDocumentRequest.setChatId(chatId);
            sendDocumentRequest.setDocument(new InputFile(file));
            sendDocumentRequest.setCaption("İndirmek için dosya: " + file.getName());

            try {
                execute(sendDocumentRequest);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            sendMessage(chatId, "Dosya bulunamadı: " + filePath);
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}