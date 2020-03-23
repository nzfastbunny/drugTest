package com.drugTest.coding.assignment.domain;

import java.util.Comparator;

public class RecordComparator implements Comparator<PurchaseRecord> {
    public int compare(PurchaseRecord r1, PurchaseRecord r2) {
        return ((Integer) r1.getDay()).compareTo(r2.getDay());
    }
}
