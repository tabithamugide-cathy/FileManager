package com.servicecop.FileManager.student;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
//@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public Integer uploadStudents(MultipartFile file) throws IOException {
        Set<Student> students = parseCsv(file);
        studentRepository.saveAll(students);
                return students.size();
    }

 private Set<Student>parseCsv(MultipartFile file)throws IOException {
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            HeaderColumnNameMappingStrategy<StudentCsvRepresentation>strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(StudentCsvRepresentation.class);

            CsvToBean<StudentCsvRepresentation>csvToBean =
                    new CsvToBeanBuilder<StudentCsvRepresentation>(reader)
                            .withMappingStrategy(strategy)
                            .withIgnoreEmptyLine(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();

         return    csvToBean.parse()
                    .stream()
                    .map(csvLine->Student.builder()
                            .firstname(csvLine.getFname())
                            .lastname(csvLine.getLname())
                            .age(csvLine.getAge())
                            .build())
                    .collect(Collectors.toSet());

        }

 }
 //Exporting user data into Excel
    public void generateExcel(HttpServletResponse response) throws IOException {
        List<Student>students  = studentRepository.findAll();
       // System.out.println(students.toString());
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet  = workbook.createSheet("students info");
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Id");
        row.createCell(1).setCellValue("Firstname");
        row.createCell(2).setCellValue("Lastname");
        row.createCell(3).setCellValue("Age");

        int dataRowIndex = 1;
        for(Student student : students){
            XSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(student.getId());
            dataRow.createCell(1).setCellValue(student.getFirstname());
            dataRow.createCell(2).setCellValue(student.getLastname());
            dataRow.createCell(3).setCellValue(student.getAge());
            dataRowIndex ++;

        }
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();


    }
    //Exporting database to pdf
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<Student> students = studentRepository.findAll();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Title
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        Paragraph paragraph = new Paragraph("Student List Report", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph.setSpacingAfter(20);
        document.add(paragraph);

        // Table setup (4 columns matching the DBeaver data)
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 1.5f, 3.5f, 3.5f});

        // Write Table Header
        writeTableHeader(table);

        // Write Table Data
        writeTableData(table, students);

        document.add(table);
        document.close();
    }
    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(52, 152, 219));
        cell.setPadding(8);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.WHITE);

        String[] headers = {"ID", "Age", "First Name", "Last Name"};
        for (String header : headers) {
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }
    private void writeTableData(PdfPTable table, List<Student> students) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(11);

        for (Student student : students) {
            table.addCell(new Phrase(String.valueOf(student.getId()), font));
            table.addCell(new Phrase(String.valueOf(student.getAge()), font));
            table.addCell(new Phrase(student.getFirstname(), font));
            table.addCell(new Phrase(student.getLastname(), font));
        }
    }
    //Pagination
    public List<Student> fetchAllStudents(Pageable pageable, String search){
        if(search==null){
            return studentRepository.findAll(pageable).getContent();
        }
        else {
            return studentRepository.findByAge(search, pageable).getContent();
        }

    }

    }
