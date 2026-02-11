package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.HerramientaDTO;
import com.rentaherramientas.plataforma.entidad.Herramienta;
import com.rentaherramientas.plataforma.repositorio.HerramientaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/herramientas")
public class HerramientaController {

    @Autowired
    private HerramientaRepositorio herramientaRepositorio;

    @GetMapping
    public List<HerramientaDTO> listarHerramientas() {
        return herramientaRepositorio.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private HerramientaDTO convertirADTO(Herramienta herramienta) {
        HerramientaDTO dto = new HerramientaDTO();
        dto.setId(herramienta.getId());
        dto.setNombre(herramienta.getNombre());
        dto.setDescripcion(herramienta.getDescripcion());

        // CORRECCIÓN DE TIPO AQUÍ:
        // Si el DTO es Double y la Entidad es BigDecimal:
        if (herramienta.getPrecioDia() != null) {
            dto.setPrecioDia(herramienta.getPrecioDia());
            // ^ Si ambos son BigDecimal, esto funciona directo.
            // ^ Si el DTO es Double, usa: dto.setPrecioDia(herramienta.getPrecioDia().doubleValue());
        }

        dto.setStock(herramienta.getStock());
        dto.setDisponible(herramienta.getStock() != null && herramienta.getStock() > 0);

        if (herramienta.getProveedor() != null) {
            dto.setNombreProveedor(herramienta.getProveedor().getNombre());
        }

        return dto;
    }
}