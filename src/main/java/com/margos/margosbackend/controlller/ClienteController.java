package com.margos.margosbackend.controlller;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.margos.margosbackend.entity.Cliente;
import com.margos.margosbackend.service.ClienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAuthority('APPROLE_Admin')")
public class ClienteController {
    private final ClienteService clienteService;

    @RequestMapping(path = "/listar", method = RequestMethod.GET)
    public ResponseEntity<Page<Cliente>> listarClientes(@PositiveOrZero
        @RequestParam(value = "page",defaultValue = "0") int page,
        @Positive @RequestParam(value = "size",defaultValue = "5") int size
    ) {
        // @Positive int page,@Positive int size
        var resultado = clienteService.listClientes(page,size);
        return ResponseEntity.ok(resultado);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Cliente> getCliente(@PathVariable Long id) {
        var cliente = clienteService.getClienteById(id);

        return cliente.isPresent() 
                ? ResponseEntity.ok(cliente.get())
                : ResponseEntity.notFound().build();
    }

    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente created = clienteService.crearCliente(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + created.getId())).body(created);
    }


    @RequestMapping(path = "/{id}", method=RequestMethod.PUT)
    public ResponseEntity<Cliente> modificarCliente(@Valid @RequestBody Cliente cliente, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(clienteService.actualizarCliente(id, cliente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    

}
