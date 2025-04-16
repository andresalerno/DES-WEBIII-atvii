package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.servicos.ClienteService;
import com.autobots.automanager.modelos.TelefoneAtualizador;
import com.autobots.automanager.modelos.TelefoneSelecionador;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes/{clienteId}/telefones")
public class TelefoneController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private TelefoneAtualizador telefoneAtualizador;  // Usando o TelefoneAtualizador para atualizar os dados do telefone

    @Autowired
    private TelefoneSelecionador telefoneSelecionador; // Usando o TelefoneSelecionador para selecionar um telefone pela lista

    // Listar todos os telefones de um cliente
    @GetMapping
    public ResponseEntity<List<EntityModel<Telefone>>> listarTelefones(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Telefone> telefones = cliente.getTelefones();
        if (telefones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Criando o EntityModel com links para cada telefone
        List<EntityModel<Telefone>> telefonesModel = telefones.stream()
                .map(telefone -> EntityModel.of(telefone,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).obterTelefone(clienteId, telefone.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).listarTelefones(clienteId)).withRel("telefones")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(telefonesModel);
    }

    // Buscar telefone por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Telefone>> obterTelefone(@PathVariable Long clienteId, @PathVariable Long id) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Telefone> telefones = cliente.getTelefones();
        Telefone telefone = telefoneSelecionador.selecionar(telefones, id);
        if (telefone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Criando o EntityModel com links para o telefone
        EntityModel<Telefone> telefoneModel = EntityModel.of(telefone,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).obterTelefone(clienteId, id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).listarTelefones(clienteId)).withRel("telefones"));

        return ResponseEntity.ok(telefoneModel);
    }

    // Criar um telefone para um cliente
    @PostMapping
    public ResponseEntity<EntityModel<Telefone>> criarTelefone(@PathVariable Long clienteId, @RequestBody Telefone telefone) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Adicionando o telefone ao cliente
        cliente.getTelefones().add(telefone);
        clienteService.criarCliente(cliente); // Salva o cliente com o novo telefone

        // Criando o EntityModel com links para o telefone recém-criado
        EntityModel<Telefone> telefoneModel = EntityModel.of(telefone,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).obterTelefone(clienteId, telefone.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).listarTelefones(clienteId)).withRel("telefones"));

        return ResponseEntity.status(HttpStatus.CREATED).body(telefoneModel);
    }

    // Atualizar um telefone de um cliente
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Telefone>> atualizarTelefone(@PathVariable Long clienteId, @PathVariable Long id, @RequestBody Telefone telefoneAtualizado) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Telefone> telefones = cliente.getTelefones();
        Telefone telefoneExistente = telefoneSelecionador.selecionar(telefones, id);
        if (telefoneExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Atualizando o telefone
        telefoneAtualizador.atualizar(telefoneExistente, telefoneAtualizado);
        clienteService.criarCliente(cliente); // Salva o cliente com o telefone atualizado

        // Criando o EntityModel com links para o telefone atualizado
        EntityModel<Telefone> telefoneModel = EntityModel.of(telefoneExistente,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).obterTelefone(clienteId, id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneController.class).listarTelefones(clienteId)).withRel("telefones"));

        return ResponseEntity.ok(telefoneModel);
    }

    // Deletar um telefone de um cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTelefone(@PathVariable Long clienteId, @PathVariable Long id) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Telefone> telefones = cliente.getTelefones();
        Telefone telefone = telefoneSelecionador.selecionar(telefones, id);
        if (telefone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Remover o telefone da lista de telefones do cliente
        telefones.remove(telefone);
        clienteService.criarCliente(cliente);

        return ResponseEntity.noContent().build();
    }
}
