package br.com.autorepairshop.application.service;

import br.com.autorepairshop.customer.Customer;
import br.com.autorepairshop.customer.CustomerRepository;
import br.com.autorepairshop.customer.dto.CreateCustomerRequest;
import br.com.autorepairshop.customer.dto.CustomerResponse;
import br.com.autorepairshop.customer.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        String name = request.name().trim();
        String cpf = normalize(request.cpf());
        String cnpj = normalizeCnpj(request.cnpj());

        validateExactlyOneDocument(cpf, cnpj);
        validateDocumentIsUnique(cpf, cnpj);

        Customer customer = new Customer(name, cpf, cnpj);
        Customer savedCustomer = customerRepository.save(customer);

        return new CustomerResponse(
                savedCustomer.getId(),
                savedCustomer.getName(),
                savedCustomer.getCpf(),
                savedCustomer.getCnpj()
        );
    }

    private void validateExactlyOneDocument(String cpf, String cnpj) {
        if ((cpf == null) == (cnpj == null)) {
            throw new IllegalArgumentException(
                    "Provide exactly one document: CPF or CNPJ"
            );
        }
    }

    private void validateDocumentIsUnique(String cpf, String cnpj) {
        if (cpf != null && customerRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF is already registered");
        }

        if (cnpj != null && customerRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException("CNPJ is already registered");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeCnpj(String value) {
        String normalizedValue = normalize(value);

        return normalizedValue == null
                ? null
                : normalizedValue.toUpperCase(Locale.ROOT);
    }
}