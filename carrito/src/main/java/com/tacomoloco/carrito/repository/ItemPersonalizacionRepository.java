package com.tacomoloco.carrito.repository;

import com.tacomoloco.carrito.entity.ItemPersonalizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPersonalizacionRepository extends JpaRepository<ItemPersonalizacion, Long> {
    List<ItemPersonalizacion> findByItemCarritoId(Long itemCarritoId);

    void deleteByItemCarritoId(Long itemCarritoId);
}