package ru.fensy.dev.repository

import java.nio.ByteBuffer
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.domain.Subscription

@Component
class SubscriptionsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun subscribe(subscriberId: Long, targetId: Long) =
        databaseClient.sql("insert into subscriptions (subscriber_id, target_id) values (:sub, :target)")
            .bind("sub", subscriberId)
            .bind("target", targetId)
            .fetch()
            .awaitRowsUpdated()

    suspend fun unsubscribe(subscriberId: Long, targetId: Long) =
        databaseClient.sql("delete from subscriptions where subscriber_id = :sub and target_id = :target")
            .bind("sub", subscriberId)
            .bind("target", targetId)
            .fetch()
            .awaitRowsUpdated()

    suspend fun isSubscribed(subscriberId: Long, targetId: Long): Boolean =
        databaseClient.sql("select exists (select 1 from subscriptions where subscriber_id = :sub and target_id = :target)")
            .bind("sub", subscriberId)
            .bind("target", targetId)
            .fetch()
            .one()
            .map { it["exists"] as Boolean }
            .awaitSingle()

    suspend fun getSubscriptions(subscriberId: Long, pageRequest: PageRequest): List<Subscription> =
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
                Subscription(
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

}
