package com.codagis.nordeste_servicos.util;

import com.codagis.nordeste_servicos.model.Cliente;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {

    public static String[] HEADERS = { "Id", "Nome", "CPF/CNPJ", "Email", "Telefone", "Endereço", "Cidade", "Estado" };
    public static String SHEET = "Clientes";

    public static ByteArrayInputStream clientesToExcel(List<Cliente> clientes) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header Font
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            // Header Cell Style
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
                cell.setCellStyle(headerCellStyle);
            }
            // Set header row height
            headerRow.setHeightInPoints((float) 25);


            // Data Cell Style
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
            dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIdx = 1;
            for (Cliente cliente : clientes) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(cliente.getId());
                row.createCell(1).setCellValue(cliente.getNomeCompleto());
                row.createCell(2).setCellValue(cliente.getCpfCnpj());
                row.createCell(3).setCellValue(cliente.getEmail());
                row.createCell(4).setCellValue(cliente.getTelefonePrincipal());
                String endereco = cliente.getRua() + ", " + cliente.getNumero() + " " + cliente.getBairro();
                row.createCell(5).setCellValue(endereco);
                row.createCell(6).setCellValue(cliente.getCidade());
                row.createCell(7).setCellValue(cliente.getEstado());

                for(int i = 0; i < HEADERS.length; i++) {
                    row.getCell(i).setCellStyle(dataCellStyle);
                }
            }

            // Auto-size columns and add padding
            for (int col = 0; col < HEADERS.length; col++) {
                sheet.autoSizeColumn(col);
                sheet.setColumnWidth(col, sheet.getColumnWidth(col) + 1200);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
} 