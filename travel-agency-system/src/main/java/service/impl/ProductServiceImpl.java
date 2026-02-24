package service.impl;

import java.util.List;

import dao.ProductDao;
import dao.impl.ProductDaoImpl;
import model.Product;
import service.ProductService;

public class ProductServiceImpl implements ProductService {

    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public void addProduct(Product p) {
        productDao.add(p);
    }

    @Override
    public void updateProduct(Product p) {
        productDao.update(p);
    }

    @Override
    public void deleteProduct(int id) {
        productDao.delete(id);
    }

    @Override
    public Product getProductById(int id) {
        return productDao.selectById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productDao.selectAll();
    }

    @Override
    public List<Product> searchProducts(String region, Integer durationDays) {
        return productDao.search(region, durationDays);
    }

    @Override
    public List<Integer> getDistinctDurationDays() {
        return productDao.getDistinctDurationDays();
    }
}
