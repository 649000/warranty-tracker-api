package com.nazri.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyTest {

    @Test
    void testGettersAndSetters() {
        Company company = new Company();
        company.id = 10L;
        company.setName("Acme Corp");
        company.setContactPhone("1-800-555-0199");
        company.setContactEmail("support@acme.com");
        company.setWebsite("https://acme.com");
        company.setAddress("123 Main St, Springfield");
        company.setClaimProcess("Online portal");
        company.setClaimUrl("https://acme.com/claims");
        company.setSupportHours("9am-5pm EST");
        company.setReturnInstructions("Ship with original packaging");

        LocalDateTime now = LocalDateTime.now();
        company.setCreatedAt(now);
        company.setUpdatedAt(now);
        company.setWarranties(new ArrayList<>());

        assertEquals(10L, company.id);
        assertEquals("Acme Corp", company.getName());
        assertEquals("1-800-555-0199", company.getContactPhone());
        assertEquals("support@acme.com", company.getContactEmail());
        assertEquals("https://acme.com", company.getWebsite());
        assertEquals("123 Main St, Springfield", company.getAddress());
        assertEquals("Online portal", company.getClaimProcess());
        assertEquals("https://acme.com/claims", company.getClaimUrl());
        assertEquals("9am-5pm EST", company.getSupportHours());
        assertEquals("Ship with original packaging", company.getReturnInstructions());
        assertEquals(now, company.getCreatedAt());
        assertEquals(now, company.getUpdatedAt());
        assertNotNull(company.getWarranties());
        assertTrue(company.toString().contains("Acme Corp"));
    }
}
