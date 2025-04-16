package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Documento;

import org.springframework.stereotype.Component;

@Component
public class DocumentoAtualizador {

    private StringVerificadorNulo verificador = new StringVerificadorNulo();

    private void atualizarDados(Documento documento, Documento atualizacao) {
        if (!verificador.verificar(atualizacao.getTipo())) {
            documento.setTipo(atualizacao.getTipo());
        }
        if (!verificador.verificar(atualizacao.getNumero())) {
            documento.setNumero(atualizacao.getNumero());
        }
    }

    public void atualizar(Documento documento, Documento atualizacao) {
        atualizarDados(documento, atualizacao);
    }
}
