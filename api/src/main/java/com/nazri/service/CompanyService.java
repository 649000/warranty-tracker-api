package com.nazri.service;

import com.nazri.model.Company;
import com.nazri.repository.CompanyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CompanyService {

    @Inject
    CompanyRepository companyRepository;

    /**
     * Find a company by its ID
     *
     * @param id the company ID
     * @return Optional containing the company if found
     */
    public Optional<Company> findById(Long id) {
        return Optional.ofNullable(companyRepository.findById(id));
    }
//
//    /**
//     * Find a company by name
//     * @param name the company name
//     * @return Optional containing the company if found
//     */
//    public Optional<Company> findByName(String name) {
//        return companyRepository.findByName(name);
//    }

    /**
     * Find companies by name (partial match)
     *
     * @param name the company name to search for
     * @return list of companies
     */
    public List<Company> findByNameContaining(String name) {
        return companyRepository.findByNameContaining(name);
    }

    /**
     * Get all companies
     *
     * @return list of all companies
     */
    public List<Company> findAllCompanies() {
        return companyRepository.findAllCompanies();
    }

//    /**
//     * Create a new company
//     * @param company the company to create
//     * @return the created company
//     */
//    @Transactional
//    public Company createCompany(Company company) {
//        // Check if company with same name already exists
//        if (company.getName() != null && !company.getName().isEmpty()) {
//            Optional<Company> existingCompany = companyRepository.findByName(company.getName());
//            if (existingCompany.isPresent()) {
//                throw new IllegalArgumentException("Company with this name already exists");
//            }
//        }
//
//        return companyRepository.createCompany(company);
//    }

    /**
     * Update an existing company
     *
     * @param company the company to update
     * @return the updated company
     */
    @Transactional
    public Company updateCompany(Company company) {
        return companyRepository.updateCompany(company);
    }

    /**
     * Delete a company by ID
     *
     * @param id the company ID
     * @return
     */
    @Transactional
    public boolean deleteCompany(Long id) {
        companyRepository.deleteCompany(id);
        return false;
    }
}
