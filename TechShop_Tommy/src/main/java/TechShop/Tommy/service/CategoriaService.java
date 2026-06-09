/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package TechShop.Tommy.service;
 
import TechShop.Tommy.domain.Categoria; 
import TechShop.Tommy.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service

public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    public CategoriaService(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;

    }

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo){
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();

    }
     @Transactional(readOnly = true)
    public Categoria getCategoria(Categoria categoria) {
        return categoriaRepository.findById(categoria.getIdCategoria()).orElse(null);
    }

    @Transactional
    public void save(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void delete(Categoria categoria) {
        categoriaRepository.delete(categoria);
    }
}




 