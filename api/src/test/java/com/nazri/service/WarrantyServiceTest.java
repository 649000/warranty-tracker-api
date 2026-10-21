package com.nazri.service;

import com.nazri.model.Company;
import com.nazri.model.User;
import com.nazri.model.UserProduct;
import com.nazri.model.Warranty;
import com.nazri.repository.CompanyRepository;
import com.nazri.repository.UserProductRepository;
import com.nazri.repository.UserRepository;
import com.nazri.repository.WarrantyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarrantyServiceTest {

    @Mock
    WarrantyRepository warrantyRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CompanyRepository companyRepository;

    @Mock
    UserProductRepository userProductRepository;

    @InjectMocks
    WarrantyService warrantyService;

    private User sampleUser;
    private Company sampleCompany;
    private UserProduct sampleUserProduct;
    private Warranty sampleWarranty;

    @BeforeEach
    void setUp() {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        sampleCompany = new Company();
        sampleCompany.id = 10L;
        sampleCompany.setName("Apple");

        sampleUserProduct = new UserProduct();
        sampleUserProduct.id = 100L;

        sampleWarranty = new Warranty();
        sampleWarranty.id = 50L;
        sampleWarranty.setUser(sampleUser);
        sampleWarranty.setCompany(sampleCompany);
        sampleWarranty.setUserProduct(sampleUserProduct);
        sampleWarranty.setStartDate(LocalDate.of(2025, 1, 1));
        sampleWarranty.setEndDate(LocalDate.of(2026, 1, 1));
        sampleWarranty.setStatus(Warranty.WarrantyStatus.ACTIVE);
    }

    @Test
    void testFindById() {
        when(warrantyRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleWarranty));

        Optional<Warranty> result = warrantyService.findById(1L, 50L);

        assertTrue(result.isPresent());
        assertEquals(50L, result.get().id);
    }

    @Test
    void testFindByUserId() {
        when(warrantyRepository.findByUserId(1L)).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByCompanyId() {
        when(warrantyRepository.findByCompanyId(10L)).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findByCompanyId(10L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByStatus() {
        when(warrantyRepository.findByStatus(Warranty.WarrantyStatus.ACTIVE)).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findByStatus(Warranty.WarrantyStatus.ACTIVE);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByUserIdAndStatus() {
        when(warrantyRepository.findByUserIdAndStatus(1L, Warranty.WarrantyStatus.ACTIVE)).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findByUserIdAndStatus(1L, Warranty.WarrantyStatus.ACTIVE);

        assertEquals(1, result.size());
    }

    @Test
    void testFindExpiringWarranties() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(30);
        when(warrantyRepository.findExpiringWarranties(1L, start, end)).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findExpiringWarranties(1L, start, end);

        assertEquals(1, result.size());
    }

    @Test
    void testFindExpiredWarranties() {
        when(warrantyRepository.findExpiredWarranties()).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findExpiredWarranties();

        assertEquals(1, result.size());
    }

    @Test
    void testFindAllWarranties() {
        when(warrantyRepository.findAllWarranties()).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findAllWarranties();

        assertEquals(1, result.size());
    }

    @Test
    void testFindByUserProductId() {
        when(warrantyRepository.findByUserProductId(1L, 100L)).thenReturn(List.of(sampleWarranty));

        List<Warranty> result = warrantyService.findByUserProductId(1L, 100L);

        assertEquals(1, result.size());
    }

    @Test
    void testCreateWarranty_Success() {
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleUser));
        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleCompany));
        when(userProductRepository.findByIdOptional(100L)).thenReturn(Optional.of(sampleUserProduct));
        when(warrantyRepository.createWarranty(any(Warranty.class))).thenAnswer(inv -> inv.getArgument(0));

        sampleWarranty.setStatus(null); // test default status assignment
        Warranty created = warrantyService.createWarranty(sampleWarranty);

        assertNotNull(created);
        assertEquals(Warranty.WarrantyStatus.ACTIVE, created.getStatus());
        verify(warrantyRepository).createWarranty(sampleWarranty);
    }

    @Test
    void testCreateWarranty_MissingFields() {
        Warranty w = new Warranty();

        // User missing
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("User is required", ex.getMessage());

        w.setUser(sampleUser);
        // Company missing
        ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("Company is required", ex.getMessage());

        w.setCompany(sampleCompany);
        // UserProduct missing
        ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("User product is required", ex.getMessage());

        w.setUserProduct(sampleUserProduct);
        // Start date missing
        ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("Start date is required", ex.getMessage());

        w.setStartDate(LocalDate.of(2025, 1, 1));
        // End date missing
        ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("End date is required", ex.getMessage());
    }

    @Test
    void testCreateWarranty_EntitiesNotFound() {
        Warranty w = new Warranty();
        w.setUser(sampleUser);
        w.setCompany(sampleCompany);
        w.setUserProduct(sampleUserProduct);
        w.setStartDate(LocalDate.of(2025, 1, 1));
        w.setEndDate(LocalDate.of(2026, 1, 1));

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("User not found", ex.getMessage());

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleUser));
        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.empty());
        ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("Company not found", ex.getMessage());

        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleCompany));
        when(userProductRepository.findByIdOptional(100L)).thenReturn(Optional.empty());
        ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("User product not found", ex.getMessage());
    }

    @Test
    void testCreateWarranty_EndDateBeforeStartDate() {
        Warranty w = new Warranty();
        w.setUser(sampleUser);
        w.setCompany(sampleCompany);
        w.setUserProduct(sampleUserProduct);
        w.setStartDate(LocalDate.of(2026, 1, 1));
        w.setEndDate(LocalDate.of(2025, 1, 1));

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleUser));
        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleCompany));
        when(userProductRepository.findByIdOptional(100L)).thenReturn(Optional.of(sampleUserProduct));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.createWarranty(w));
        assertEquals("End date must be after start date", ex.getMessage());
    }

    @Test
    void testUpdateWarranty_Success() {
        when(warrantyRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleWarranty));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleUser));
        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleCompany));
        when(userProductRepository.findByIdOptional(100L)).thenReturn(Optional.of(sampleUserProduct));
        when(warrantyRepository.updateWarranty(any(Warranty.class))).thenAnswer(inv -> inv.getArgument(0));

        Warranty updated = warrantyService.updateWarranty(1L, sampleWarranty);

        assertNotNull(updated);
        verify(warrantyRepository).updateWarranty(sampleWarranty);
    }

    @Test
    void testUpdateWarranty_NotFoundOrNotBelongToUser() {
        when(warrantyRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.updateWarranty(1L, sampleWarranty));
        assertEquals("Warranty not found or does not belong to user", ex.getMessage());
    }

    @Test
    void testUpdateWarranty_NestedEntitiesNotFound() {
        when(warrantyRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleWarranty));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> warrantyService.updateWarranty(1L, sampleWarranty));
        assertEquals("User not found", ex1.getMessage());

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleUser));
        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> warrantyService.updateWarranty(1L, sampleWarranty));
        assertEquals("Company not found", ex2.getMessage());

        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleCompany));
        when(userProductRepository.findByIdOptional(100L)).thenReturn(Optional.empty());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> warrantyService.updateWarranty(1L, sampleWarranty));
        assertEquals("User product not found", ex3.getMessage());
    }

    @Test
    void testUpdateWarranty_InvalidDates() {
        when(warrantyRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleWarranty));
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleUser));
        when(companyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleCompany));
        when(userProductRepository.findByIdOptional(100L)).thenReturn(Optional.of(sampleUserProduct));
        sampleWarranty.setStartDate(LocalDate.of(2026, 1, 1));
        sampleWarranty.setEndDate(LocalDate.of(2025, 1, 1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> warrantyService.updateWarranty(1L, sampleWarranty));
        assertEquals("End date must be after start date", ex.getMessage());
    }

    @Test
    void testDeleteWarranty() {
        doNothing().when(warrantyRepository).deleteWarranty(1L, 50L);

        warrantyService.deleteWarranty(1L, 50L);

        verify(warrantyRepository).deleteWarranty(1L, 50L);
    }

    @Test
    void testIsWarrantyExpired() {
        when(warrantyRepository.isWarrantyExpired(sampleWarranty)).thenReturn(false);

        assertFalse(warrantyService.isWarrantyExpired(sampleWarranty));
    }
}
