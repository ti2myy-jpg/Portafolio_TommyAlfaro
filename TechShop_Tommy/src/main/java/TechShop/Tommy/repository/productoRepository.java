package TechShop.Tommy.repository;

import TechShop.Tommy.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    public List<Producto> findByActivoTrue();

    public List<Producto> findByPrecioBetweenOrderByDescripcion(
            BigDecimal precioInf,
            BigDecimal precioSup);

    @Query("SELECT p FROM Producto p "
            + "WHERE p.precio BETWEEN :precioInf AND :precioSup "
            + "ORDER BY p.descripcion")
    public List<Producto> consultaJPQL(
            @Param("precioInf") BigDecimal precioInf,
            @Param("precioSup") BigDecimal precioSup);

    @Query(value = "SELECT * FROM producto "
            + "WHERE precio BETWEEN :precioInf AND :precioSup "
            + "ORDER BY descripcion",
            nativeQuery = true)
    public List<Producto> consultaSQL(
            @Param("precioInf") BigDecimal precioInf,
            @Param("precioSup") BigDecimal precioSup);
}
