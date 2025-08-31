package com.nazri.repository;

import com.nazri.model.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CompanyRepository implements PanacheRepository<Company> {

    /**
     * Find a company by name
     *
     * @param name the company name
     * @return list of companies with matching name
     */
    public List<Company> findByName(String name) {
        return find("name", name).list();
    }

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
     * Get all companies
     *
     * @return list of all companies
     */
    public List<Company> findAllCompanies() {
        return findAll().list();
    }

    /**
     * Create a new company
     *
     * @param company the company to create
     * @return the created company
     */
    public Company createCompany(Company company) {
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
    public Company updateCompany(Company company) {
        company.setUpdatedAt(LocalDateTime.now());
        getEntityManager().merge(company);
        return company;
    }
}
