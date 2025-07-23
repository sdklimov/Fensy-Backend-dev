package ru.fensy.dev.service.payment.ton

import org.springframework.stereotype.Service
import org.ton.ton4j.address.Address
import org.ton.ton4j.smartcontract.SendMode
import org.ton.ton4j.smartcontract.types.Destination
import org.ton.ton4j.smartcontract.types.WalletV5Config
import org.ton.ton4j.smartcontract.wallet.v5.WalletV5
import org.ton.ton4j.tonlib.types.RawTransaction
import org.ton.ton4j.utils.Utils

@Service
class TonService(
    private val tonWallet: WalletV5,
) {

    fun send(targetWalletId: String, amountTon: Double, comment: String) {

        val walletV5Config =
            WalletV5Config.builder()
                .seqno(tonWallet.seqno)
                .walletId(tonWallet.walletId)
                .body(
                    tonWallet
                        .createBulkTransfer(
                            listOf(
                                Destination.builder()
                                    .bounce(false)
                                    .address(
                                        Address.of(targetWalletId).toString(true)
                                    )
                                    .sendMode(SendMode.PAY_GAS_SEPARATELY_AND_IGNORE_ERRORS)
                                    .amount(Utils.toNano(amountTon))
                                    .comment(comment)
                                    .build()
                            )
                        )
                        .toCell()
                )
                .build()

        val msg = tonWallet.prepareExternalMsg(walletV5Config)
        tonWallet.send(msg)
    }

    fun get(limit: Int): List<RawTransaction> {
        return tonWallet.getTransactions(limit)
    }

}
