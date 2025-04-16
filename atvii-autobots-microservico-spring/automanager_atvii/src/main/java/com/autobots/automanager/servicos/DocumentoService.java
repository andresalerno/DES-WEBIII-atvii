package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    public List<Documento> salvarDocumentos(List<Documento> documentos) {
        return documentoRepositorio.saveAll(documentos);
    }
}
