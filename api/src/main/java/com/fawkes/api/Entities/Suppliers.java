package com.fawkes.api.Entities;
import com.fawkes.api.Helpers.PaymentConverter;
import jakarta.persistence.*;
import lombok.*;
import java.lang.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBsupplier") 
public class Suppliers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_name", length = 255)
    private String supplierName;

    @Column(name = "cnpj", unique = true, length = 20)
    private String cnpj;

    @Convert(converter = PaymentConverter.class)
    @Column(columnDefinition = "SET('PIX', 'CREDITO', 'BOLETO', 'DEBITO')")
    private Set<PaymentMethod> paymentMethods;

    @Column(name = "isActive")
    private Boolean isActive;

}
