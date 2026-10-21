package com.nazri.service;

import com.nazri.model.Product;
import com.nazri.model.User;
import com.nazri.model.UserProduct;
import com.nazri.repository.UserProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProductServiceTest {

    @Mock
    UserProductRepository userProductRepository;

    @InjectMocks
    UserProductService userProductService;

    private User sampleUser;
    private Product sampleProduct;
    private UserProduct sampleUserProduct;

    @BeforeEach
    void setUp() {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        sampleProduct = new Product();
        sampleProduct.id = 10L;

        sampleUserProduct = new UserProduct();
        sampleUserProduct.id = 100L;
        sampleUserProduct.setUser(sampleUser);
        sampleUserProduct.setProduct(sampleProduct);
        sampleUserProduct.setSerialNumber("SN-12345");
    }

    @Test
    void testFindByIdAndUserId() {
        when(userProductRepository.findByIdAndUserId(100L, 1L)).thenReturn(sampleUserProduct);

        Optional<UserProduct> result = userProductService.findByIdAndUserId(100L, 1L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().id);
    }

    @Test
    void testFindById() {
        when(userProductRepository.findById(100L)).thenReturn(sampleUserProduct);

        Optional<UserProduct> result = userProductService.findById(100L);

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUserId() {
        when(userProductRepository.findByUserId(1L)).thenReturn(List.of(sampleUserProduct));

        List<UserProduct> result = userProductService.findByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByProductId() {
        when(userProductRepository.findByProductId(10L)).thenReturn(List.of(sampleUserProduct));

        List<UserProduct> result = userProductService.findByProductId(10L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindBySerialNumber() {
        when(userProductRepository.findBySerialNumber("SN-12345")).thenReturn(Optional.of(sampleUserProduct));

        Optional<UserProduct> result = userProductService.findBySerialNumber("SN-12345");

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUserIdAndProductId() {
        when(userProductRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(List.of(sampleUserProduct));

        List<UserProduct> result = userProductService.findByUserIdAndProductId(1L, 10L);

        assertEquals(1, result.size());
    }

    @Test
    void testCreateUserProduct_Success() {
        when(userProductRepository.existsByUserIdAndSerialNumber(1L, "SN-12345")).thenReturn(false);
        when(userProductRepository.createUserProduct(any(UserProduct.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProduct created = userProductService.createUserProduct(sampleUserProduct);

        assertNotNull(created);
        verify(userProductRepository).createUserProduct(sampleUserProduct);
    }

    @Test
    void testCreateUserProduct_DuplicateSerialNumberThrowsException() {
        when(userProductRepository.existsByUserIdAndSerialNumber(1L, "SN-12345")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userProductService.createUserProduct(sampleUserProduct);
        });

        assertEquals("User product with this serial number already exists for this user", ex.getMessage());
        verify(userProductRepository, never()).createUserProduct(any());
    }

    @Test
    void testUpdateUserProduct_SuccessSameSerialNumber() {
        when(userProductRepository.findByIdAndUserId(100L, 1L)).thenReturn(sampleUserProduct);
        when(userProductRepository.updateUserProduct(any(UserProduct.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProduct updated = userProductService.updateUserProduct(sampleUserProduct, 1L);

        assertNotNull(updated);
        verify(userProductRepository).updateUserProduct(sampleUserProduct);
    }

    @Test
    void testUpdateUserProduct_SuccessNewUniqueSerialNumber() {
        UserProduct existing = new UserProduct();
        existing.id = 100L;
        existing.setSerialNumber("SN-OLD");

        when(userProductRepository.findByIdAndUserId(100L, 1L)).thenReturn(existing);
        when(userProductRepository.existsByUserIdAndSerialNumber(1L, "SN-12345")).thenReturn(false);
        when(userProductRepository.updateUserProduct(any(UserProduct.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProduct updated = userProductService.updateUserProduct(sampleUserProduct, 1L);

        assertNotNull(updated);
        verify(userProductRepository).updateUserProduct(sampleUserProduct);
    }

    @Test
    void testUpdateUserProduct_DuplicateNewSerialNumberThrowsException() {
        UserProduct existing = new UserProduct();
        existing.id = 100L;
        existing.setSerialNumber("SN-OLD");

        when(userProductRepository.findByIdAndUserId(100L, 1L)).thenReturn(existing);
        when(userProductRepository.existsByUserIdAndSerialNumber(1L, "SN-12345")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userProductService.updateUserProduct(sampleUserProduct, 1L);
        });

        assertEquals("User product with this serial number already exists for this user", ex.getMessage());
    }

    @Test
    void testUpdateUserProduct_NotFoundThrowsException() {
        when(userProductRepository.findByIdAndUserId(100L, 1L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userProductService.updateUserProduct(sampleUserProduct, 1L);
        });

        assertEquals("User product not found or does not belong to user", ex.getMessage());
    }

    @Test
    void testDeleteUserProduct_Found() {
        when(userProductRepository.findByIdAndUserId(100L, 1L)).thenReturn(sampleUserProduct);
        doNothing().when(userProductRepository).deleteUserProduct(100L);

        userProductService.deleteUserProduct(100L, 1L);

        verify(userProductRepository).deleteUserProduct(100L);
    }

    @Test
    void testDeleteUserProduct_NotFound() {
        when(userProductRepository.findByIdAndUserId(100L, 1L)).thenReturn(null);

        userProductService.deleteUserProduct(100L, 1L);

        verify(userProductRepository, never()).deleteUserProduct(anyLong());
    }

    @Test
    void testExistsBySerialNumber() {
        when(userProductRepository.existsBySerialNumber("SN-123")).thenReturn(true);
        assertTrue(userProductService.existsBySerialNumber("SN-123"));
    }

    @Test
    void testExistsByUserIdAndSerialNumber() {
        when(userProductRepository.existsByUserIdAndSerialNumber(1L, "SN-123")).thenReturn(true);
        assertTrue(userProductService.existsByUserIdAndSerialNumber(1L, "SN-123"));
    }
}
