package manager;

import file.FileHandler;
import model.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductManagerImpl implements ProductManager {

    private List<Product> productList = new ArrayList<>();

    private static final String FILE_PATH = "data/products.txt";

    public ProductManagerImpl() {
        loadFromFile();
    }

    @Override
    public boolean addProduct(Product product) {

        if (product == null) return false;

        if (containsProductId(product.getProductId())) return false;

        boolean added = productList.add(product);

        if (added) saveToFile();

        return added;
    }

    @Override
    public boolean updateProduct(Product updatedProduct) {

        if (updatedProduct == null) return false;

        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getProductId()
                    .equalsIgnoreCase(updatedProduct.getProductId())) {

                productList.set(i, updatedProduct);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteProductById(String productId) {

        if (productId == null) return false;

        boolean removed = productList.removeIf(
                p -> p.getProductId().equalsIgnoreCase(productId.trim())
        );

        if (removed) saveToFile();

        return removed;
    }

    @Override
    public Product findProductById(String productId) {

        if (productId == null) return null;

        for (Product p : productList) {
            if (p.getProductId().equalsIgnoreCase(productId.trim())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Product> searchProductsByName(String name) {

        List<Product> result = new ArrayList<>();
        if (name == null) return result;

        String key = name.toLowerCase().trim();

        for (Product p : productList) {
            if (p.getProductName().toLowerCase().contains(key)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }

    @Override
    public List<Product> sortByPriceAscending() {
        List<Product> list = new ArrayList<>(productList);
        list.sort(Comparator.comparingDouble(Product::getProductPrice));
        return list;
    }

    @Override
    public List<Product> sortByPriceDescending() {
        List<Product> list = new ArrayList<>(productList);
        list.sort(Comparator.comparingDouble(Product::getProductPrice).reversed());
        return list;
    }

    @Override
    public List<Product> sortByNameAscending() {
        List<Product> list = new ArrayList<>(productList);
        list.sort(Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    @Override
    public boolean containsProductId(String id) {
        return findProductById(id) != null;
    }

    @Override
    public boolean isEmpty() {
        return productList.isEmpty();
    }

    @Override
    public int getTotalProducts() {
        return productList.size();
    }

    @Override
    public void loadFromFile() {
        List<Product> loaded = FileHandler.loadProductsFromFile(FILE_PATH);
        if (loaded != null) productList = loaded;
    }

    @Override
    public void saveToFile() {
        FileHandler.saveProductsToFile(FILE_PATH, productList);
    }
}