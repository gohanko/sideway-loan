package com.example.sidewayloan.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.breens.beetablescompose.BeeTablesCompose
import com.example.sidewayloan.data.database.loan.Loan
import com.example.sidewayloan.data.database.loan.LoanType
import com.example.sidewayloan.utils.getHousingLoanTableDataset
import com.example.sidewayloan.utils.getLastPaymentDate
import com.example.sidewayloan.utils.getMonthlyInstalment
import com.example.sidewayloan.utils.getPersonalLoanTableDataset
import com.example.sidewayloan.utils.getTotalAmountPaid

@Composable
fun CalculationResult(
    loan: Loan
) {
    var showAmortisationTable by remember { mutableStateOf(false) }

    val monthlyInstallment = getMonthlyInstalment(loan)
    Text(text = "Monthly Instalment: $monthlyInstallment")

    val lastRepaymentDate = getLastPaymentDate(loan)
    Text(text="Final Payment Date: $lastRepaymentDate")

    val totalAmountPaid = getTotalAmountPaid(loan)

    Text(text="Total Amount Paid: $totalAmountPaid")

    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Show Amortisation Table"
        )

        Spacer(Modifier.weight(1f))

        Switch(
            checked = showAmortisationTable,
            onCheckedChange = { showAmortisationTable = !showAmortisationTable }
        )
    }

    if (showAmortisationTable) {
        val headerTitles = listOf("Payment Number", "Beginning Balance (RM)", "Monthly Repayment (RM)", "Interest Paid (RM)", "Principal Paid (RM)")
        BeeTablesCompose(
            data = if (loan.type == LoanType.PERSONAL) getPersonalLoanTableDataset(loan) else getHousingLoanTableDataset(loan),
            headerTableTitles = headerTitles
        )
    }
}