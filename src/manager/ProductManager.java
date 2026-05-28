package manager;

import model.Product;
import java.util.List;

public interface ProductManager {

    boolean addProduct(Product product);

    boolean updateProduct(Product product);

    boolean deleteProductById(String productId);

    Product findProductById(String productId);

    List<Product> searchProductsByName(String productName);

    List<Product> getAllProducts();

    List<Product> sortByPriceAscending();

    List<Product> sortByPriceDescending();

    List<Product> sortByNameAscending();

    boolean containsProductId(String productId);

    boolean isEmpty();

    int getTotalProducts();

    void loadFromFile();

    void saveToFile();
}