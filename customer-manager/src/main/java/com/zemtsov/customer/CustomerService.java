package com.zemtsov.customer;

import com.zemtsov.exceptions.DuplicateResourceException;
import com.zemtsov.exceptions.NoChangesFoundException;
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

    public void updateCustomer(Integer id, CustomerRegistrationRequest request) {
        Customer customer = customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));

        boolean isChange = false;

        if (request.name() != null && !customer.getName().equals(request.name())) {
            customer.setName(request.name());
            isChange = true;
        }
        if (request.email() != null && !customer.getEmail().equals(request.email())) {
            if (customerDao.existsPersonWithEmail(request.email())) {
                throw new DuplicateResourceException("Email [%s] already taken".formatted(request.email()));
            }
            customer.setEmail(request.email());
            isChange = true;
        }
        if (request.age() != null && !customer.getAge().equals(request.age())) {
            customer.setAge(request.age());
            isChange = true;
        }

        if (!isChange) {
            throw new NoChangesFoundException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }
}
