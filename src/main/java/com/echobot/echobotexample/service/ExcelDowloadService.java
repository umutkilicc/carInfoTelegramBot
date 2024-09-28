package com.echobot.echobotexample.service;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ExcelDowloadService {

    private final ExcelExporter excelExporter;

    public ExcelDowloadService(ExcelExporter excelExporter) {
        this.excelExporter = excelExporter;
    }

    public String logExcelDownload() {
        String dateSuffix = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filePath = "home/ec2-user/new/LogBilgileri_" + dateSuffix + ".xlsx"; // Dosya adında tarihi ekle
        String exportedFilePath = excelExporter.exportLogDataToExcel();
        return exportedFilePath;
    }

    public String dateExcelDownload() {
        String dateSuffix = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filePath = "home/ec2-user/new/AracBilgileri_" + dateSuffix + ".xlsx"; // Dosya adında tarihi ekle
        String exportedFilePath = excelExporter.exportDataToExcel();
        return exportedFilePath;
    }
}
