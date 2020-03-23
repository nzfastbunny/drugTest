var Analysis = (function () {

    var _biAnalysisEndpoint = '/bi/analysis';

    function Analysis() {
        // No-op
    }

    Analysis.prototype = {
        getBIAnalysis: function (success, failure) {
            $.getJSON(_biAnalysisEndpoint,
                function callback(result, status, xhr) {
                    if ("success" === status) {
                        success(result);
                    } else {
                        failure(result);
                    }
                }
            );
        }
    };

    return new Analysis();
})();

var Patients = (function () {

    var _patientsEndpoint = '/bi/patients';

    function Patients(category) {
    }

    Patients.prototype = {
        getPatients: function (category, success, failure) {
            $.getJSON(_patientsEndpoint, "category=" + category,
                function callback(result, status, xhr) {
                    if ("success" === status) {
                        success(result);
                    } else {
                        failure(result);
                    }
                }
            );
        }
    };

    return new Patients();
})();

var PatientData = (function () {

    var _patientDataEndpoint = '/bi/patientdata';

    function PatientData(category) {
    }

    PatientData.prototype = {
        getPatientData: function (id, success, failure) {
            $.getJSON(_patientDataEndpoint, "id=" + id,
                function callback(result, status, xhr) {
                    if ("success" === status) {
                        success(result);
                    } else {
                        failure(result);
                    }
                }
            );
        }
    };

    return new PatientData();
})();