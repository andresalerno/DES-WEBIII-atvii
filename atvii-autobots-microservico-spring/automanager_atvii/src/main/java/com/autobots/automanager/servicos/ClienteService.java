package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    
    // Listar todos os clientes
    public List<Cliente> listarClientes() {
        return clienteRepositorio.findAll(); // Retorna todos os clientes do banco de dados
    }
    
    // Buscar cliente por ID
    public Cliente buscarCliente(Long id) {
        return clienteRepositorio.findById(id).orElse(null); // Retorna o cliente se encontrado, senão retorna null
    }

    // Criação do cliente
    public Cliente criarCliente(Cliente cliente) {
        return clienteRepositorio.save(cliente); // Salva o cliente no banco de dados
    }

    // Atualizar cliente
    public Cliente atualizarCliente(Long id, Cliente cliente) {
        if (!clienteRepositorio.existsById(id)) {
            return null; // Se não encontrar o cliente, retorna null
        }
        cliente.setId(id); // Garantimos que o ID seja o mesmo
        return clienteRepositorio.save(cliente); // Salva o cliente atualizado
    }

    // Deletar cliente
    public boolean deletarCliente(Long id) {
        if (clienteRepositorio.existsById(id)) {
            clienteRepositorio.deleteById(id); // Exclui o cliente do banco de dados
            return true;
        }
        return false; // Se não encontrar o cliente, retorna false
    }
}
