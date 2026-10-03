package com.example.healthcare.doctors_section;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model class in 'doctors_section' folder.
 */
public class DoctorModel implements Serializable {

    private String id;
    private String name;
    private String department;
    private String specialization;
    private String qualification;
    private String experienceGender;
    private String contactNumber;
    private String feeCapacity;
    private String timeSlots;
    private String leaveStartDate;
    private String leaveEndDate;
    private String leaveReason;
    private boolean isUnavailable;

    public DoctorModel() {
    }

    public DoctorModel(String id, String name, String department, String specialization,
                       String qualification, String experienceGender, String contactNumber,
                       String feeCapacity, String timeSlots) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceGender = experienceGender;
        this.contactNumber = contactNumber;
        this.feeCapacity = feeCapacity;
        this.timeSlots = timeSlots;
        this.isUnavailable = false;
    }

    public String getInitials() {
        if (name == null || name.trim().isEmpty()) return "DR";
        String cleanName = name.replace("Dr.", "").replace("Dr", "").trim();
        String[] parts = cleanName.split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        } else if (cleanName.length() >= 2) {
            return cleanName.substring(0, 2).toUpperCase(Locale.ROOT);
        } else {
            return cleanName.toUpperCase(Locale.ROOT);
        }
    }

    public boolean isOnLeaveToday() {
        if (isUnavailable) return true;
        if (leaveStartDate == null || leaveEndDate == null || leaveStartDate.isEmpty() || leaveEndDate.isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date today = new Date();
            Date start = sdf.parse(leaveStartDate);
            Date end = sdf.parse(leaveEndDate);

            if (start != null && end != null) {
                // Strip time components for accurate date comparison
                SimpleDateFormat daySdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                long todayLong = Long.parseLong(daySdf.format(today));
                long startLong = Long.parseLong(daySdf.format(start));
                long endLong = Long.parseLong(daySdf.format(end));

                return todayLong >= startLong && todayLong <= endLong;
            }
        } catch (Exception e) {
            // Fallback for simple date comparisons
            return true;
        }
        return false;
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
