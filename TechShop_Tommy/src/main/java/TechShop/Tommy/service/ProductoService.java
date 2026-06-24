/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package TechShop.Tommy.service;

import TechShop.Tommy.domain.Categoria;
import TechShop.Tommy.repository.productoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final productoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(
            productoRepository productoRepository,
            FirebaseStorageService firebaseStorageService) {

        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activos) {

        if (activos) {
            return productoRepository.findByActivoTrue();
        }

        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(
            Categoria producto,
            MultipartFile imagenFile) {

        /*
         * Si se está modificando una categoría y no se selecciona una imagen
         * nueva, conserva la ruta de la imagen anterior.
         */
        if (producto.getIdCategoria() != null) {

            Optional<Categoria> productoAnterior =
                    productoRepository.findById(
                            producto.getIdCategoria()
                    );

            if (productoAnterior.isPresent()
                    && (producto.getRutaImagen() == null
                    || producto.getRutaImagen().isBlank())) {

                producto.setRutaImagen(
                        productoAnterior.get().getRutaImagen()
                );
            }
        }

        /*
         * Primero se guarda para obtener el ID de la categoría.
         */
        producto = productoRepository.save(producto);

        /*
         * Si el usuario seleccionó una imagen, se sube a Firebase.
         */
        if (imagenFile != null && !imagenFile.isEmpty()) {

            try {
                String rutaImagen =
                        firebaseStorageService.uploadImage(
                                imagenFile,
                                "producto",
                                producto.getIdCategoria()
                        );

                producto.setRutaImagen(rutaImagen);

                productoRepository.save(producto);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudo guardar la imagen de la categoría.",
                        e
                );
            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {

        /*
         * Verifica que la categoría exista.
         */
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException(
                    "La categoría con ID "
                    + idProducto
                    + " no existe."
            );
        }

        try {
            productoRepository.deleteById(idProducto);

            /*
             * Obliga a Hibernate a ejecutar el DELETE aquí.
             * Esto permite capturar el error de llave foránea
             * dentro del try-catch.
             */
            productoRepository.flush();

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "No se puede eliminar Producto porque tiene productos asociados.",
                    e
            );
        }
    }
}