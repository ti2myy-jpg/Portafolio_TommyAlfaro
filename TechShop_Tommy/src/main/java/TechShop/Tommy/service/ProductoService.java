package TechShop.Tommy.service;

import TechShop.Tommy.domain.Producto;
import TechShop.Tommy.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(
            ProductoRepository productoRepository,
            FirebaseStorageService firebaseStorageService) {

        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activos) {

        if (activos) {
            return productoRepository.findByActivoTrue();
        }

        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(
            Producto producto,
            MultipartFile imagenFile) {

        if (producto.getIdProducto() != null) {

            Optional<Producto> productoAnterior =
                    productoRepository.findById(producto.getIdProducto());

            if (productoAnterior.isPresent()
                    && (producto.getRutaImagen() == null
                    || producto.getRutaImagen().isBlank())) {

                producto.setRutaImagen(
                        productoAnterior.get().getRutaImagen()
                );
            }
        }

        producto = productoRepository.save(producto);

        if (imagenFile != null && !imagenFile.isEmpty()) {

            try {
                String rutaImagen =
                        firebaseStorageService.uploadImage(
                                imagenFile,
                                "producto",
                                producto.getIdProducto()
                        );

                producto.setRutaImagen(rutaImagen);

                productoRepository.save(producto);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudo guardar la imagen del producto.",
                        e
                );
            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {

        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException(
                    "El producto con ID "
                    + idProducto
                    + " no existe."
            );
        }

        try {
            productoRepository.deleteById(idProducto);
            productoRepository.flush();

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "No se puede eliminar el producto porque tiene ventas asociadas.",
                    e
            );
        }
    }
}
