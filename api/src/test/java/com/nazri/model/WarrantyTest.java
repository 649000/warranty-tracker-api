package com.nazri.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class WarrantyTest {

    @Test
    void testGettersAndSetters() {
        Warranty warranty = new Warranty();
        warranty.id = 20L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2027, 1, 1);
        warranty.setStartDate(start);
        warranty.setEndDate(end);
        warranty.setWarrantyPeriod(24);
        warranty.setWarrantyType("Manufacturer Limited");
        warranty.setNotes("Covers hardware defects");
        warranty.setStatus(Warranty.WarrantyStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        warranty.setCreatedAt(now);
        warranty.setUpdatedAt(now);

        User user = new User();
        user.id = 1L;
        warranty.setUser(user);

        Company company = new Company();
        company.id = 2L;
        warranty.setCompany(company);

        UserProduct userProduct = new UserProduct();
        userProduct.id = 3L;
        warranty.setUserProduct(userProduct);

        warranty.setClaims(new ArrayList<>());

        assertEquals(20L, warranty.id);
        assertEquals(start, warranty.getStartDate());
        assertEquals(end, warranty.getEndDate());
        assertEquals(24, warranty.getWarrantyPeriod());
        assertEquals("Manufacturer Limited", warranty.getWarrantyType());
        assertEquals("Covers hardware defects", warranty.getNotes());
        assertEquals(Warranty.WarrantyStatus.ACTIVE, warranty.getStatus());
        assertEquals(now, warranty.getCreatedAt());
        assertEquals(now, warranty.getUpdatedAt());
        assertEquals(user, warranty.getUser());
        assertEquals(company, warranty.getCompany());
        assertEquals(userProduct, warranty.getUserProduct());
        assertNotNull(warranty.getClaims());
        assertTrue(warranty.toString().contains("Manufacturer Limited"));
    }

    @Test
    void testWarrantyStatusEnum() {
        assertEquals(3, Warranty.WarrantyStatus.values().length);
        assertEquals(Warranty.WarrantyStatus.ACTIVE, Warranty.WarrantyStatus.valueOf("ACTIVE"));
        assertEquals(Warranty.WarrantyStatus.EXPIRED, Warranty.WarrantyStatus.valueOf("EXPIRED"));
        assertEquals(Warranty.WarrantyStatus.CANCELLED, Warranty.WarrantyStatus.valueOf("CANCELLED"));
    }
}
