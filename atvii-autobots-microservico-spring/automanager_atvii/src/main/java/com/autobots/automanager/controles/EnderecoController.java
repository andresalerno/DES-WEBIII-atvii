package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.servicos.ClienteService;
import com.autobots.automanager.servicos.EnderecoService;
import com.autobots.automanager.modelos.EnderecoAtualizador;

@RestController
@RequestMapping("/clientes/{clienteId}/endereco")
public class EnderecoController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private EnderecoAtualizador enderecoAtualizador;

    // Buscar o endereço de um cliente
    @GetMapping
    public ResponseEntity<EntityModel<Endereco>> obterEndereco(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Endereco endereco = cliente.getEndereco();
        if (endereco == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Criando o EntityModel com links para o endereço
        EntityModel<Endereco> enderecoModel = EntityModel.of(endereco,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EnderecoController.class).obterEndereco(clienteId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).obterCliente(clienteId)).withRel("cliente"));

        return ResponseEntity.ok(enderecoModel);
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Endereco>> criarEndereco(@PathVariable Long clienteId, @RequestBody Endereco endereco) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Cria e associa o novo endereço ao cliente
        cliente.setEndereco(endereco);
        enderecoService.salvarEndereco(endereco);  // Salva o novo endereço no banco de dados

        // Criando o EntityModel com links para o endereço recém-criado
        EntityModel<Endereco> enderecoModel = EntityModel.of(endereco,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EnderecoController.class).obterEndereco(clienteId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).obterCliente(clienteId)).withRel("cliente"));

        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoModel);
    }


    // Atualizar o endereço de um cliente
    @PutMapping
    public ResponseEntity<EntityModel<Endereco>> atualizarOuCriarEndereco(@PathVariable Long clienteId, @RequestBody Endereco enderecoAtualizado) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Endereco enderecoExistente = cliente.getEndereco();
        if (enderecoExistente == null) {
            // Se não houver endereço, cria um novo
            cliente.setEndereco(enderecoAtualizado);
            enderecoService.salvarEndereco(enderecoAtualizado); // Salva o novo endereço
            enderecoExistente = enderecoAtualizado;  // Atualiza a variável com o novo endereço
        } else {
            // Caso contrário, atualiza o endereço existente
            enderecoAtualizador.atualizar(enderecoExistente, enderecoAtualizado);
            enderecoService.salvarEndereco(enderecoExistente);  // Atualiza o endereço
        }

        // Criando o EntityModel com links para o endereço atualizado ou criado
        EntityModel<Endereco> enderecoModel = EntityModel.of(enderecoExistente,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EnderecoController.class).obterEndereco(clienteId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteController.class).obterCliente(clienteId)).withRel("cliente"));

        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoModel);
    }


    // Deletar o endereço de um cliente
    @DeleteMapping
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.buscarCliente(clienteId);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Endereco endereco = cliente.getEndereco();
        if (endereco == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cliente.setEndereco(null); // Remover o endereço do cliente
        clienteService.criarCliente(cliente); // Salvar o cliente sem o endereço

        return ResponseEntity.noContent().build(); // Status 204, sem corpo
    }
}
