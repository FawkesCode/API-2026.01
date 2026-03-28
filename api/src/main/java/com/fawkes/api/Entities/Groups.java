package com.fawkes.api.Entities;

import jakarta.persistence.*;

import lombok.*;

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
