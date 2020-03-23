package com.drugTest.coding.assignment.service;

import com.drugTest.coding.assignment.data.PurchaseRecordDAO;
import com.drugTest.coding.assignment.domain.AnalysisResult;
import com.drugTest.coding.assignment.domain.AnalysisResult.PatientType;
import com.drugTest.coding.assignment.domain.Patient;
import com.drugTest.coding.assignment.domain.PatientResult;
import com.drugTest.coding.assignment.domain.PurchaseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.drugTest.coding.assignment.domain.AnalysisResult.PatientType.*;

@Component
public class BIAnalysisService {
    private final PurchaseRecordDAO purchaseRecordDAO;

    @Autowired
    public BIAnalysisService(PurchaseRecordDAO purchaseRecordDAO) {
        this.purchaseRecordDAO = purchaseRecordDAO;
    }

    /**
     * Part 1 - categorise the patients into different categories (types) based on use of B and I drugs
     *
     * @return the analysis result of patient's use of B and I drugs
     * @throws Exception an exception if something goes wrong (most likely the loading of the file)
     */
    public AnalysisResult performBIAnalysis() throws Exception {
        HashMap<PatientType, List<Patient>> categories = getPatientTypeMap();

        // then put real results in here
        AnalysisResult result = new AnalysisResult();
        result.putTotal(VIOLATED, getSize(categories, VIOLATED));
        result.putTotal(VALID_NO_COMED, getSize(categories, VALID_NO_COMED));
        result.putTotal(VALID_BI_SWITCH, getSize(categories, VALID_BI_SWITCH));
        result.putTotal(VALID_IB_SWITCH, getSize(categories, VALID_IB_SWITCH));
        result.putTotal(VALID_I_TRIAL, getSize(categories, VALID_I_TRIAL));
        result.putTotal(VALID_B_TRIAL, getSize(categories, VALID_B_TRIAL));
        return result;
    }

    /**
     * Part 2 - get the list of patients for the supplied category (type)
     *
     * @param type the patient type (category)
     * @return the list of patients that are in the patient type (category) supplied
     * @throws Exception an exception if something goes wrong (most likely the loading of the file)
     */
    public List<PatientResult> getPatientsByType(PatientType type) throws Exception {
        List<PatientResult> patientResults = new ArrayList<>();

        List<Patient> patients = getPatientTypeMap().getOrDefault(type, new ArrayList<>());

        for (Patient patient : patients) {
            PatientResult result = new PatientResult(patient.getId());
            result.setRecord(patient.printRecord());
            patientResults.add(result);
        }

        return patientResults;
    }

    /**
     * Part 3 - get the patient details of a selected patient to be charted
     *
     * @param id the id of the selected patient
     * @return the patient record containing the data required to be charted
     * @throws Exception an exception if something goes wrong (most likely the loading of the file)
     */
    public Patient getPatientDataById(int id) throws Exception {
        return getPatientMap().get(id);
    }

    /**
     * Load and process the patient records to sort them into types (categories)
     *
     * @return the map of the patients sorted into types
     * @throws Exception an exception if something goes wrong (most likely the loading of the file)
     */
    private HashMap<PatientType, List<Patient>> getPatientTypeMap() throws Exception {
        HashMap<Integer, Patient> patients = getPatientMap();

        // Categorise the patients into types
        HashMap<PatientType, List<Patient>> categories = new HashMap<>();
        for (Patient patient : patients.values()) {
            if (!categories.containsKey(patient.getPatientType())) {
                categories.put(patient.getPatientType(), new ArrayList<>());
            }
            categories.get(patient.getPatientType()).add(patient);
        }

        return categories;
    }

    private HashMap<Integer, Patient> getPatientMap() throws Exception {
        // Load the records
        List<PurchaseRecord> purchaseRecords = purchaseRecordDAO.allPurchaseRecords();

        // Order the patient records together
        HashMap<Integer, Patient> patients = new HashMap<>();
        for (PurchaseRecord record : purchaseRecords) {
            int patientId = record.getPatientId();
            if (!patients.containsKey(patientId)) {
                patients.put(patientId, new Patient(patientId));
            }
            patients.get(patientId).getRecords().add(record);
        }

        for (Patient patient : patients.values()) {
            patient.sortRecords();
            // Decide the category (type) of the patient
            processPatientRecords(patient);
        }

        return patients;
    }

