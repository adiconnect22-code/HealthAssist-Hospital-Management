package com.example.healthcare;

/** Static sample data used across Patient & Admin UI. */
public final class DoctorData {

    public final String name;
    public final String initials;
    public final String dept;
    public final String qualification;
    public final int years;
    public final String fee;
    public final boolean available;
    public final String[] days;

    private DoctorData(String name, String initials, String dept, String qualification,
                       int years, String fee, boolean available, String[] days) {
        this.name = name;
        this.initials = initials;
        this.dept = dept;
        this.qualification = qualification;
        this.years = years;
        this.fee = fee;
        this.available = available;
        this.days = days;
    }

    public static final DoctorData[] ALL = {
            new DoctorData("Dr. Sarojini Rao", "SR", "Cardiology", "M.D. Cardiology, MBBS", 22,
                    "\u20B9600", true, new String[]{"Mon", "Wed", "Fri"}),
            new DoctorData("Dr. K. V. Mehta", "KM", "Orthopedics", "MS Orthopedics, M.Ch", 25,
                    "\u20B9500", true, new String[]{"Tue", "Thu", "Sat"}),
            new DoctorData("Dr. Sunita Iyer", "SI", "Dermatology", "MD Dermatology, DVD", 16,
                    "\u20B9550", true, new String[]{"Mon", "Tue", "Sat"}),
            new DoctorData("Dr. Rajesh Sharma", "RS", "Neurology", "M.Ch Neurology, MBBS", 20,
                    "\u20B9800", true, new String[]{"Wed", "Thu", "Fri"}),
    };

    public static DoctorData find(String name) {
        if (name != null) {
            for (DoctorData d : ALL) {
                if (d.name.equalsIgnoreCase(name) || name.contains(d.name)) return d;
            }
        }
        return ALL[0];
    }
}
