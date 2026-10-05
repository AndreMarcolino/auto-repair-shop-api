package br.com.autorepairshop.domain.service;

import br.com.autorepairshop.customer.dto.CreateCustomerRequest;
import br.com.autorepairshop.customer.dto.CustomerResponse;

public interface CustomerService {

    CustomerResponse create(CreateCustomerRequest request);
}