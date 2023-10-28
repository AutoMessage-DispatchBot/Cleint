package database;

import data.Buyer;
import data.ContactInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SaveClients {
    private SaveClients() {}

    public static synchronized void saveCsvContacts(List<Buyer> buyers) {
        try {
            FileWriter writer = new FileWriter("contacts.csv");

            // Write header
            writer.append("Given Name;Family Name;Organization 1 - Name;Organization 1 - Title;Phone 1 - Value;E-mail 1 - Value;Website 1 - Value;Address 1 - Formatted");
            writer.append("\n");

            // Write data
            for (Buyer buyer: buyers) {
                for (int i = 0; i < buyer.contactInfos().length; i++) {
                    writer.append(buyer.contactInfos()[i].name()).append(";");
                    writer.append(buyer.contactInfos()[i].surname()).append(";");
                    writer.append(buyer.companyName()).append(";");
                    writer.append(buyer.contactInfos()[i].jobTitle()).append(";");
                    writer.append("+" + buyer.contactInfos()[i].phone()).append(";");
                    writer.append(buyer.emails()[0]).append(";");
                    writer.append(buyer.site()).append(";");
                    writer.append(buyer.address()).append("\n");
                }
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized void saveExcelContacts(List<Buyer> buyers) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Данные");

        // Заголовки столбцов
        String[] headers = {
                "id",
                "Название компании",
                "Адрес",
                "Тип",
                "Регион",
                "Сайт",
                "Менеджер",
                "Источник",
                "Категория",
                "Телефон",
                "Имя",
                "Почта",
                "Комментарий"
        };

        // Запись заголовков
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<Object[]> list = new LinkedList<>();
        for(Buyer buyer: buyers) {
            Object[] objects = new Object[13];
            objects[0] = (long) buyer.id();
            objects[1] = buyer.companyName();
            objects[2] = buyer.address();
            objects[3] = buyer.type().toString();
            objects[4] = buyer.region().toString();
            objects[5] = buyer.site();
            objects[6] = buyer.manager();
            objects[7] = buyer.source().toString();
            objects[8] = buyer.category().toString();
            if(buyer.contactInfos().length > 0) {
                objects[9] = buyer.contactInfos()[0].phone();
                objects[10] = buyer.contactInfos()[0].name();
            } else {
                objects[9] = ""; // phone
                objects[10] = ""; // name
            }
            objects[11] = buyer.emails()[0];
            objects[12] = buyer.additionalInfo();

            list.add(objects);

            if(buyer.contactInfos().length > buyer.emails().length) {
                if (buyer.contactInfos().length > 1) {
                    for (int i = 1; i < buyer.contactInfos().length; i++) {
                        ContactInfo info = buyer.contactInfos()[i];
                        try {
                            objects = new Object[]{null, null, null, null, null, null, null, null, null, info.phone(), info.name(), buyer.emails()[i], null};
                        } catch (ArrayIndexOutOfBoundsException e) {
                            objects = new Object[]{null, null, null, null, null, null, null, null, null, info.phone(), info.name(), null, null};
                        }
                        list.add(objects);
                    }
                }
            } else {
                if (buyer.emails().length > 1) {
                    for (int i = 1; i < buyer.emails().length; i++) {
                        try {
                            ContactInfo info = buyer.contactInfos()[i];
                            objects = new Object[]{null, null, null, null, null, null, null, null, null, info.phone(), info.name(), buyer.emails()[i], null};
                        } catch (ArrayIndexOutOfBoundsException e) {
                            objects = new Object[]{null, null, null, null, null, null, null, null, null, null, null, buyer.emails()[i], null};
                        }
                        list.add(objects);
                    }
                }
            }
        }

        Object[][] data = list.toArray(new Object[0][]);

        // Запись данных
        int rowIndex = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (Object cellData : rowData) {
                Cell cell = row.createCell(cellIndex++);
                if (cellData != null) {
                    if (cellData instanceof String) {
                        cell.setCellValue((String) cellData);
                    } else if (cellData instanceof Long) {
                        cell.setCellValue((Long) cellData);
                    }
                }
            }
        }

        // Автонастройка ширины столбцов
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Сохранение документа
        try (FileOutputStream outputStream = new FileOutputStream("clients.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
