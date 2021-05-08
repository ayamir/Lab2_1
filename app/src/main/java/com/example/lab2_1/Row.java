package com.example.lab2_1;

public class Row {
    private boolean isModified;
    private String id;
    private String name;
    private String gender;
    private String department;
    private String salary;
    public static final int ADD = 0;
    public static final int UPD = 1;
    public static final int DEL = 2;

    public Row(String id, String name, String gender, String department, String salary) {
        this.isModified = false;
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.department = department;
        this.salary = salary;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
