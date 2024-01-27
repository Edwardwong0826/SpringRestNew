package com.wongweiye.dto;

import com.wongweiye.model.Fund;
import com.wongweiye.model.Payment;
import com.wongweiye.model.PaymentTransaction;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAndFundDTO {

    Payment payment;
    Fund fund;
}
