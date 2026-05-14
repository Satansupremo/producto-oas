package com.example.producto_oas.controllers;

import com.example.producto_oas.dto.request.ProductoRequest;
import com.example.producto_oas.dto.response.ProductoResponse;
import com.example.producto_oas.dto.response.ServiceResponse;
import com.example.producto_oas.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/producto")
@Tag(name = "Módulo de Productos", description = "Endpoints para la gestión y pruebas masivas de productos en la nube")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos los productos", description = "Retorna un listado completo con los productos registrados en la base de datos")
    public ResponseEntity<ServiceResponse<List<ProductoResponse>>> listarProductos() {
        ServiceResponse<List<ProductoResponse>> response = productoService.listarProductos();

        if (response.isOk()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar un nuevo producto", description = "Valida los campos obligatorios del Request y almacena el registro en MySQL")
    public ResponseEntity<ServiceResponse<Integer>> registrarProducto(@Valid @RequestBody ProductoRequest productoRequest) {
        ServiceResponse<Integer> response = productoService.registrarProducto(productoRequest);

        if (response.isOk()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Busca un producto específico utilizando su identificador numérico de clave primaria")
    public ResponseEntity<ServiceResponse<ProductoResponse>> obtenerProductoPorId(@PathVariable("id") int idProducto) {
        ServiceResponse<ProductoResponse> response = productoService.obtenerProductoPorId(idProducto);

        if (response.isOk()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/cargar-pruebas")
    @Operation(summary = "Poblar base de datos con datos simulados", description = "Inserta de golpe 4 productos tecnológicos de prueba para agilizar los tests del sistema")
    public ResponseEntity<ServiceResponse<String>> cargarDatosDePrueba() {
        List<ProductoRequest> productosPrueba = new ArrayList<>();

        productosPrueba.add(new ProductoRequest("Laptop Gamer Nitro", "Core i7, 16GB RAM, RTX 4060", 15, 1299.99));
        productosPrueba.add(new ProductoRequest("Mouse Ergonómico Inalámbrico", "Mouse óptico recargable 2.4G", 50, 29.50));
        productosPrueba.add(new ProductoRequest("Teclado Mecánico RGB", "Teclado con switches mecánicos azules", 30, 75.00));
        productosPrueba.add(new ProductoRequest("Monitor 27'' Curvo", "Resolución QHD 165Hz para gaming", 8, 349.90));

        int insertadosCorrectamente = 0;

        for (ProductoRequest prod : productosPrueba) {
            ServiceResponse<Integer> res = productoService.registrarProducto(prod);
            if (res.isOk()) {
                insertadosCorrectamente++;
            }
        }

        if (insertadosCorrectamente > 0) {
            return new ResponseEntity<>(
                    ServiceResponse.exito("Se insertaron con éxito " + insertadosCorrectamente + " productos de prueba.", null),
                    HttpStatus.CREATED
            );
        } else {
            return new ResponseEntity<>(
                    ServiceResponse.error("No se pudo insertar ningún producto de prueba."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
