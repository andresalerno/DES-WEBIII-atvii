package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Telefone;

import org.springframework.stereotype.Component;

@Component
public class TelefoneAtualizador {

    private StringVerificadorNulo verificador = new StringVerificadorNulo();

    private void atualizarDados(Telefone telefone, Telefone atualizacao) {
        if (!verificador.verificar(atualizacao.getDdd())) {
            telefone.setDdd(atualizacao.getDdd());
        }
        if (!verificador.verificar(atualizacao.getNumero())) {
            telefone.setNumero(atualizacao.getNumero());
        }
    }

    public void atualizar(Telefone telefone, Telefone atualizacao) {
        atualizarDados(telefone, atualizacao);
    }
}
