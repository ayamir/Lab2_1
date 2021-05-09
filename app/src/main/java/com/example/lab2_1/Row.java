package com.example.lab2_1;

public class Row {
    public static final int NON = 0;
    public static final int ADD = 1;
    public static final int UPD = 2;
    public static final int DEL = 3;
    private final int id;
    private boolean isModified;
    private String name;
    private String gender;
    private String department;
    private String salary;
    private int action;

    public Row(int id, String name, String gender, String department, String salary) {
        this.isModified = false;
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.department = department;
        this.salary = salary;
        this.action = NON;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public int getId() {
        return id;
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

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
