package com.wongweiye.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
@Getter
@Setter
@ToString
@Entity
@Table(name = "paymenttransaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paymentid")
    private Payment payment;

    @Column(name = "channelid")
    private long channelId;

}