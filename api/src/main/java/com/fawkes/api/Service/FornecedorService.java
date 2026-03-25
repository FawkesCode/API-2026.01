package com.fawkes.api.Service;

import com.fawkes.api.Models.Fornecedor;
import com.fawkes.api.Repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public void cadastrar(Fornecedor fornecedor) {
        fornecedorRepository.save(fornecedor);
    }

    public void editar(Fornecedor fornecedor) {
        fornecedorRepository.save(fornecedor);
    }

    public void deletar(int id) {
        fornecedorRepository.deleteById(id);
    }
}