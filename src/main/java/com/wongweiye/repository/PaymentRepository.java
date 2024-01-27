package com.wongweiye.repository;

import com.wongweiye.dto.PaymentAndPaymentTransactionDTO;
import com.wongweiye.model.Payment;
import com.wongweiye.model.PaymentAndPaymentTransaction;
import com.wongweiye.model.PaymentTransactionAndFund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // There are three ways of doing Projections in Spring Data JPA,this repo is doing interface way
    // Projections means retrieve only specific views / column of the tables
    // 1. scalar value
    // 2. interface
    // 3. DTO

    // Projection by interface
    // the join on clause only work in native query, as native query is just the sql query return by Hibernate
    @Query(value = "Select p.id, f.fund from paymenttransaction p join fund f on p.paymentid = f.paymentId " +
            "where p.channelid = :channelId ", nativeQuery = true)
    List<PaymentTransactionAndFund> findByPaymentAndPaymentTransactionFund(@Param("channelId") Long channelId);

    // Projection by DTO
    // When using HQL / JPQL, join using on is not supported, unrelated entity join support via Hibernate > 5.1 version
    @Query("Select new com.wongweiye.dto.PaymentAndPaymentTransactionDTO(p, pt) from PaymentTransaction pt join pt.payment p where p.id = :paymentId ")
    List<PaymentAndPaymentTransactionDTO> findPaymentAndPaymentTransactionByDTO(@Param("paymentId") Long paymentId);

//    @Query(value = "Select p as paymentxn, f as funds from paymenttransaction p join fund f on p.paymentid = f.transactionpaymentid " +
//           "where p.channelid = :channelId ", nativeQuery = true)
//    List<PaymentTransactionAndFund> findByPaymentAndPaymentTransactionFund(@Param("channelId") Long channelId);

    @Query(value = "Select p.id, p.paymentNumber, pt.channelId from payment p join paymenttransaction pt on p.id = pt.paymentid " +
            "where p.id = ?1 ", nativeQuery = true)
    List<PaymentAndPaymentTransaction> GetPaymentAndPaymentTransaction(Long paymentId);

//    @Query("Select p.id, p.paymentNumber, pt.channelId from Payment p join PaymentTransaction pt on p.id = pt.payment.id " +
//            "where p.id = ?1 ")
//    List<PaymentAndPaymentTransaction> GetPaymentAndPaymentTransaction(Long paymentId);

}