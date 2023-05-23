package com.gateweb.voucher.utils;

import com.aspose.cells.Cell;
import com.aspose.cells.CellValueType;
import com.aspose.cells.Cells;
import com.aspose.cells.Row;
import com.aspose.cells.RowCollection;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;

@Slf4j
public class ExcelReader {

  //  public static String toCsv(InputStream inputStream)
  //      throws Exception {
  //    try (InputStream stream = inputStream; OutputStream outputStream=new
  // ByteArrayOutputStream()) {
  //      final Workbook wb = new Workbook(stream);
  //      wb.save(outputStream, SaveFormat.CSV);
  //      new ByteArrayInputStream()
  //      return read(wb, headers, format);
  //    } catch (IOException e) {
  //      throw new Exception("read error: " + e.getMessage());
  //    }
  //  }

  /**
   * @param inputStream
   * @param headers 欄位順序
   * @param format 資料格式
   * @param <T> 資料物件
   * @return
   * @throws Exception
   */
  public static <T> List<T> read(InputStream inputStream, String[] headers, Class<T> format)
      throws Exception {
    try (InputStream stream = inputStream) {
      final Workbook wb = new Workbook(stream);
      return read(wb, headers, format);
    } catch (IOException e) {
      throw new Exception("read error: " + e.getMessage());
    }
  }

  /**
   * @param filePath 檔案路徑
   * @param headers 欄位順序
   * @param format 資料格式
   * @param <T> 資料物件
   * @return
   * @throws Exception
   */
  public static <T> List<T> read(String filePath, String[] headers, Class<T> format)
      throws Exception {
    final Workbook wb = new Workbook(filePath);
    return read(wb, headers, format);
  }

  /**
   * 讀取 Excel
   *
   * @param wb Workbook
   * @param headers 欄位順序
   * @param format 資料格式
   * @param <T> 資料物件
   * @return
   * @throws Exception
   */
  static <T> List<T> read(Workbook wb, String[] headers, Class<T> format) throws Exception {
    try {
      final Worksheet sheet = wb.getWorksheets().get(0);
      final Cells cells = sheet.getCells();
      cells.deleteRows(0, 1, true); // 刪除第一行
      cells.deleteBlankRows(); // 刪除空行
      return toObjList(cells.getRows(), headers, format);
      //      //Create an instance of DeleteOptions class
      //      DeleteOptions options = new DeleteOptions();
      //      //Set UpdateReference property to true;
      //      options.setUpdateReference(true);
      //      //Delete all blank rows and headers
      //      cells.deleteBlankColumns(options);
      //      cells.deleteBlankRows(options);
    } catch (Exception e) {
      throw new Exception("Parse Excel error : " + e.getMessage());
    }
  }

  /**
   * 日期格式，只保留日期，去除時間
   *
   * @param cell
   * @param isRemoveTime
   * @return
   */
  static Object parseCellValue(Cell cell, boolean isRemoveTime) {
    final Object value = cell.getValue();
    if (isRemoveTime) {
      switch (cell.getType()) {
          //            case CellValueType.IS_BOOL:
          //              value = row.get(i).getBoolValue();
          //              break;
        case CellValueType.IS_DATE_TIME: // 只取日期，後面時間去掉，否則會解析失敗
          return StringUtils.substringBefore(value.toString(), "T");
          //            case CellValueType.IS_NULL:
          //              value = "";
          //              break;
          //            case CellValueType.IS_NUMERIC:
          //              value = row.get(i).getIntValue();
          //              break;
          //            case CellValueType.IS_STRING:
          //              value = row.get(i).getStringValue();
          //              break;
      }
    }
    return value;
  }

  static <T> List<T> toObjList(RowCollection rows, String[] headers, Class<T> format)
      throws InstantiationException, IllegalAccessException {
    final int headerLength = headers.length;
    final List<T> results = new ArrayList<>();
    for (Object o : rows) {
      final Row row = (Row) o;
      final T obj = format.newInstance();
      for (int i = 0; i < headerLength; i++) {
        final Object value = parseCellValue(row.get(i));
        ReflectUtils.invokeSetter(obj, headers[i], value);
      }
      results.add(obj);
    }
    return results;
  }

  static Object parseCellValue(Cell cell) {
    return parseCellValue(cell, true);
  }
}
