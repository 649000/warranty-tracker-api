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
        return companyRepository.findByIdOptional(id);
    }

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

    /**
     * Create a new company
     * @param company the company to create
     * @return the created company
     */
    @Transactional
    public Company createCompany(Company company) {
        // Check if company with same name already exists
        if (company.getName() != null && !company.getName().isEmpty()) {
            List<Company> existingCompanies = companyRepository.findByNameContaining(company.getName());
            if (!existingCompanies.isEmpty()) {
                for (Company existingCompany : existingCompanies) {
                    if (existingCompany.getName().equals(company.getName())) {
                        throw new IllegalArgumentException("Company with this name already exists");
                    }
                }
            }
        }

        return companyRepository.createCompany(company);
    }

    /**
     * Update an existing company
     *
     * @param id the company ID
     * @param company the company data to update
     * @return Optional containing the updated company if found
     */
    @Transactional
    public Optional<Company> updateCompany(Long id, Company company) {
        Optional<Company> existingCompany = findById(id);
        if (existingCompany.isPresent()) {
            Company companyToUpdate = existingCompany.get();
            companyToUpdate.setName(company.getName());
            companyToUpdate.setContactPhone(company.getContactPhone());
            companyToUpdate.setContactEmail(company.getContactEmail());
            companyToUpdate.setWebsite(company.getWebsite());
            companyToUpdate.setAddress(company.getAddress());
            companyToUpdate.setClaimProcess(company.getClaimProcess());
            companyToUpdate.setClaimUrl(company.getClaimUrl());
            companyToUpdate.setSupportHours(company.getSupportHours());
            companyToUpdate.setReturnInstructions(company.getReturnInstructions());
            return Optional.of(companyRepository.updateCompany(companyToUpdate));
        }
        return Optional.empty();
    }

    /**
     * Delete a company by ID
     *
     * @param id the company ID
     * @return true if company was deleted, false if not found
     */
    @Transactional
    public boolean deleteCompany(Long id) {
        return companyRepository.deleteById(id);
    }
}
