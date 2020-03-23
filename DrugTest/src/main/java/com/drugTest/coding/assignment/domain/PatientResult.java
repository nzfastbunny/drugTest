package com.drugTest.coding.assignment.domain;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class PatientResult {
    /**
     * The patient ID
     */
    private long id;
    /**
     * The formatted list of records displayed as a string
     */
    private String record;

    public PatientResult(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}