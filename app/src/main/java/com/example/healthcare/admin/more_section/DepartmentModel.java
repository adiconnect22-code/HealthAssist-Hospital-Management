package com.example.healthcare.admin.more_section;

import java.io.Serializable;

/**
 * Model class in 'more_section' folder.
 */
public class DepartmentModel implements Serializable {

    private String id;
    private String name;
    private String floor;
    private String headDoctor;
    private String status;

    public DepartmentModel() {
    }

    public DepartmentModel(String id, String name, String floor, String headDoctor, String status) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.headDoctor = headDoctor;
        this.status = status;
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

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getHeadDoctor() {
        return headDoctor;
    }

    public void setHeadDoctor(String headDoctor) {
        this.headDoctor = headDoctor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
