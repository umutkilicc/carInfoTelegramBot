package com.echobot.echobotexample.service;

import com.echobot.echobotexample.entity.Car;
import com.echobot.echobotexample.repository.CarRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ReadExcelService {

    private final CarRepository carRepository;

    public ReadExcelService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public int readExcelFile(String filePath, String previousMessage) {
        int lastRowNum = -1;
        int total = 0;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            if (previousMessage.equals("/yukle") && sheet.getLastRowNum() > 1) {
                carRepository.deleteAll();
            }
            total = (int) carRepository.count();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row != null) {

                    Cell cellPlaka = row.getCell(0);
                    Cell cellStartDate = row.getCell(1);
                    Cell cellEndDate = row.getCell(2);
                    Cell cellCarInfo = row.getCell(3);
                    Cell cellCompany = row.getCell(4);

                    Car car = new Car();
                    car.setPlaka(cellPlaka.toString());
                    car.setStartDate(cellStartDate.toString());
                    car.setEndDate(cellEndDate.toString());
                    car.setCar(cellCarInfo.toString());
                    car.setFirma(cellCompany.toString());
                    carRepository.save(car);
                }
            }

            lastRowNum = sheet.getLastRowNum();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastRowNum + total;
    }
}
