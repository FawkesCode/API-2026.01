package com.fawkes.api.Entities;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TBgroups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "groupName",nullable=false, unique=true)
    private Roles role = Roles.OPERATIONAL;

    @Column(name="groupDescription",nullable=false,unique=true)
    private String groupDescription;
    
}
