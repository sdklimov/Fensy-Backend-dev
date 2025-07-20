package ru.fensy.dev.configuration.ton

import com.iwebpp.crypto.TweetNaclFast
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.mnemonic.Mnemonic
import org.ton.ton4j.smartcontract.wallet.v4.WalletV4R2
import org.ton.ton4j.smartcontract.wallet.v5.WalletV5
import org.ton.ton4j.tonlib.Tonlib
import org.ton.ton4j.tonlib.types.VerbosityLevel
import org.ton.ton4j.utils.Utils

@Configuration(proxyBeanMethods = false)
class TonWalletProxyConfiguration(
    @Value("\${application.ton.wallet.mnemonics}")
    private val mnemonics: String,
    @Value("\${application.ton.provider.url}")
    private val url: String,
    @Value("\${application.ton.provider.api-key}")
    private val apiKey: String,
) {

    @Bean
    fun tonWallet(): Boolean {
//        val tonLib = Tonlib
//            .builder()
//            .pathToTonlibSharedLib(Utils.getTonlibGithubUrl())
//            .verbosityLevel(Verm.FATAL)
//            .build()


        val mnemonic = listOf(
            "budget", "kitten", "lamp", "century", "utility", "already", "rotate", "front",
            "embark", "glimpse", "sudden", "snow", "bitter", "balance", "tilt", "second",
            "unusual", "mushroom", "silent", "medal", "finish", "odor", "fine", "time"
        )
        val keyPair = TweetNaclFast.Signature.keyPair_fromSeed(Mnemonic.toSeed(mnemonic))


        val tonlib = Tonlib.builder()
            .pathToTonlibSharedLib(Utils.getTonlibGithubUrl())
            .pathToGlobalConfig(Utils.getGlobalConfigUrlMainnet())
            .testnet(false)
            .verbosityLevel(VerbosityLevel.DEBUG)
            .build()

        val wallet = WalletV5
            .builder()
            .wc(0)
            .walletId(2147483409L)
            .keyPair(keyPair)
            .isSigAuthAllowed(true)
            .tonlib(tonlib)
            .build()

        val address = wallet.address.toNonBounceable()

        println(address)


        return true
    }
//
//    fun tonAddress(): Address {
//        Address.of()
//    }

}


//class RateLimiter(
//    private val limit: Int,
//) {
//    private var start = System.currentTimeMillis()
//    var count = AtomicInteger(0)
//
//    fun tryConsume(): Boolean {
//        val now = System.currentTimeMillis()
//
//        if (now - start >= 3000) {
//            count.set(0)
//            start = now
//        }
//
//        if (count.get() < limit) {
//            count.incrementAndGet()
//            return true
//        }
//
//        return false
//
//    }
//
//}
//
//fun main() {
//    val rl = RateLimiter(3)
//    while (true) {
//        println(rl.tryConsume())
//        sleep(300)
//    }
//}


