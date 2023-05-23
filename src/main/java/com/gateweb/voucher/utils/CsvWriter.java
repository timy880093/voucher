package com.gateweb.voucher.utils;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import java.io.*;
import java.util.List;

public class CsvWriter {
  public static String writeToString(List<String[]> arrayList, boolean addBom) throws IOException {
    //    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
    //        Writer out = new OutputStreamWriter(bos, StandardCharsets.UTF_8)) {
    try (Writer w = new StringWriter()) {
      if (addBom) w.write('\ufeff'); // Add BOM character for Excel compatibility
      try (CSVWriter csvw =
          new CSVWriter(
              w, ',', '"', CSVWriter.NO_ESCAPE_CHARACTER, System.getProperty("line.separator"))) {
        //        writer.writeNext(GW_DOWNLOAD_CSV_COLUMN);
        csvw.writeAll(arrayList);
        //        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        return w.toString();
      }
    }
  }
}
