package ru.fensy.dev.usecase.auth

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.constants.Constants.JTI_CLAIM_NAME
import ru.fensy.dev.graphql.controller.post.response.BaseResponse
import ru.fensy.dev.repository.RevokedTokensRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class LogoutUseCase(
    private val revokedTokensRepository: RevokedTokensRepository,
) : BaseUseCase() {

    suspend fun execute(): BaseResponse {
        val user = currentUser(required = true)!!
        val jti = getJwtClaims()[JTI_CLAIM_NAME] as String
        val exp =getJwtClaims()["exp"]

        val expiredAt = OffsetDateTime.ofInstant(exp as Instant, ZoneOffset.UTC)
        revokedTokensRepository.revoke(jti =jti, expiredAt = expiredAt, userId = user.id!!)
        return BaseResponse(message = "Токен отозван", success = true)
    }

}
