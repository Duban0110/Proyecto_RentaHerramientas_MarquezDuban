package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.entidad.Herramienta;
import com.rentaherramientas.plataforma.repositorio.HerramientaRepositorio; // Asegúrate de tener este repo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/herramientas")
public class HerramientaController {

    @Autowired
    private HerramientaRepositorio herramientaRepositorio;

    @GetMapping
    public List<Herramienta> listarHerramientas() {
        return herramientaRepositorio.findAll();
    }
}