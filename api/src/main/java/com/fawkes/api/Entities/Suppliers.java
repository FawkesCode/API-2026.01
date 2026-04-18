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

    @Column(name = "nome_fornecedor", length = 255)
    private String nomeFornecedor;

    @Column(name = "cnpj_fornecedor", unique = true, length = 20)
    private String cnpjFornecedor;

    @Convert(converter = PaymentConverter.class)
    @Column(columnDefinition = "SET('PIX', 'CREDITO', 'BOLETO', 'DEBITO')")
    private Set<PaymentMethod> meioPagamento;

}
