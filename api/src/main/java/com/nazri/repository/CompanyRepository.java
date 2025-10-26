package com.nazri.repository;

import com.nazri.model.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CompanyRepository implements PanacheRepository<Company> {

    /**
     * Find companies by name (partial match)
     *
     * @param name the company name to search for
     * @return list of companies
     */
    public List<Company> findByNameContaining(String name) {
        return find("name like ?1", "%" + name + "%").list();
    }

    /**
     * Save a new company
     *
     * @param company the company to save
     * @return the saved company
     */
    public Company save(Company company) {
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        persistAndFlush(company);
        return company;
    }

    /**
     * Update an existing company
     *
     * @param company the company to update
     * @return the updated company
     */
    public Company update(Company company) {
        company.setUpdatedAt(LocalDateTime.now());
        getEntityManager().merge(company);
        return company;
    }

    /**
     * Find a company by ID
     *
     * @param id the company ID
     * @return Optional containing the company if found
     */
    public Optional<Company> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }
}
