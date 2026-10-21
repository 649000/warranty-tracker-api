package com.nazri.service;

import com.nazri.model.Company;
import com.nazri.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    private Company sampleCompany;

    @BeforeEach
    void setUp() {
        sampleCompany = new Company();
        sampleCompany.id = 1L;
        sampleCompany.setName("Apple Inc");
        sampleCompany.setContactEmail("contact@apple.com");
    }

    @Test
    void testFindById_Found() {
        when(companyRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleCompany));

        Optional<Company> result = companyService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Apple Inc", result.get().getName());
        verify(companyRepository).findByIdOptional(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(companyRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Optional<Company> result = companyService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByNameContaining() {
        List<Company> list = List.of(sampleCompany);
        when(companyRepository.findByNameContaining("Apple")).thenReturn(list);

        List<Company> result = companyService.findByNameContaining("Apple");

        assertEquals(1, result.size());
        assertEquals("Apple Inc", result.get(0).getName());
    }

    @Test
    void testFindAllCompanies() {
        when(companyRepository.listAll()).thenReturn(List.of(sampleCompany));

        List<Company> result = companyService.findAllCompanies();

        assertEquals(1, result.size());
        verify(companyRepository).listAll();
    }

    @Test
    void testCreateCompany_Success() {
        when(companyRepository.findByNameContaining("Apple Inc")).thenReturn(Collections.emptyList());
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company created = companyService.createCompany(sampleCompany);

        assertNotNull(created);
        assertEquals("Apple Inc", created.getName());
        verify(companyRepository).save(sampleCompany);
    }

    @Test
    void testCreateCompany_PartialMatchDoesNotThrow() {
        Company partialMatch = new Company();
        partialMatch.id = 3L;
        partialMatch.setName("Apple Pie Co");

        when(companyRepository.findByNameContaining("Apple Inc")).thenReturn(List.of(partialMatch));
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company created = companyService.createCompany(sampleCompany);

        assertNotNull(created);
        assertEquals("Apple Inc", created.getName());
        verify(companyRepository).save(sampleCompany);
    }

    @Test
    void testCreateCompany_DuplicateNameThrowsException() {
        Company existing = new Company();
        existing.id = 2L;
        existing.setName("Apple Inc");

        when(companyRepository.findByNameContaining("Apple Inc")).thenReturn(List.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            companyService.createCompany(sampleCompany);
        });

        assertEquals("Company with this name already exists", ex.getMessage());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void testCreateCompany_WithNullOrEmptyName() {
        Company noName = new Company();
        when(companyRepository.save(noName)).thenReturn(noName);

        Company created = companyService.createCompany(noName);

        assertNotNull(created);
        verify(companyRepository).save(noName);
    }

    @Test
    void testUpdateCompany_Found() {
        when(companyRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleCompany));
        when(companyRepository.update(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        Company updateData = new Company();
        updateData.setName("Apple Operations");
        updateData.setContactPhone("123-456");
        updateData.setContactEmail("ops@apple.com");
        updateData.setWebsite("https://apple.com");
        updateData.setAddress("Cupertino, CA");
        updateData.setClaimProcess("Genius Bar");
        updateData.setClaimUrl("https://support.apple.com");
        updateData.setSupportHours("24/7");
        updateData.setReturnInstructions("Drop off at store");

        Optional<Company> result = companyService.updateCompany(1L, updateData);

        assertTrue(result.isPresent());
        assertEquals("Apple Operations", result.get().getName());
        assertEquals("123-456", result.get().getContactPhone());
        assertEquals("ops@apple.com", result.get().getContactEmail());
        assertEquals("https://apple.com", result.get().getWebsite());
        assertEquals("Cupertino, CA", result.get().getAddress());
        assertEquals("Genius Bar", result.get().getClaimProcess());
        assertEquals("https://support.apple.com", result.get().getClaimUrl());
        assertEquals("24/7", result.get().getSupportHours());
        assertEquals("Drop off at store", result.get().getReturnInstructions());
        verify(companyRepository).update(sampleCompany);
    }

    @Test
    void testUpdateCompany_NotFound() {
        when(companyRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Optional<Company> result = companyService.updateCompany(999L, new Company());

        assertFalse(result.isPresent());
        verify(companyRepository, never()).update(any());
    }

    @Test
    void testDeleteCompany() {
        when(companyRepository.deleteById(1L)).thenReturn(true);

        boolean deleted = companyService.deleteCompany(1L);

        assertTrue(deleted);
        verify(companyRepository).deleteById(1L);
    }
}
