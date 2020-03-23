package com.drugTest.coding.assignment.data;

import com.drugTest.coding.assignment.domain.PurchaseRecord;

import java.util.List;

public interface PurchaseRecordDAO {

    List<PurchaseRecord> allPurchaseRecords() throws Exception;

}
