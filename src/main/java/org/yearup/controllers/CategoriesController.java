package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao)
    {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // GET /categories
    @GetMapping
    public List<Category> getAll()
    {
        return categoryDao.getAllCategories();
    }

    // GET /categories/{id}
    @GetMapping("/{id}")
    public Category getById(@PathVariable int id)
    {
        return categoryDao.getById(id);
    }

    // GET /categories/{categoryId}/products
    @GetMapping("/{categoryId}/products")
    public List<Product> getProductsByCategory(@PathVariable int categoryId)
    {
        return productDao.listByCategoryId(categoryId);
    }

    // POST /categories (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        return categoryDao.create(category);
    }

    // PUT /categories/{id} (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        categoryDao.update(id, category);
    }

    // DELETE /categories/{id} (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {
        categoryDao.delete(id);
    }
}
