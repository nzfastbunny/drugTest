package com.drugTest.coding.assignment.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Patient {
    private long id;
    private AnalysisResult.PatientType patientType;
    private List<PurchaseRecord> records = new ArrayList<>();

    public Patient(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AnalysisResult.PatientType getPatientType() {
        return patientType;
    }

    public void setPatientType(AnalysisResult.PatientType patientType) {
        this.patientType = patientType;
    }

    public List<PurchaseRecord> getRecords() {
        return records;
    }

    public void setRecords(List<PurchaseRecord> records) {
        this.records = records;
    }

    public void sortRecords() {
        Collections.sort(records, new RecordComparator());
    }

    public String printRecord() {
        StringBuilder builder = new StringBuilder();
        for (PurchaseRecord record : records) {
            builder.append(String.format(" %s (day %s),", record.getMedication(), record.getDay()));
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}