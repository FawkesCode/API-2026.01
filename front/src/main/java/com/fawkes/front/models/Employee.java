package com.fawkes.front.models;

import javafx.scene.image.Image;

public class Employee {
    private String status;
    private String department;
    private Image picture;
    private String position;
    private String name;
    private String email;
    private String signed;

    public Employee(String status, String department, Image picture, String position, String name, String email, String signed) {
        this.setStatus(status);
        this.setDepartment(department);
        this.setPicture(picture);
        this.setPosition(position);
        this.setName(name);
        this.setEmail(email);
        this.setSigned(signed);
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Image getPicture() {
        return picture;
    }

    public void setPicture(Image picture) {
        this.picture = picture;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSigned() {
        return signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }
}
