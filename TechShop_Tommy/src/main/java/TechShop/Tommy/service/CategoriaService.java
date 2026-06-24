/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package TechShop.Tommy.service;

import TechShop.Tommy.domain.Categoria;
import TechShop.Tommy.repository.CategoriaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final FirebaseStorageService firebaseStorageService;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            FirebaseStorageService firebaseStorageService) {

        this.categoriaRepository = categoriaRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activos) {

        if (activos) {
            return categoriaRepository.findByActivoTrue();
        }

        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    @Transactional
    public void save(
            Categoria categoria,
            MultipartFile imagenFile) {

        /*
         * Si se está modificando una categoría y no se selecciona una imagen
         * nueva, conserva la ruta de la imagen anterior.
         */
        if (categoria.getIdCategoria() != null) {

            Optional<Categoria> categoriaAnterior =
                    categoriaRepository.findById(
                            categoria.getIdCategoria()
                    );

            if (categoriaAnterior.isPresent()
                    && (categoria.getRutaImagen() == null
                    || categoria.getRutaImagen().isBlank())) {

                categoria.setRutaImagen(
                        categoriaAnterior.get().getRutaImagen()
                );
            }
        }

        /*
         * Primero se guarda para obtener el ID de la categoría.
         */
        categoria = categoriaRepository.save(categoria);

        /*
         * Si el usuario seleccionó una imagen, se sube a Firebase.
         */
        if (imagenFile != null && !imagenFile.isEmpty()) {

            try {
                String rutaImagen =
                        firebaseStorageService.uploadImage(
                                imagenFile,
                                "categoria",
                                categoria.getIdCategoria()
                        );

                categoria.setRutaImagen(rutaImagen);

                categoriaRepository.save(categoria);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudo guardar la imagen de la categoría.",
                        e
                );
            }
        }
    }

    @Transactional
    public void delete(Integer idCategoria) {

        /*
         * Verifica que la categoría exista.
         */
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException(
                    "La categoría con ID "
                    + idCategoria
                    + " no existe."
            );
        }

        try {
            categoriaRepository.deleteById(idCategoria);

            /*
             * Obliga a Hibernate a ejecutar el DELETE aquí.
             * Esto permite capturar el error de llave foránea
             * dentro del try-catch.
             */
            categoriaRepository.flush();

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "No se puede eliminar la categoría porque tiene productos asociados.",
                    e
            );
        }
    }
}