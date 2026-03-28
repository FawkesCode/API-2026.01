package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import java.lang.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orderNote")
public class OrderNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "noteNumber", unique = true, length = 45, columnDefinition = "CHAR(45)")
    private String numberNote;

    @Column(name = "serie", length = 20)
    private String serie;

    @Column(name = "orderNoteDate")
    private LocalDate orderNoteDate;

}
