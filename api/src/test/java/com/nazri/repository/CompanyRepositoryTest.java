package com.nazri.repository;

import com.nazri.model.Company;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyRepositoryTest {

    @Test
    void testSave() {
        CompanyRepository repo = spy(new CompanyRepository());
        doNothing().when(repo).persistAndFlush(any());

        Company company = new Company();
        Company saved = repo.save(company);

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        verify(repo).persistAndFlush(company);
    }

    @Test
    void testUpdate() {
        CompanyRepository repo = spy(new CompanyRepository());
        EntityManager em = mock(EntityManager.class);
        doReturn(em).when(repo).getEntityManager();

        Company company = new Company();
        Company updated = repo.update(company);

        assertNotNull(updated.getUpdatedAt());
        verify(em).merge(company);
    }

    @Test
    void testFindByIdOptional() {
        CompanyRepository repo = spy(new CompanyRepository());
        Company company = new Company();
        company.id = 1L;

        doReturn(company).when(repo).findById(1L);
        Optional<Company> opt = repo.findByIdOptional(1L);
        assertTrue(opt.isPresent());

        doReturn(null).when(repo).findById(2L);
        Optional<Company> empty = repo.findByIdOptional(2L);
        assertFalse(empty.isPresent());
    }

    @Test
    void testFindByNameContaining() {
        CompanyRepository repo = spy(new CompanyRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new Company()));
        doReturn(query).when(repo).find(anyString(), any(Object[].class));

        List<Company> list = repo.findByNameContaining("Acme");
        assertEquals(1, list.size());
    }
}
