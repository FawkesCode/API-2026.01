package com.fawkes.api.Models;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TBgroups")
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "groupName",nullable=false, unique=true)
    private String groupName;

    @Column(name="groupDescription",nullable=false,unique=true)
    private String groupDescription;
    
}
