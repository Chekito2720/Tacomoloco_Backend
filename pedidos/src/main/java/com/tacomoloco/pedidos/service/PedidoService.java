package com.tacomoloco.pedidos.service;

import com.tacomoloco.pedidos.entity.Pedido;
import com.tacomoloco.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public List<Pedido> obtenerPorFechaEntre(LocalDateTime inicio, LocalDateTime fin) {
        return pedidoRepository.findByFechaCreacionBetween(inicio, fin);
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }
}