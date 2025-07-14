package ru.fensy.dev.repository

import java.nio.ByteBuffer
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.domain.PaymentStatus
import ru.fensy.dev.domain.Subscription
import ru.fensy.dev.domain.SubscriptionStatus
import ru.fensy.dev.domain.SubscriptionType
import ru.fensy.dev.domain.SubscriptionViewModel

@Component
class SubscriptionsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun createSubscription(rq: CreateSubscriptionRq): Long =
        databaseClient.sql(
            """
            insert into subscriptions (subscriber_id, target_id, 
            subscription_type, status, started_at, expires_at) 
            values (:subscriberId, :targetId, :subscriptionType, :status, :startedAt, :expiresAt)
            returning id
        """.trimIndent()
        )
            .bind("subscriberId", rq.subscriberId)
            .bind("targetId", rq.targetId)
            .bind("subscriptionType", rq.subscriptionType.name)
            .bind("status", rq.status.name)
            .bind("startedAt", rq.startedAt)
            .bind("expiresAt", rq.expiresAt)
            .fetch()
            .one()
            .map { it["id"] as Long }
            .awaitSingle()

    suspend fun getByUniqueId(uniqueId: UUID): Subscription? =
        databaseClient
            .sql(
                """
                select * from subscriptions
                where uniqueId = :uniqueId
            """.trimIndent()
            )
            .fetch()
            .one()
            .map {
                Subscription(
                    id = it["id"] as Long,
                    uniqueId = it["unique_id"] as UUID,
                    subscriberId = it["subscriber_id"] as Long,
                    targetId = it["target_id"] as Long,
                    subscriptionType = SubscriptionType.valueOf(it["subscription_type"] as String),
                    subscriptionStatus = SubscriptionStatus.valueOf(it["status"] as String),
                    startedAt = (it["started_at"] as OffsetDateTime).withOffsetSameInstant(ZoneOffset.UTC),
                    expiresAt = (it["expires_at"] as OffsetDateTime).withOffsetSameInstant(ZoneOffset.UTC),
                    createdAt = (it["created_at"] as OffsetDateTime).withOffsetSameInstant(ZoneOffset.UTC),
                    updatedAt = (it["updated_at"] as OffsetDateTime).withOffsetSameInstant(ZoneOffset.UTC),
                )
            }
            .awaitSingleOrNull()

    suspend fun unsubscribe(subscriberId: Long, targetId: Long) =
        databaseClient.sql("delete from subscriptions where subscriber_id = :sub and target_id = :target")
            .bind("sub", subscriberId)
            .bind("target", targetId)
            .fetch()
            .awaitRowsUpdated()

    suspend fun getSubscriptionWithStatus(subscriberId: Long, targetId: Long, status: SubscriptionStatus): Boolean =
        databaseClient.sql(
            "select exists (select 1 " +
                "from subscriptions " +
                "where subscriber_id = :sub " +
                "and target_id = :target" +
                "and status = :status )"
        )
            .bind("sub", subscriberId)
            .bind("target", targetId)
            .bind("status", status.name)
            .fetch()
            .one()
            .map { it["exists"] as Boolean }
            .awaitSingle()

    suspend fun getSubscriptions(subscriberId: Long, pageRequest: PageRequest): List<SubscriptionViewModel> =
        databaseClient
            .sql(
                """
       select u.id,
               u.username as "userName",
               u.full_name,
               u.avatar
            from subscriptions s
                     join
                 users u on s.target_id = u.id
            where s.subscriber_id = :subscriberId
            limit :limit offset :offset
            """.trimIndent()
            )
            .bind("subscriberId", subscriberId)
            .bind("limit", pageRequest.pageSize)
            .bind("offset", pageRequest.offset)
            .fetch()
            .all()
            .map {
                SubscriptionViewModel(
                    id = it["id"] as Long,
                    fullName = it["full_name"] as String,
                    userName = it["username"] as String,
                    avatar = it["avatar"] as? ByteBuffer,
                )
            }
            .collectList()
            .awaitSingle()

    // todo: Переделать на оконную count(*) over
    suspend fun countSubscriptions(subscriberId: Long): Long =
        databaseClient
            .sql(
                """
                     select count(*)
                from subscriptions s
                         join
                     users u on s.target_id = u.id
                where s.subscriber_id = :subscriberId;
            """.trimIndent()
            )
            .bind("subscriberId", subscriberId)
            .fetch()
            .one()
            .map {
                it["count"] as Long
            }
            .awaitSingle()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun updateStatus(subscriptionId: Long, status: SubscriptionStatus) {
        databaseClient
            .sql("""
                update subscriptions set status = :status, updated_at = now() where id = :id
            """.trimIndent())
            .bind("id", subscriptionId)
            .bind("status", status.name)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun updateStartedAt(subscriptionId: Long) {
        databaseClient
            .sql("""
                update subscriptions set started_at = now(), updated_at = now()
                 where id = :id
            """.trimIndent())
            .bind("id", subscriptionId)
            .fetch()
            .awaitRowsUpdated()
    }

}

data class CreateSubscriptionRq(
    val id: Long? = null,
    val subscriberId: Long,
    val targetId: Long,
    val subscriptionType: SubscriptionType,
    val status: SubscriptionStatus,
    val startedAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
)
