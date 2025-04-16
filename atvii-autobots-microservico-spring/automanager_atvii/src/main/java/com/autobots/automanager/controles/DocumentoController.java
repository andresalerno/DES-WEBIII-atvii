package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.servicos.ClienteService;
import com.autobots.automanager.modelos.DocumentoAtualizador;
import com.autobots.automanager.modelos.DocumentoSelecionador;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes/{clienteId}/documentos")
public class DocumentoController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private DocumentoAtualizador documentoAtualizador;  // Usando o DocumentoAtualizador para atualizar os dados do documento

    @Autowired
    private DocumentoSelecionador documentoSelecionador; // Usando o DocumentoSelecionador para selecionar um documento pela lista

    // Listar todos os documentos de um cliente
    @GetMapping
    public ResponseEntity<List<EntityModel<Documento>>> listarDocumentos(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Documento> documentos = cliente.getDocumentos();
        if (documentos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Criando o EntityModel com links para cada documento
        List<EntityModel<Documento>> documentosModel = documentos.stream()
                .map(documento -> EntityModel.of(documento,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).obterDocumento(clienteId, documento.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).listarDocumentos(clienteId)).withRel("documentos")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(documentosModel);
    }

    // Buscar documento por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Documento>> obterDocumento(@PathVariable Long clienteId, @PathVariable Long id) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Documento> documentos = cliente.getDocumentos();
        Documento documento = documentoSelecionador.selecionar(documentos, id);
        if (documento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Criando o EntityModel com links para o documento
        EntityModel<Documento> documentoModel = EntityModel.of(documento,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).obterDocumento(clienteId, id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).listarDocumentos(clienteId)).withRel("documentos"));

        return ResponseEntity.ok(documentoModel);
    }

    // Criar um documento para um cliente
    @PostMapping
    public ResponseEntity<EntityModel<Documento>> criarDocumento(@PathVariable Long clienteId, @RequestBody Documento documento) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Adicionando o documento ao cliente
        cliente.getDocumentos().add(documento);
        clienteService.criarCliente(cliente); // Salva o cliente com o novo documento
        
        documento.setId(cliente.getDocumentos().get(cliente.getDocumentos().size() - 1).getId());

        // Criando o EntityModel com links para o documento recém-criado
        EntityModel<Documento> documentoModel = EntityModel.of(documento,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).obterDocumento(clienteId, documento.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).listarDocumentos(clienteId)).withRel("documentos"));

        return ResponseEntity.status(HttpStatus.CREATED).body(documentoModel);
    }

    // Atualizar um documento de um cliente
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Documento>> atualizarDocumento(@PathVariable Long clienteId, @PathVariable Long id, @RequestBody Documento documentoAtualizado) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Documento> documentos = cliente.getDocumentos();
        Documento documentoExistente = documentoSelecionador.selecionar(documentos, id);
        if (documentoExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Atualizando o documento
        documentoAtualizador.atualizar(documentoExistente, documentoAtualizado);
        clienteService.criarCliente(cliente); // Salva o cliente com o documento atualizado

        // Criando o EntityModel com links para o documento atualizado
        EntityModel<Documento> documentoModel = EntityModel.of(documentoExistente,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).obterDocumento(clienteId, id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoController.class).listarDocumentos(clienteId)).withRel("documentos"));

        return ResponseEntity.ok(documentoModel);
    }

    // Deletar um documento de um cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDocumento(@PathVariable Long clienteId, @PathVariable Long id) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Documento> documentos = cliente.getDocumentos();
        Documento documento = documentoSelecionador.selecionar(documentos, id);
        if (documento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Remover o documento da lista de documentos do cliente
        documentos.remove(documento);
        clienteService.criarCliente(cliente); // Salva o cliente com o documento removido

        return ResponseEntity.noContent().build(); // Status 204, sem corpo
    }
}
