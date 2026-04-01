package com.fawkes.api.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBusers")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username",unique=true, nullable=false)
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name ="usermail",unique=true, nullable=false)
    private String userMail;

    @Column(name = "isActive",nullable=false)
    @ColumnDefault("true")
    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name ="groupID",nullable=false)
    private Group group;

    @ManyToOne
    @JoinColumn(name="departamentID",nullable=false)
    private Departments departments;




}
