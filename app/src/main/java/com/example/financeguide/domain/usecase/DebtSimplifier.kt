package com.example.financeguide.domain.usecase

import kotlin.math.abs
import kotlin.math.min

data class SimplifiedDebt(
    val fromMemberId: Long,
    val fromMemberName: String,
    val toMemberId: Long,
    val toMemberName: String,
    val amount: Double
)

class DebtSimplifier {

    fun simplifyDebts(memberBalances: Map<Long, Pair<String, Double>>): List<SimplifiedDebt> {
        val simplifiedList = mutableListOf<SimplifiedDebt>()
        
        // Make a mutable copy of net balances
        val netBalances = memberBalances.mapValues { it.value.second }.toMutableMap()
        val memberNames = memberBalances.mapValues { it.value.first }

        while (true) {
            // Find max debtor (minimum net balance < 0)
            var minDebtorId: Long? = null
            var minBalance = -0.01

            for ((id, balance) in netBalances) {
                if (balance < minBalance) {
                    minBalance = balance
                    minDebtorId = id
                }
            }

            // Find max creditor (maximum net balance > 0)
            var maxCreditorId: Long? = null
            var maxBalance = 0.01

            for ((id, balance) in netBalances) {
                if (balance > maxBalance) {
                    maxBalance = balance
                    maxCreditorId = id
                }
            }

            // If no debtor or creditor found, all debts are settled
            if (minDebtorId == null || maxCreditorId == null) {
                break
            }

            val settleAmount = min(abs(minBalance), maxBalance)

            val fromName = memberNames[minDebtorId] ?: "Member $minDebtorId"
            val toName = memberNames[maxCreditorId] ?: "Member $maxCreditorId"

            simplifiedList.add(
                SimplifiedDebt(
                    fromMemberId = minDebtorId,
                    fromMemberName = fromName,
                    toMemberId = maxCreditorId,
                    toMemberName = toName,
                    amount = settleAmount
                )
            )

            netBalances[minDebtorId] = (netBalances[minDebtorId] ?: 0.0) + settleAmount
            netBalances[maxCreditorId] = (netBalances[maxCreditorId] ?: 0.0) - settleAmount
        }

        return simplifiedList
    }
}
