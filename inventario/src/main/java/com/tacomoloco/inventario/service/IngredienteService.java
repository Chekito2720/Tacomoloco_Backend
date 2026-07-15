package com.tacomoloco.inventario.service;

import com.tacomoloco.inventario.entity.Ingrediente;
import com.tacomoloco.inventario.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public Ingrediente crear(Ingrediente ingrediente) {
        return ingredienteRepository.save(ingrediente);
    }

    public Optional<Ingrediente> obtenerPorId(Long id) {
        return ingredienteRepository.findById(id);
    }

    public List<Ingrediente> listarTodos() {
        return ingredienteRepository.findAll();
    }

    public List<Ingrediente> listarPorEstado(Ingrediente.EstadoIngrediente estado) {
        return ingredienteRepository.findByEstado(estado);
    }

    public Ingrediente actualizar(Long id, Ingrediente ingrediente) {
        Ingrediente existente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado con ID: " + id));
        
        existente.setNombre(ingrediente.getNombre());
        existente.setUnidadMedida(ingrediente.getUnidadMedida());
        existente.setCantidadDisponible(ingrediente.getCantidadDisponible());
        existente.setEstado(ingrediente.getEstado());
        existente.setCostoUnitario(ingrediente.getCostoUnitario());
        
        return ingredienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new RuntimeException("Ingrediente no encontrado con ID: " + id);
        }
        ingredienteRepository.deleteById(id);
    }
}