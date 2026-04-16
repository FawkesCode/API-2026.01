package com.fawkes.front.models;

import javafx.scene.image.Image;

/*
  Espelha a entidade Users do back-end (TBusers).
  Campos: id, userName, userMail, isActive, creationDate, group, departments.
  O campo picture não vem do back — é carregado localmente ou deixado nulo.
*/

public class Employee {

    private Long id;
    private String name;
    private String email;
    private String status;
    private String department;
    private String position;
    private String signed;
    private Image picture;

    public Employee() {}

    public Employee(Long id, String name, String email, String status,
                    String department, String position, String signed, Image picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.department = department;
        this.position = position;
        this.signed = signed;
        this.picture = picture;
    }

    // Constrói um Employee a partir do JsonNode retornado pelo back
    // Espera campos: id, userName, userMail, isActive, creationDate,
    //                departments.departmentName, group.groupName
    public static Employee fromJson(com.fasterxml.jackson.databind.JsonNode node) {
        Long id = node.path("id").asLong();
        String name = node.path("userName").asText("-");
        String email = node.path("userMail").asText("-");
        boolean active = node.path("isActive").asBoolean(true);
        String status = active ? "Ativo" : "Inativo";

        String department = node.path("departamentName").asText("-");
        String position = node.path("groupName").asText("-");

        String rawDate = node.path("creationDate").asText("");
        String signed = formatDate(rawDate);

        return new Employee(id, name, email, status, department, position, signed, null);
    }

    /** Converte "2026-03-19T10:30:00" em "19 de Mar. 2026" */
    private static String formatDate(String iso) {
        if (iso == null || iso.isBlank()) return "-";
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(
                    iso.length() > 19 ? iso.substring(0, 19) : iso);
            String[] months = {"Jan.", "Fev.", "Mar.", "Abr.", "Mai.", "Jun.",
                    "Jul.", "Ago.", "Set.", "Out.", "Nov.", "Dez."};
            return dt.getDayOfMonth() + " de " + months[dt.getMonthValue() - 1]
                    + " " + dt.getYear();
        } catch (Exception e) {
            return iso;
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getSigned() { return signed; }
    public void setSigned(String signed) { this.signed = signed; }

    public Image getPicture() { return picture; }
    public void setPicture(Image picture) { this.picture = picture; }

    @Override
    public String toString() {
        return "Employee [nome=" + name + ", id=" + id + ", setor=" + department + ", posição=" + position + "]";
    }
}