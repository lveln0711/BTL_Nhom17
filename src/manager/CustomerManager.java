package manager;

import model.Customer;
import java.util.List;

public interface CustomerManager {

    boolean addCustomer(Customer customer);

    boolean updateCustomer(Customer customer);

    boolean deleteCustomerById(String customerId);

    Customer findCustomerById(String customerId);

    List<Customer> searchCustomersByName(String customerName);

    List<Customer> getAllCustomers();

    List<Customer> sortByNameAscending();

    boolean containsCustomerId(String customerId);

    boolean isEmpty();

    int getTotalCustomers();

    void clearAllCustomers();

    void loadFromFile();

    void saveToFile();
}