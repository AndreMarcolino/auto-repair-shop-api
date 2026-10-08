package br.com.autorepairshop.domain.service;

import br.com.autorepairshop.dto.input.CustomerInput;
import br.com.autorepairshop.dto.output.CustomerOutput;

public interface CustomerService {

    CustomerOutput create(CustomerInput request);
}