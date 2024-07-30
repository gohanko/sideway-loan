package com.example.sidewayloan.data

import java.math.BigDecimal

data class AmortisationRow(
    val paymentNumber: Int,
    val beginningBalance: BigDecimal,
    val monthlyRepayment: BigDecimal,
    val interestPaid: BigDecimal,
    val principalPaid: BigDecimal
)