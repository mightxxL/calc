package com.uniinclusive.export;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: ghlin
 * @Date: 2019/8/13 16:53
 */
public class ExcelExportUtils {
    private static Logger logger = LoggerFactory.getLogger(ExcelExportUtils.class);

    public static <T> void exportExcel(Map<String, String> headers, Collection<T> dataset, OutputStream out, String pattern) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        write2Sheet(workbook, sheet, headers, dataset, pattern);
        try {
            workbook.write(out);

        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    public static <T> void write2Sheet(HSSFWorkbook workbook, HSSFSheet sheet, Map<String, String> headers, Collection<T> dataset, String pattern) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        setCellStyle(cellStyle);

        sheet.setColumnWidth(0, 200);
        //时间格式默认"yyyy-MM-dd"
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        // 标题行转中文
        Set<String> keys = headers.keySet();
        Iterator<String> it1 = keys.iterator();
        //存放临时键变量
        String key = "";
        //标题列数
        int c = 0;
        while (it1.hasNext()) {
            key = it1.next();
            if (headers.containsKey(key)) {
                HSSFCell cell = row.createCell(c);
                cell.setCellStyle(cellStyle);
                HSSFRichTextString text = new HSSFRichTextString(headers.get(key));
                cell.setCellValue(text);
                c++;
            }
        }
        String[] keysArr = new String[keys.size()];
        keys.toArray(keysArr);
        // 写入数据
        writeData2Sheet(sheet, dataset, pattern, cellStyle, keysArr, 0);
    }

    public static <T> void writeData2Sheet(HSSFSheet sheet, Collection<T> dataset, String pattern, HSSFCellStyle cellStyle, String[] keys, int startIndex) {
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        HSSFRow row;// 遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        while (it.hasNext()) {
            startIndex ++;
            row = sheet.createRow(startIndex);
            T t = it.next();
            try {
                if (t instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) t;
                    int cellNum = 0;
                    //遍历列名
                    for (String titleKey : keys) {
                        Object value = map.get(titleKey);
                        HSSFCell cell = row.createCell(cellNum);
                        cell.setCellStyle(cellStyle);
                        cellNum = setCellValue(cell, value, pattern, cellNum, row);

                        cellNum++;
                    }
                } else {
                    int cellNum = 0;
                    BeanInfo info = null;
                    try {
                        info = Introspector.getBeanInfo(t.getClass(), Object.class);
                    } catch (IntrospectionException e) {
                        e.printStackTrace();
                    }
                    Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
                    for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                        String fieldName = descriptor.getName();
                        Method readMethod = descriptor.getReadMethod();
                        Method writeMethod = descriptor.getWriteMethod();
                        if (readMethod != null && writeMethod != null) {
                            propertyDescriptorMap.put(fieldName, descriptor);
                        }
                    }
                    for (String titleKey : keys) {
                        PropertyDescriptor propertyDescriptor = propertyDescriptorMap.get(titleKey);
                        if (propertyDescriptor != null) {
                            HSSFCell cell = row.createCell(cellNum);
                            Object fieldValue = propertyDescriptor.getReadMethod().invoke(t);
                            cell.setCellStyle(cellStyle);
                            cellNum = setCellValue(cell, fieldValue, pattern, cellNum, row);
                            cellNum++;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }
        }
    }

    public static void setCellStyle(HSSFCellStyle cellStyle) {
        // 换行
        cellStyle.setWrapText(true);
        // 居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
    }

    private static int setCellValue(HSSFCell cell, Object value, String pattern, int cellNum, HSSFRow row) {
        String textValue = null;
        if (value instanceof Integer) {
            int intValue = (Integer) value;
            cell.setCellValue(intValue);
        } else if (value instanceof Float) {
            float fValue = (Float) value;
            cell.setCellValue(fValue);
        } else if (value instanceof Double) {
            double dValue = (Double) value;
            cell.setCellValue(dValue);
        } else if (value instanceof Long) {
            long longValue = (Long) value;
            cell.setCellValue(longValue);
        } else if (value instanceof Boolean) {
            boolean bValue = (Boolean) value;
            cell.setCellValue(bValue);
        } else if (value instanceof Date) {
            Date date = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        } else if (value instanceof String[]) {
            String[] strArr = (String[]) value;
            for (int j = 0; j < strArr.length; j++) {
                String str = strArr[j];
                cell.setCellValue(str);
                if (j != strArr.length - 1) {
                    cellNum++;
                    cell = row.createCell(cellNum);
                }
            }
        } else if (value instanceof Double[]) {
            Double[] douArr = (Double[]) value;
            for (int j = 0; j < douArr.length; j++) {
                Double val = douArr[j];
                // 值不为空则set Value
                if (val != null) {
                    cell.setCellValue(val);
                }

                if (j != douArr.length - 1) {
                    cellNum++;
                    cell = row.createCell(cellNum);
                }
            }
        } else {
            // 其它数据类型都当作字符串简单处理
            textValue = value == null ? "" : value.toString();
        }
        if (textValue != null) {
            HSSFRichTextString richString = new HSSFRichTextString(textValue);
            cell.setCellValue(richString);
        }
        return cellNum;
    }

}
