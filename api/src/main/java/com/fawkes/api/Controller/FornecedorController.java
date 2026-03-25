package com.fawkes.api.Controller;

import com.fawkes.api.Models.Fornecedor;
import com.fawkes.api.Service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @GetMapping
    public ResponseEntity<List<Fornecedor>> listarTodos() {
        return ResponseEntity.ok(fornecedorService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody Fornecedor fornecedor) {
        fornecedorService.cadastrar(fornecedor);
        return ResponseEntity.ok("Fornecedor cadastrado com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editar(@PathVariable int id,
                                         @RequestBody Fornecedor fornecedor) {
        fornecedor.setId(id);
        fornecedorService.editar(fornecedor);
        return ResponseEntity.ok("Fornecedor atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable int id) {
        fornecedorService.deletar(id);
        return ResponseEntity.ok("Fornecedor deletado com sucesso!");
    }
}