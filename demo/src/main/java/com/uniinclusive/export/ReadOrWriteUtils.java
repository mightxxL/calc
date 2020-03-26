package com.uniinclusive.export;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: ghlin
 * @Date: 2020/3/21 15:13
 */
public class ReadOrWriteUtils {

    public static <T> List<T> readExcel(String filePath, String sheetName, String[] fields, Class<T> clazz) {
        FileInputStream fileInputStream = null;
        List<T> projects = new ArrayList<>();
        try {
            fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = sheets.getSheet(sheetName);
            for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
                T project = clazz.newInstance();
                XSSFRow xssfRow = sheet.getRow(rowIndex);
                if (isRowEmpty(xssfRow)) {
                    continue;
                }
                //循环取每个单元格(cell)的数据
                for (int cellIndex = 0; cellIndex < fields.length; cellIndex++) {
                    String field = fields[cellIndex];
                    XSSFCell xssfCell = xssfRow.getCell(cellIndex);
                    if (xssfCell != null) {
                        String value = getCellValue(xssfCell);
                        SetValueUtils.setProjectValue(project, field, value);
                    }
                }
                projects.add(project);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return projects;
    }

    public static <T> void writeToExcel(String writeFilePath, List<T> exportDatas, String[] keys) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(35);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        ExcelExportUtils.writeData2Sheet(sheet, exportDatas, null, cellStyle, keys, 0);
        File file = new File(writeFilePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getCellValue(XSSFCell cell) {
        String value = "";
        // 以下是判断数据的类型
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                value = cell.getNumericCellValue() + "";
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    if (date != null) {
                        value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    } else {
                        value = "";
                    }
                } else {
                    value = new DecimalFormat("0").format(cell.getNumericCellValue());
                }
                break;
            case HSSFCell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue() + "";
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                value = "";
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                value = "非法字符";
                break;
            default:
                value = "未知类型";
                break;
        }
        return value;
    }

    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false;
        }
        return true;
    }
}
