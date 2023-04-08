package com.zemtsov.customer;

import com.zemtsov.exceptions.DuplicateResourceException;
import com.zemtsov.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        if (customerDao.existsPersonWithEmail(request.email())) {
            throw new DuplicateResourceException("Email [%s] already taken".formatted(request.email()));
        }
        Customer customer = new Customer(request.name(), request.email(), request.age());
        customerDao.insertCustomer(customer);
    }

    public void removeCustomer(Integer id) {
        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
        customerDao.deleteCustomer(id);
    }
}
