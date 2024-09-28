package com.echobot.echobotexample.service;

import com.echobot.echobotexample.entity.Car;
import com.echobot.echobotexample.entity.Log;
import com.echobot.echobotexample.repository.CarRepository;
import com.echobot.echobotexample.repository.LogRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ExcelExporter {

    private final CarRepository carRepository;
    private final LogRepository logRepository;

    public ExcelExporter(CarRepository carRepository, LogRepository logRepository) {
        this.carRepository = carRepository;
        this.logRepository = logRepository;
    }

    public String exportDataToExcel() {
        String dateSuffix = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filePath = "home/ec2-user/new/AracBilgileri_" + dateSuffix + ".xlsx";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Araçlar");

        List<Car> cars = carRepository.findAll();

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Plaka");
        headerRow.createCell(1).setCellValue("Başlangıç Tarihi");
        headerRow.createCell(2).setCellValue("Bitiş Tarihi");
        headerRow.createCell(3).setCellValue("Araç Bilgisi");
        headerRow.createCell(4).setCellValue("Firma");

        int rowIndex = 1;
        for (Car car : cars) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(car.getPlaka());
            row.createCell(1).setCellValue(car.getStartDate());
            row.createCell(2).setCellValue(car.getEndDate());
            row.createCell(3).setCellValue(car.getCar());
            row.createCell(4).setCellValue(car.getFirma());
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    public String exportLogDataToExcel() {
        String dateSuffix = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filePath = "home/ec2-user/new/LogBilgileri_" + dateSuffix + ".xlsx";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Loglar");

        List<Log> logs = logRepository.findAll();

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Tarih");
        headerRow.createCell(1).setCellValue("Kullanıcı Id");
        headerRow.createCell(2).setCellValue("İsim Soyisim");
        headerRow.createCell(3).setCellValue("Mesaj");

        int rowIndex = 1;
        for (Log log : logs) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(log.getDate().toString());
            row.createCell(1).setCellValue(log.getUserId());
            row.createCell(2).setCellValue(log.getName());
            row.createCell(3).setCellValue(log.getMessage());
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }
}
