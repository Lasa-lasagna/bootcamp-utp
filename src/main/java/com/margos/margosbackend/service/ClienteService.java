package com.margos.margosbackend.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.margos.margosbackend.entity.Cliente;
import com.margos.margosbackend.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService{
    private final ClienteRepository clienteRepository;

    public Page<Cliente> listClientes(int page, int size){
        Pageable pgCliente = PageRequest.of(page, size);

        var clientes = clienteRepository.findByActivoTrue(pgCliente);

        return clientes;
    }

    public Optional<Cliente> getClienteById(Long id){
        return clienteRepository.findById(id);
    }

    public Cliente crearCliente(Cliente cliente){

        clienteRepository.findByEmail(cliente.getEmail()).ifPresent(
            c -> {throw new IllegalArgumentException("Ya existe un cliente con ese email");}
        );

        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(Long id, Cliente cliente){
        Cliente actual = clienteRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Cliente no encontrado")
        );

        actual.setNombre(cliente.getNombre());
        actual.setEmail(cliente.getEmail());
        actual.setActivo(cliente.isActivo());
        
        return clienteRepository.save(actual);
    }

    public void eliminarCliente(Long id){
        Cliente eliminar = clienteRepository.findById(id).orElseThrow(
            ()->new IllegalArgumentException("No existe ese cliente")
        );

        eliminar.setActivo(false);
        
        clienteRepository.save(eliminar);
    }

}
