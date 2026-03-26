package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissoes")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_permissao", nullable = false, unique = true, length = 255)
    private String nomePermissao;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @ManyToMany(mappedBy = "permissoes", fetch = FetchType.LAZY)
    private Set<Grupo> grupos = new HashSet<>();

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomePermissao() { return nomePermissao; }
    public void setNomePermissao(String nomePermissao) { this.nomePermissao = nomePermissao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Set<Grupo> getGrupos() { return grupos; }
    public void setGrupos(Set<Grupo> grupos) { this.grupos = grupos; }
}
