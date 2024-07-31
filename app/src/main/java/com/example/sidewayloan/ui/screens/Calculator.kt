package com.example.sidewayloan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.navigation.NavHostController
import com.example.sidewayloan.data.Loan
import com.example.sidewayloan.data.LoanType
import com.example.sidewayloan.data.user_settings.UserSettings
import com.example.sidewayloan.ui.composables.CalculatorResultsBottomSheet
import com.example.sidewayloan.ui.composables.CalculatorTopAppBar
import com.example.sidewayloan.ui.composables.ChipGroup
import com.example.sidewayloan.ui.composables.DateTextField
import com.example.sidewayloan.utils.convertDateToMillis
import com.example.sidewayloan.utils.convertMillisToDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

@Composable
fun CalculatorScreen(
    navHostController: NavHostController,
) {
    var type by remember { mutableStateOf(LoanType.PERSONAL) }
    var amount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var numberOfInstalment by remember { mutableStateOf("") }

    val currentTime = System.currentTimeMillis()
    var startDate by remember { mutableStateOf(convertMillisToDate(currentTime)) }

    var showBottomSheet by remember { mutableStateOf(false) }

    fun calculateMaximumTenure(type: LoanType, birthday: Long): Int {
        val instant = Instant.ofEpochMilli(birthday)
        val age = Period.between(
            instant.atZone(ZoneId.of("utc")).toLocalDate(),
            LocalDate.now()
        ).years

        val defaultMaxTenure = when(type) {
            LoanType.PERSONAL -> 10
            LoanType.HOUSING -> 35
        }

        val defaultMaxAge = when(type) {
            LoanType.PERSONAL -> 60
            LoanType.HOUSING -> 75
        }

        val ageDifference = defaultMaxAge - age
        if (ageDifference <= 0 || age > defaultMaxAge) {
            return 0
        }

        if (ageDifference < defaultMaxTenure) {
            return ageDifference * 12
        }

        if (ageDifference > defaultMaxTenure) {
            return defaultMaxTenure * 12
        }

        return 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CalculatorTopAppBar { navHostController.popBackStack() }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .imePadding()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Loan Type Dropdown
            Text(text = "Select Loan Type")

            ChipGroup(
                LoanType.entries,
                onValueChange = {
                    type = LoanType.valueOf(it)
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Loan Amount") },
                value = amount,
                onValueChange = {
                    amount = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Interest Rate (% per annum)") },
                value = interestRate,
                onValueChange = {
                    interestRate = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Loan Tenure (months)") },
                value = numberOfInstalment,
                onValueChange = {
                    numberOfInstalment = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            DateTextField(
                modifier = Modifier.fillMaxWidth(),
                initialSelectedDate = startDate,
                onSelectDate = {
                    startDate = it
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showBottomSheet = true
                    }
                ) {
                    Text(text = "Calculate")
                }
            }
        }

        if (showBottomSheet) {
            CalculatorResultsBottomSheet(
                loan = Loan(
                    type = type,
                    amount = amount.toFloat(),
                    interestRate = interestRate.toFloat(),
                    numberOfInstalment = numberOfInstalment.toInt(),
                    startDateUnixTime = convertDateToMillis(startDate)
                ),
                onDismissRequest = {
                    showBottomSheet = false
                }
            )
        }
    }
}
