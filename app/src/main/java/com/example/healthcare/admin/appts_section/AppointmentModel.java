package com.example.healthcare.admin.appts_section;

import java.io.Serializable;

/**
 * Model class for Hospital Appointments stored in Firebase Cloud Firestore.
 */
public class AppointmentModel implements Serializable {

    private String id;
    private String patientName;
    private String patientPhone;
    private String doctorName;
    private String department;
    private String date;
    private String timeSlot;
    private String status; // "Pending", "Confirmed", "Upcoming", "Completed", "Cancelled", "Rejected"
    private String reason;
    private String rejectionReason;
    private long cancelledTimestamp;

    public AppointmentModel() {
    }

    public AppointmentModel(String id, String patientName, String patientPhone, String doctorName,
                            String department, String date, String timeSlot, String status, String reason) {
        this.id = id;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.doctorName = doctorName;
        this.department = department;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.reason = reason;
        this.cancelledTimestamp = 0L;
    }

    public AppointmentModel(String patientName, String doctorName, String department, String date, String status) {
        this("APT-" + System.currentTimeMillis(), patientName, "+91 9876543210", doctorName, department, date, "10:30 AM", status, "General Medical Checkup");
    }

    public String getId() {
        return (id != null && !id.isEmpty()) ? id : "APT-" + System.currentTimeMillis();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return (patientName != null && !patientName.isEmpty()) ? patientName : "Patient";
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return (patientPhone != null && !patientPhone.isEmpty()) ? patientPhone : "+91 9876543210";
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getDoctorName() {
        return (doctorName != null && !doctorName.isEmpty()) ? doctorName : "Dr. Rahul Sharma";
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDepartment() {
        return (department != null && !department.isEmpty()) ? department : "General Medicine";
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDate() {
        return (date != null && !date.isEmpty()) ? date : "Today";
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return (timeSlot != null && !timeSlot.isEmpty()) ? timeSlot : "10:30 AM";
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return (status != null && !status.isEmpty()) ? status : "Pending";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return (reason != null && !reason.isEmpty()) ? reason : "General Medical Checkup";
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public long getCancelledTimestamp() {
        return cancelledTimestamp;
    }

    public void setCancelledTimestamp(long cancelledTimestamp) {
        this.cancelledTimestamp = cancelledTimestamp;
    }
}
