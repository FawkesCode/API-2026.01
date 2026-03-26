package com.fawkes.api.Models.entity;

import jakarta.persistence.*;

import lombok.*;

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
