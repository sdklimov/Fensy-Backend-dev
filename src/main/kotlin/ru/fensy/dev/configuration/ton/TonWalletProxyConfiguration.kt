package ru.fensy.dev.configuration.ton

import com.iwebpp.crypto.TweetNaclFast
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.mnemonic.Mnemonic
import org.ton.ton4j.smartcontract.wallet.v5.WalletV5
import org.ton.ton4j.tonlib.Tonlib
import org.ton.ton4j.tonlib.types.VerbosityLevel
import org.ton.ton4j.utils.Utils
import java.io.File


@Configuration(proxyBeanMethods = false)
class TonWalletProxyConfiguration(
    @Value("\${application.ton.wallet.mnemonics}")
    private val mnemonics: String,
//    @Value("\${application.ton.provider.url}")
//    private val url: String,
//    @Value("\${application.ton.provider.api-key}")
//    private val apiKey: String,
) {

    private fun getTonlibPath(): String {
        val dockerPath = "/app/tonlibjson-linux-x86_64.so"
        return if (File(dockerPath).exists()) {
            dockerPath
        } else {
            Utils.getTonlibGithubUrl()
        }
    }

    @Bean
    fun tonLib(): Tonlib = Tonlib.builder()
        .pathToTonlibSharedLib(getTonlibPath())
        .pathToGlobalConfig(Utils.getGlobalConfigUrlMainnet())
        .testnet(false)
        .verbosityLevel(VerbosityLevel.DEBUG)
        .build()

    @Bean
    fun tonWallet(tonLib: Tonlib): WalletV5 {

        val keyPair = TweetNaclFast.Signature.keyPair_fromSeed(Mnemonic.toSeed(mnemonics.split(",").map { it.trim() }))
        val tonlib = Tonlib.builder()
            .pathToTonlibSharedLib(getTonlibPath())
            .pathToGlobalConfig(Utils.getGlobalConfigUrlMainnet())
            .testnet(false)
            .verbosityLevel(VerbosityLevel.FATAL)
            .build()

        return WalletV5
            .builder()
            .wc(0)
            .walletId(2147483409L)
            .keyPair(keyPair)
            .isSigAuthAllowed(true)
            .tonlib(tonlib)
            .build()

//        val walletV5Config =
//            WalletV5Config.builder()
//                .seqno(wallet.seqno)
//                .walletId(wallet.walletId)
//                .body(
//                    wallet
//                        .createBulkTransfer(
//                            listOf(
//                                Destination.builder()
//                                    .bounce(false)
//                                    .address(
//                                        Address.of("UQAyxJF21V_nbpkDf6W-Bo0DIQdeBv4e_qCR90gTSw9yBN-G").toString(true)
//                                    )
//                                    .sendMode(SendMode.PAY_GAS_SEPARATELY_AND_IGNORE_ERRORS)
//                                    .amount(Utils.toNano(0.05))
//                                    .comment("На подарочек катенкий")
//                                    .build()
//                            )
//                        )
//                        .toCell()
//                )
//                .build()
//
//        val msg = wallet.prepareExternalMsg(walletV5Config)
//        wallet.send(msg)
//
//        return true
    }

}
