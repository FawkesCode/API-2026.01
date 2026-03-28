package com.fawkes.api.Models.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TBpermissions")
public class Permissions {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="permissionName")
    private String permissionName;

    @Column(name="permissionDescription",nullable=false,unique=true)
    private String permissionDescription;

}
