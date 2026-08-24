package com.servicecop.FileManager.student;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Integer> uploadstudents(
            @RequestPart("file")MultipartFile file
            ) throws IOException {
        return ResponseEntity.ok(studentService.uploadStudents(file));
    }

    @GetMapping("/excel")
    public void generateExcelReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=students.xlsx";
        response.setHeader(headerKey, headerValue);
        studentService.generateExcel(response);

    }

    @GetMapping("/export/pdf")
    public void generatePdfReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=students_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        studentService.exportToPdf(response);
    }
    //pagination
    @GetMapping("/list")
    public List<Student>getStudents(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                    @RequestParam(required = false, defaultValue = "3") int pageSize,
                                    @RequestParam(required = false, defaultValue = "id") String sortBy,
                                    @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                    @RequestParam(required = false) String search){
        Sort sort = null;
        if(sortDir.equalsIgnoreCase("ASC")) {
            sort = Sort.by(sortBy).ascending();
        }else {
            sort = Sort.by(sortBy).descending();
        }


        return studentService.fetchAllStudents(PageRequest.of(pageNo-1, pageSize, sort), search);
    }

}
