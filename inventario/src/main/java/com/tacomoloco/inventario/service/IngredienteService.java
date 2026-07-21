package com.tacomoloco.inventario.service;

import com.tacomoloco.inventario.entity.Ingrediente;
import com.tacomoloco.inventario.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public Ingrediente crear(Ingrediente ingrediente) {
        if (ingrediente.getStockMinimo() == null) {
            ingrediente.setStockMinimo(10.0);
        }
        ingrediente.actualizarEstado();
        return ingredienteRepository.save(ingrediente);
    }

    @Transactional(readOnly = true)
    public Optional<Ingrediente> obtenerPorId(Long id) {
        return ingredienteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Ingrediente> listarTodos() {
        return ingredienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Ingrediente> listarPorEstado(Ingrediente.EstadoIngrediente estado) {
        return ingredienteRepository.findByEstado(estado);
    }

    public Ingrediente actualizar(Long id, Ingrediente ingrediente) {
        Ingrediente existente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado con ID: " + id));

        existente.setNombre(ingrediente.getNombre());
        existente.setUnidadMedida(ingrediente.getUnidadMedida());
        existente.setCantidadDisponible(ingrediente.getCantidadDisponible());
        existente.setStockMinimo(ingrediente.getStockMinimo() != null ? ingrediente.getStockMinimo() : 10.0);
        existente.setCostoUnitario(ingrediente.getCostoUnitario());
        existente.actualizarEstado();

        return ingredienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new RuntimeException("Ingrediente no encontrado con ID: " + id);
        }
        ingredienteRepository.deleteById(id);
    }

    public Ingrediente registrarConsumo(Long id, Double cantidad) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado con ID: " + id));

        if (ingrediente.getCantidadDisponible() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + ingrediente.getCantidadDisponible() + ", solicitado: " + cantidad);
        }

        ingrediente.setCantidadDisponible(ingrediente.getCantidadDisponible() - cantidad);
        ingrediente.actualizarEstado();
        return ingredienteRepository.save(ingrediente);
    }

    public Ingrediente reabastecer(Long id, Double cantidad) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado con ID: " + id));

        ingrediente.setCantidadDisponible(ingrediente.getCantidadDisponible() + cantidad);
        ingrediente.actualizarEstado();
        return ingredienteRepository.save(ingrediente);
    }
}