package com.autobots.automanager.servicos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepositorio enderecoRepositorio;

    public Endereco salvarEndereco(Endereco endereco) {
        return enderecoRepositorio.save(endereco);
    }
}
