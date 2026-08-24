package com.servicecop.FileManager.student;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentCsvRepresentation {
    @CsvBindByName(column = "firstname")
    private String fname;
    @CsvBindByName(column = "lastname")
    private String lname;
    @CsvBindByName(column = "age")
    private int age;

}
