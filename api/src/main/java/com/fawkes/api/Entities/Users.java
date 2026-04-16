package com.fawkes.api.Entities;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.stream.Collectors.toList;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBusers")
public class Users {

    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated (EnumType.STRING)
    @Column(name = "role")
    private Set<Roles> roles = new HashSet<>();
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.name()))
                    .toList();
    }
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


    public void setRoles(Set<Roles> role) {
        this.roles = role;
    }
}
