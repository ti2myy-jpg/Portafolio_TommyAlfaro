/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.Tommy.controller;

import TechShop.Tommy.domain.Categoria;
import TechShop.Tommy.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/categoria")


public class CategoriaController {
    
    private final CategoriaService categoriaService;
    
    public CategoriaController(TechShop.Tommy.service.CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }
    
    @GetMapping("/listado")
    public String inicio(Model model){
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias",categorias);
        model.addAttribute("totalCategorias",categorias.size());
        model.addAttribute("categoria", new Categoria());
        return  "categoria/listado";
    }

    @GetMapping("/nuevo")
    public String categoriaNuevo(Categoria categoria) {
        return "categoria/modifica";
    }

    @PostMapping("/guardar")
    public String categoriaGuardar(Categoria categoria) {
        categoriaService.save(categoria);
        return "redirect:/categoria/listado";
    }

    @GetMapping("/eliminar/{idCategoria}")
    public String categoriaEliminar(Categoria categoria) {
        categoriaService.delete(categoria);
        return "redirect:/categoria/listado";
    }

    @GetMapping("/modificar/{idCategoria}")
    public String categoriaModificar(Categoria categoria, Model model) {
        categoria = categoriaService.getCategoria(categoria);
        model.addAttribute("categoria", categoria);
        return "categoria/modifica";
    }
}
