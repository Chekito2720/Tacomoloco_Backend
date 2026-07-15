package com.tacomoloco.pedidos.service;

import com.tacomoloco.pedidos.entity.Notificacion;
import com.tacomoloco.pedidos.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Transactional(readOnly = true)
    public List<Notificacion> obtenerPorPedido(Long pedidoId) {
        return notificacionRepository.findByPedidoId(pedidoId);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> obtenerNoLeidas(Long clienteId) {
        return notificacionRepository.findByClienteIdAndLeidoFalse(clienteId);
    }

    @Transactional
    public Notificacion marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificacion no encontrada con ID: " + notificacionId));
        notificacion.setLeido(true);
        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public void marcarTodasComoLeidas(Long clienteId) {
        List<Notificacion> noLeidas = notificacionRepository.findByClienteIdAndLeidoFalse(clienteId);
        noLeidas.forEach(n -> n.setLeido(true));
        notificacionRepository.saveAll(noLeidas);
    }
}