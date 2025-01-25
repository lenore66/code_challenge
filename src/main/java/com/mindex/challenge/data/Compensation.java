package com.mindex.challenge.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Compensation {
    private String employeeId;
    private String salary;
    private String effectiveDate;
}
