package com.example.healthcare.appts_section;

import java.io.Serializable;

/**
 * Model class in 'appts_section' folder.
 */
public class AppointmentModel implements Serializable {

    private String patientName;
    private String doctorName;
    private String department;
    private String date;
    private String status;

    public AppointmentModel() {
    }

    public AppointmentModel(String patientName, String doctorName, String department, String date, String status) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.department = department;
        this.date = date;
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
