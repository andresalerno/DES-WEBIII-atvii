package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.servicos.ClienteService;
import com.autobots.automanager.modelos.AdicionadorLinkCliente;
import com.autobots.automanager.modelos.ClienteAtualizador;
import com.autobots.automanager.modelos.ClienteSelecionador;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AdicionadorLinkCliente adicionadorLinkCliente;

    @Autowired
    private ClienteAtualizador clienteAtualizador;

    @Autowired
    private ClienteSelecionador clienteSelecionador;

    // Listar todos os clientes
    @GetMapping
    public ResponseEntity<List<EntityModel<Cliente>>> listarClientes() {
        List<Cliente> clientes = clienteService.listarClientes();
        if (clientes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Adicionando links a cada cliente
        adicionadorLinkCliente.adicionarLink(clientes);

        // Criando o EntityModel com links para cada cliente
        List<EntityModel<Cliente>> clientesModel = clientes.stream()
                .map(cliente -> EntityModel.of(cliente,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).obterCliente(cliente.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).listarClientes()).withRel("clientes")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientesModel);
    }

    // Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> obterCliente(@PathVariable Long id) {
        // Usando ClienteSelecionador para selecionar o cliente
        List<Cliente> clientes = clienteService.listarClientes(); // Supondo que você tenha uma lista de clientes
        Cliente cliente = clienteSelecionador.selecionar(clientes, id);

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Adicionando links ao cliente
        adicionadorLinkCliente.adicionarLink(cliente);

        // Criando o EntityModel com links para o cliente
        EntityModel<Cliente> clienteModel = EntityModel.of(cliente,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).obterCliente(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).listarClientes()).withRel("clientes"));

        return ResponseEntity.ok(clienteModel);
    }

    // Criação do cliente
    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> criarCliente(@RequestBody Cliente cliente) {
        Cliente clienteCriado = clienteService.criarCliente(cliente);
        adicionadorLinkCliente.adicionarLink(clienteCriado);

        EntityModel<Cliente> clienteModel = EntityModel.of(clienteCriado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).obterCliente(clienteCriado.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(clienteModel);
    }

    // Atualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> atualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        // Usando ClienteSelecionador para selecionar o cliente pela lista
        List<Cliente> clientes = clienteService.listarClientes();
        Cliente clienteSelecionado = clienteSelecionador.selecionar(clientes, id);

        if (clienteSelecionado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Usando ClienteAtualizador para atualizar os dados do cliente
        clienteAtualizador.atualizar(clienteSelecionado, cliente);
        clienteService.criarCliente(clienteSelecionado);

        // Adicionando links ao cliente atualizado
        adicionadorLinkCliente.adicionarLink(clienteSelecionado);

        EntityModel<Cliente> clienteModel = EntityModel.of(clienteSelecionado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).obterCliente(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).listarClientes()).withRel("clientes"));

        return ResponseEntity.ok(clienteModel);
    }

    // Deletar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        boolean clienteDeletado = clienteService.deletarCliente(id);
        if (clienteDeletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
