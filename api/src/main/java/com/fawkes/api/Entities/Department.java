package com.fawkes.api.Entities;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TBdepartament")
public class Department {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="departamentName",nullable=false)
    private String departamentName;

    @Column(name="text",nullable=false)
    private String text;

}
