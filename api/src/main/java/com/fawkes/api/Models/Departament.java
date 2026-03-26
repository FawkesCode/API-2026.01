package com.fawkes.api.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TBdepartament")
public class Departament {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="departamentName",nullable=false)
    private String departamentName;

    @Column(name="text",nullable=false)
    private String text;

}
