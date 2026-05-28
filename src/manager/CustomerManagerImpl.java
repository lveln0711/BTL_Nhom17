package manager;

import file.FileHandler;
import model.Customer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomerManagerImpl implements CustomerManager {

    private List<Customer> customerList = new ArrayList<>();

    private static final String FILE_PATH = "data/customers.txt";

    public CustomerManagerImpl() {
        loadFromFile();
    }

    @Override
    public boolean addCustomer(Customer customer) {

        if (customer == null) return false;

        if (containsCustomerId(customer.getCustomerId())) return false;

        boolean added = customerList.add(customer);

        if (added) saveToFile();

        return added;
    }

    @Override
    public boolean updateCustomer(Customer updatedCustomer) {

        if (updatedCustomer == null) return false;

        for (int i = 0; i < customerList.size(); i++) {

            if (customerList.get(i).getCustomerId()
                    .equalsIgnoreCase(updatedCustomer.getCustomerId())) {

                customerList.set(i, updatedCustomer);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteCustomerById(String id) {

        if (id == null) return false;

        boolean removed = customerList.removeIf(
                c -> c.getCustomerId().equalsIgnoreCase(id.trim())
        );

        if (removed) saveToFile();

        return removed;
    }

    @Override
    public Customer findCustomerById(String id) {

        if (id == null) return null;

        for (Customer c : customerList) {
            if (c.getCustomerId().equalsIgnoreCase(id.trim())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Customer> searchCustomersByName(String name) {

        List<Customer> result = new ArrayList<>();
        if (name == null) return result;

        String key = name.toLowerCase().trim();

        for (Customer c : customerList) {
            if (c.getCustomerName().toLowerCase().contains(key)) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerList);
    }

    @Override
    public List<Customer> sortByNameAscending() {
        List<Customer> list = new ArrayList<>(customerList);
        list.sort(Comparator.comparing(Customer::getCustomerName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    @Override
    public boolean containsCustomerId(String id) {
        return findCustomerById(id) != null;
    }

    @Override
    public boolean isEmpty() {
        return customerList.isEmpty();
    }

    @Override
    public int getTotalCustomers() {
        return customerList.size();
    }

    @Override
    public void clearAllCustomers() {
        customerList.clear();
        saveToFile();
    }

    @Override
    public void loadFromFile() {
        List<Customer> loaded = FileHandler.loadCustomersFromFile(FILE_PATH);
        if (loaded != null) customerList = loaded;
    }

    @Override
    public void saveToFile() {
        FileHandler.saveCustomersToFile(FILE_PATH, customerList);
    }
}