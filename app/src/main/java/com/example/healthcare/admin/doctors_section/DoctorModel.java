package com.example.healthcare.admin.doctors_section;

import java.io.Serializable;

/**
 * Model class in 'doctors_section' folder matching the exact Firestore schema.
 */
public class DoctorModel implements Serializable {

    private String id;
    private String name;
    private String department;
    private String specialization;
    private String qualification;
    private String hospitalName;
    private String location;
    private String experienceGender;
    private String contactNumber;
    private String email;
    private String feeCapacity;
    private String timeSlots;
    private String aboutSummary;
    private String imageResName;
    private String leaveStartDate;
    private String leaveEndDate;
    private String leaveReason;
    private boolean isUnavailable;

    public DoctorModel() {
    }

    public DoctorModel(String id, String name, String specialization, String qualification,
                       String hospitalName, String location, String contactNumber, String email,
                       String imageResName, boolean isUnavailable) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.department = specialization;
        this.qualification = qualification;
        this.hospitalName = hospitalName;
        this.location = location;
        this.contactNumber = contactNumber;
        this.email = email;
        this.imageResName = imageResName;
        this.isUnavailable = isUnavailable;
        this.feeCapacity = "10/day";
        this.timeSlots = "10:00 AM - 02:00 PM";
        this.aboutSummary = "Experienced medical specialist at " + hospitalName + " (" + location + ").";
    }

    public DoctorModel(String id, String name, String department, String specialization,
                       String qualification, String experienceGender, String contactNumber,
                       String feeCapacity, String timeSlots, String aboutSummary, String imageResName) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceGender = experienceGender;
        this.contactNumber = contactNumber;
        this.feeCapacity = feeCapacity;
        this.timeSlots = timeSlots;
        this.aboutSummary = aboutSummary;
        this.imageResName = imageResName;
        this.hospitalName = "City Care Hospital";
        this.location = "Bengaluru";
        this.isUnavailable = false;
    }

    public boolean isOnLeaveToday() {
        return isUnavailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getHospitalName() {
        return (hospitalName != null && !hospitalName.isEmpty()) ? hospitalName : "City Care Hospital";
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getLocation() {
        return (location != null && !location.isEmpty()) ? location : "Bengaluru";
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExperienceGender() {
        return experienceGender;
    }

    public void setExperienceGender(String experienceGender) {
        this.experienceGender = experienceGender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeeCapacity() {
        return feeCapacity;
    }

    public void setFeeCapacity(String feeCapacity) {
        this.feeCapacity = feeCapacity;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(String timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getAboutSummary() {
        return aboutSummary;
    }

    public void setAboutSummary(String aboutSummary) {
        this.aboutSummary = aboutSummary;
    }

    public String getImageResName() {
        return imageResName;
    }

    public void setImageResName(String imageResName) {
        this.imageResName = imageResName;
    }

    public String getLeaveStartDate() {
        return leaveStartDate;
    }

    public void setLeaveStartDate(String leaveStartDate) {
        this.leaveStartDate = leaveStartDate;
    }

    public String getLeaveEndDate() {
        return leaveEndDate;
    }

    public void setLeaveEndDate(String leaveEndDate) {
        this.leaveEndDate = leaveEndDate;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public boolean isUnavailable() {
        return isUnavailable;
    }

    public void setUnavailable(boolean unavailable) {
        isUnavailable = unavailable;
    }
}
