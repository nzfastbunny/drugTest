package com.drugTest.coding.assignment.service;

import com.drugTest.coding.assignment.data.PurchaseRecordDAO;
import com.drugTest.coding.assignment.domain.AnalysisResult;
import com.drugTest.coding.assignment.domain.Patient;
import com.drugTest.coding.assignment.domain.PatientResult;
import com.drugTest.coding.assignment.domain.PurchaseRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.drugTest.coding.assignment.domain.AnalysisResult.PatientType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BIAnalysisServiceTest {

    @Mock
    private PurchaseRecordDAO purchaseRecordDAO;

    private BIAnalysisService biAnalysisService;

    @Before
    public void setUp() {
        biAnalysisService = new BIAnalysisService(purchaseRecordDAO);
    }

    @Test
    public void allB() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(20, "B", 1),
                new PurchaseRecord(40, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they never took B and I together.", result.getTotal(VALID_NO_COMED), is(1));
    }

    @Test
    public void allI() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(80, "I", 1),
                new PurchaseRecord(120, "I", 1),
                new PurchaseRecord(160, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they never took B and I together.", result.getTotal(VALID_NO_COMED), is(1));
    }

    @Test
    public void correctlyUsedDrugsWithGaps() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(40, "I", 1),
                new PurchaseRecord(140, "B", 1),
                new PurchaseRecord(180, "I", 1),
                new PurchaseRecord(280, "B", 1),
                new PurchaseRecord(320, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they never took B and I together.", result.getTotal(VALID_NO_COMED), is(1));
    }

    @Test
    public void BandIWithGapInBetween() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(20, "B", 1),
                new PurchaseRecord(40, "B", 1),
                new PurchaseRecord(80, "I", 1),
                new PurchaseRecord(150, "I", 1),
                new PurchaseRecord(220, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they never took B and I together.", result.getTotal(VALID_NO_COMED), is(1));
    }

    @Test
    public void clearViolation() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(20, "I", 1),
                new PurchaseRecord(50, "I", 1),
                new PurchaseRecord(120, "B", 1),
                new PurchaseRecord(140, "B", 1),
                new PurchaseRecord(160, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that violated by taking B and I together.", result.getTotal(VIOLATED), is(1));
    }

    @Test
    public void simpleSwitchBI() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(20, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Valid B to I switches should be 1.", result.getTotal(VALID_BI_SWITCH), is(1));
    }

    @Test
    public void simpleSwitchIB() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(70, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Valid I to B switches should be 1.", result.getTotal(VALID_IB_SWITCH), is(1));
    }

    @Test
    public void switchIB() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(60, "I", 1),
                new PurchaseRecord(130, "I", 1),
                new PurchaseRecord(180, "I", 1),
                new PurchaseRecord(210, "B", 1),
                new PurchaseRecord(230, "B", 1),
                new PurchaseRecord(250, "B", 1),
                new PurchaseRecord(265, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Valid I to B switches should be 1.", result.getTotal(VALID_IB_SWITCH), is(1));
    }

    @Test
    public void switchBI() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(25, "B", 1),
                new PurchaseRecord(50, "B", 1),
                new PurchaseRecord(70, "B", 1),
                new PurchaseRecord(80, "I", 1),
                new PurchaseRecord(150, "I", 1),
                new PurchaseRecord(230, "I", 1),
                new PurchaseRecord(310, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Valid B to I switches should be 1.", result.getTotal(VALID_BI_SWITCH), is(1));
    }

    @Test
    public void multipleSwitchesInOneSequence() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(60, "I", 1),
                new PurchaseRecord(120, "I", 1),
                new PurchaseRecord(160, "B", 1),
                new PurchaseRecord(180, "B", 1),
                new PurchaseRecord(200, "B", 1),
                new PurchaseRecord(220, "I", 1),
                new PurchaseRecord(270, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that violated by taking B and I together.", result.getTotal(VIOLATED), is(1));
    }

    @Test
    public void multipleSwitchesWithGap() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(70, "I", 1),
                new PurchaseRecord(130, "I", 1),
                new PurchaseRecord(190, "B", 1),
                new PurchaseRecord(210, "B", 1),
                new PurchaseRecord(250, "I", 1),
                new PurchaseRecord(320, "I", 1),
                new PurchaseRecord(390, "B", 1),
                new PurchaseRecord(410, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they switched from I to B.", result.getTotal(VALID_IB_SWITCH), is(1));
    }

    @Test
    public void multipleSwitchesAfterGap() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(70, "I", 1),
                new PurchaseRecord(130, "I", 1),
                new PurchaseRecord(190, "B", 1),
                new PurchaseRecord(210, "B", 1),
                new PurchaseRecord(250, "I", 1),
                new PurchaseRecord(320, "I", 1),
                new PurchaseRecord(390, "B", 1),
                new PurchaseRecord(410, "B", 1),
                new PurchaseRecord(430, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that violated by taking B and I together.", result.getTotal(VIOLATED), is(1));
    }

    @Test
    public void simpleTrialB() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(80, "B", 1),
                new PurchaseRecord(100, "I", 1),
                new PurchaseRecord(160, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they simply trialled B during I.", result.getTotal(VALID_B_TRIAL), is(1));
    }

    @Test
    public void simpleTrialI() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(20, "I", 1),
                new PurchaseRecord(60, "B", 1),
                new PurchaseRecord(80, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they simply trialled I during B.", result.getTotal(VALID_I_TRIAL), is(1));
    }

    @Test
    public void multipleTrialsInOneSequence() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(50, "I", 1),
                new PurchaseRecord(100, "B", 1),
                new PurchaseRecord(120, "I", 1),
                new PurchaseRecord(180, "I", 1),
                new PurchaseRecord(250, "I", 1),
                new PurchaseRecord(320, "I", 1),
                new PurchaseRecord(370, "B", 1),
                new PurchaseRecord(390, "I", 1),
                new PurchaseRecord(460, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that violated by taking B and I together.", result.getTotal(VIOLATED), is(1));
    }

    @Test
    public void multipleTrialsWithGap() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(80, "I", 1),
                new PurchaseRecord(160, "B", 1),
                new PurchaseRecord(180, "I", 1),
                new PurchaseRecord(260, "I", 1),
                new PurchaseRecord(360, "B", 1),
                new PurchaseRecord(380, "B", 1),
                new PurchaseRecord(400, "I", 1),
                new PurchaseRecord(480, "B", 1),
                new PurchaseRecord(500, "B", 1),
                new PurchaseRecord(520, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they simply trialled B during I.", result.getTotal(VALID_B_TRIAL), is(1));
    }

    @Test
    public void singleAlternateEnd() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(25, "B", 1),
                new PurchaseRecord(50, "B", 1),
                new PurchaseRecord(70, "B", 1),
                new PurchaseRecord(80, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Valid B to I switches should be 1.", result.getTotal(VALID_BI_SWITCH), is(1));
    }

    @Test
    public void singleAlternateStart() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(25, "B", 1),
                new PurchaseRecord(50, "B", 1),
                new PurchaseRecord(70, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Valid I to B switches should be 1.", result.getTotal(VALID_IB_SWITCH), is(1));
    }

    @Test
    public void PatientsByTypeTest() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(5, "B", 1),
                new PurchaseRecord(10, "B", 1),
                new PurchaseRecord(15, "B", 1)
        );

        List<PatientResult> result = biAnalysisService.getPatientsByType(VIOLATED);
        assertThat("There are no validating patients", result.size(), is(0));

        result = biAnalysisService.getPatientsByType(VALID_IB_SWITCH);
        assertThat("There is one valid IB switch patient", result.size(), is(1));
    }

    @Test
    public void PatientDataByIdTest() throws Exception {
        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(25, "B", 1),
                new PurchaseRecord(50, "B", 2),
                new PurchaseRecord(70, "B", 3)
        );

        Patient result = biAnalysisService.getPatientDataById(1);
        Assert.assertNotNull(result);
        assertThat("Patient 1 has 2 records", result.getRecords().size(), is(2));

        result = biAnalysisService.getPatientDataById(2);
        Assert.assertNotNull(result);
        assertThat("Patient 2 has 1 record", result.getRecords().size(), is(1));

        result = biAnalysisService.getPatientDataById(3);
        Assert.assertNotNull(result);
        assertThat("Patient 3 has 1 record", result.getRecords().size(), is(1));

        result = biAnalysisService.getPatientDataById(4);
        Assert.assertNull(result);

        result = biAnalysisService.getPatientDataById(4);
        Assert.assertNull(result);

    }

    private void setupPatients(PurchaseRecord... values) throws Exception {
        when(purchaseRecordDAO.allPurchaseRecords())
                .thenReturn(Arrays.asList(values));
    }
}