    private int getSize(HashMap<PatientType, List<Patient>> categories, PatientType type) {
        return categories.get(type) == null ? 0 : categories.get(type).size();
    }

    private void processPatientRecords(Patient patient) {
        PatientType overall = VALID_NO_COMED;
        PatientType streak = VALID_NO_COMED;
        String previousDrug = "";
        int previousEndDate = 0;

        for (PurchaseRecord record : patient.getRecords()) {
            if (!previousDrug.isEmpty()) {
                switch (overall) {
                    case VALID_NO_COMED:
                        if (!record.getMedication().equalsIgnoreCase(previousDrug) && record.getDay() < previousEndDate) {
                            if (record.getMedication().equalsIgnoreCase("I")) {
                                overall = VALID_BI_SWITCH;
                                record.setNewCategory(VALID_BI_SWITCH.name());
                            } else {
                                overall = VALID_IB_SWITCH;
                                record.setNewCategory(VALID_IB_SWITCH.name());
                            }
                        }
                        break;
                    case VALID_B_TRIAL:
                        if (record.getDay() < previousEndDate) {
                            if (!previousDrug.equals(record.getMedication())) {
                                if (streak == VALID_B_TRIAL) {
                                    overall = VIOLATED;
                                    record.setNewCategory(VIOLATED.name());
                                } else if (streak != VALID_I_TRIAL) {
                                    streak = VALID_B_TRIAL;
                                }
                            }
                        } else {
                            previousDrug = "";
                            streak = VALID_I_TRIAL;
                        }
                        break;
                    case VALID_I_TRIAL:
                        if (record.getDay() < previousEndDate) {
                            if (!previousDrug.equals(record.getMedication())) {
                                if (streak != VALID_NO_COMED) {
                                    overall = VIOLATED;
                                    record.setNewCategory(VIOLATED.name());
                                } else if (streak != VALID_B_TRIAL) {
                                    streak = VALID_I_TRIAL;
                                }
                            }
                        } else {
                            previousDrug = "";
                            streak = VALID_B_TRIAL;
                        }
                        break;
                    case VALID_BI_SWITCH:
                        if (record.getDay() < previousEndDate) {
                            if (!previousDrug.equals(record.getMedication())) {
                                if (streak == VALID_BI_SWITCH) {
                                    overall = VIOLATED;
                                    record.setNewCategory(VIOLATED.name());
                                } else {
                                    if (streak != VALID_IB_SWITCH) {
                                        overall = VALID_I_TRIAL;
                                        record.setNewCategory(VALID_I_TRIAL.name());
                                    } else {
                                        streak = VALID_BI_SWITCH;
                                    }
                                    streak = VALID_I_TRIAL;
                                }
                            } else if (streak != VALID_IB_SWITCH) {
                                streak = VALID_BI_SWITCH;
                            }
                        } else {
                            previousDrug = "";
                            streak = VALID_IB_SWITCH;
                        }
                        break;
                    case VALID_IB_SWITCH:
                        if (record.getDay() < previousEndDate) {
                            if (!previousDrug.equals(record.getMedication())) {
                                if (streak == VALID_IB_SWITCH) {
                                    overall = VIOLATED;
                                    record.setNewCategory(VIOLATED.name());
                                } else {
                                    if (streak != VALID_BI_SWITCH) {
                                        overall = VALID_B_TRIAL;
                                        record.setNewCategory(VALID_B_TRIAL.name());
                                    } else {
                                        streak = VALID_IB_SWITCH;
                                    }
                                    streak = VALID_B_TRIAL;
                                }
                            } else if (streak != VALID_BI_SWITCH) {
                                streak = VALID_IB_SWITCH;
                            }
                        } else {
                            previousDrug = "";
                            streak = VALID_BI_SWITCH;
                        }
                        break;
                    default:
                        // Already violated (no processing needed)
                }
            }

            previousDrug = record.getMedication();
            previousEndDate = getEndDate(record);
        }

        patient.setPatientType(overall);
    }

    private int getEndDate(PurchaseRecord record) {
        int daysActive = 30; // Use B as the default
        if (record.getMedication().equalsIgnoreCase("I")) {
            daysActive = 90;
        }

        return record.getDay() + daysActive;
    }
}
