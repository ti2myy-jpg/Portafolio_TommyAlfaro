package TechShop.Tommy.controller;

import TechShop.Tommy.domain.Categoria;
import TechShop.Tommy.domain.Producto;
import TechShop.Tommy.service.CategoriaService;
import TechShop.Tommy.service.ProductoService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IndexController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    public IndexController(
            CategoriaService categoriaService,
            ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        var categorias = categoriaService.getCategorias(true);
        var productos = productoService.getProductos(true);

        cargarModelo(model, categorias, productos, null);

        return "index";
    }

    @GetMapping("/categoria/{idCategoria}")
    public String categoria(
            @PathVariable("idCategoria") Integer idCategoria,
            Model model) {

        var categorias = categoriaService.getCategorias(true);
        var productos =
                productoService.getProductosPorCategoria(idCategoria);

        cargarModelo(model, categorias, productos, idCategoria);

        return "index";
    }

    private void cargarModelo(
            Model model,
            List<Categoria> categorias,
            List<Producto> productos,
            Integer idCategoria) {

        model.addAttribute("categorias", categorias);
        model.addAttribute("productos", productos);
        model.addAttribute("idCategoria", idCategoria);
    }
}
