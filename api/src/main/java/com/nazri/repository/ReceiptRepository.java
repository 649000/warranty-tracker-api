package com.nazri.repository;

import com.nazri.model.Receipt;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReceiptRepository implements PanacheRepository<Receipt> {
    
    public Optional<Receipt> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }
    
    public List<Receipt> findByUserId(Long userId) {
        return list("userProduct.user.id", userId);
    }
    
    public List<Receipt> findByUserIdAndConfirmed(Long userId, Boolean isConfirmed) {
        return list("userProduct.user.id = ?1 and isConfirmed = ?2", userId, isConfirmed);
    }
    
    public Receipt createReceipt(Receipt receipt) {
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setUpdatedAt(LocalDateTime.now());
        persistAndFlush(receipt);
        return receipt;
    }
    
    public Receipt updateReceipt(Receipt receipt) {
        receipt.setUpdatedAt(LocalDateTime.now());
        getEntityManager().merge(receipt);
        return receipt;
    }
    
    public void deleteReceipt(Long id) {
        deleteById(id);
    }
}
