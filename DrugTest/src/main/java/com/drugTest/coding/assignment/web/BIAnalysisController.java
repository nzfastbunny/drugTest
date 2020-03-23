package com.drugTest.coding.assignment.web;

import com.drugTest.coding.assignment.domain.AnalysisResult;
import com.drugTest.coding.assignment.domain.Patient;
import com.drugTest.coding.assignment.domain.PatientResult;
import com.drugTest.coding.assignment.service.BIAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("bi")
public class BIAnalysisController {

    private final BIAnalysisService biAnalysisService;

    @Autowired
    public BIAnalysisController(BIAnalysisService biAnalysisService) {
        this.biAnalysisService = biAnalysisService;
    }

    @RequestMapping("/analysis")
    @ResponseBody
    public AnalysisResult analysis() throws Exception {
        return biAnalysisService.performBIAnalysis();
    }

    @RequestMapping("/patients")
    @ResponseBody
    public List<PatientResult> patients(@RequestParam String category) throws Exception {
        AnalysisResult.PatientType type = AnalysisResult.PatientType.getName(category);
        return biAnalysisService.getPatientsByType(type);
    }

    @RequestMapping("/patientdata")
    @ResponseBody
    public Patient patientData(@RequestParam int id) throws Exception {
        return biAnalysisService.getPatientDataById(id);
    }
}
