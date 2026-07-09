package TechShop.Tommy.controller;

import TechShop.Tommy.domain.Producto;
import TechShop.Tommy.service.ProductoService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/consultas")
public class ConsultasController {

    private final ProductoService productoService;

    public ConsultasController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);

        cargarModelo(
                model,
                productos,
                BigDecimal.ZERO,
                new BigDecimal("1000000")
        );

        return "/consultas/listado";
    }

    @PostMapping("/consultaDerivada")
    public String consultaDerivada(
            @RequestParam BigDecimal precioInf,
            @RequestParam BigDecimal precioSup,
            Model model) {

        var productos =
                productoService.consultaDerivada(precioInf, precioSup);

        cargarModelo(model, productos, precioInf, precioSup);

        return "/consultas/listado";
    }

    @PostMapping("/consultaJPQL")
    public String consultaJPQL(
            @RequestParam BigDecimal precioInf,
            @RequestParam BigDecimal precioSup,
            Model model) {

        var productos = productoService.consultaJPQL(precioInf, precioSup);

        cargarModelo(model, productos, precioInf, precioSup);

        return "/consultas/listado";
    }

    @PostMapping("/consultaSQL")
    public String consultaSQL(
            @RequestParam BigDecimal precioInf,
            @RequestParam BigDecimal precioSup,
            Model model) {

        var productos = productoService.consultaSQL(precioInf, precioSup);

        cargarModelo(model, productos, precioInf, precioSup);

        return "/consultas/listado";
    }

    private void cargarModelo(
            Model model,
            List<Producto> productos,
            BigDecimal precioInf,
            BigDecimal precioSup) {

        model.addAttribute("productos", productos);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
    }
}
