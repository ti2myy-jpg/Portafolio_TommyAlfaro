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
        cargarModelo(
                model,
                productoService.getProductos(false),
                "consultas.derivadas",
                "derivada",
                BigDecimal.ZERO,
                new BigDecimal("1000000")
        );

        return "consultas/listado";
    }

    @PostMapping("/derivada")
    public String consultaDerivada(
            @RequestParam BigDecimal precioInf,
            @RequestParam BigDecimal precioSup,
            Model model) {

        cargarModelo(
                model,
                productoService.consultaDerivada(precioInf, precioSup),
                "consultas.derivadas",
                "derivada",
                precioInf,
                precioSup
        );

        return "consultas/listado";
    }

    @PostMapping("/jpql")
    public String consultaJPQL(
            @RequestParam BigDecimal precioInf,
            @RequestParam BigDecimal precioSup,
            Model model) {

        cargarModelo(
                model,
                productoService.consultaJPQL(precioInf, precioSup),
                "consultas.jpql",
                "jpql",
                precioInf,
                precioSup
        );

        return "consultas/listado";
    }

    @PostMapping("/sql")
    public String consultaSQL(
            @RequestParam BigDecimal precioInf,
            @RequestParam BigDecimal precioSup,
            Model model) {

        cargarModelo(
                model,
                productoService.consultaSQL(precioInf, precioSup),
                "consultas.sql",
                "sql",
                precioInf,
                precioSup
        );

        return "consultas/listado";
    }

    private void cargarModelo(
            Model model,
            List<Producto> productos,
            String tipoConsulta,
            String tabActivo,
            BigDecimal precioInf,
            BigDecimal precioSup) {

        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("tipoConsulta", tipoConsulta);
        model.addAttribute("tabActivo", tabActivo);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
    }
}
