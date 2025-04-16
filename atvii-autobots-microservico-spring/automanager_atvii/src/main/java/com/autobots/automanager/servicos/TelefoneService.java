package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelos.TelefoneAtualizador;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@Service
public class TelefoneService {

    @Autowired
    private TelefoneRepositorio telefoneRepositorio;

    public List<Telefone> salvarTelefones(List<Telefone> telefones) {
        return telefoneRepositorio.saveAll(telefones);
    }
}
