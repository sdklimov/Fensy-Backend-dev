package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.CollectionCover

@Component
class CollectionCoversRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun getByCollectionId(collectionId: Long): List<CollectionCover> =
        databaseClient.sql(
            """
            select * from collection_covers where collection_id = :collectionId
        """.trimIndent()
        )
            .bind("collectionId", collectionId)
            .fetch()
            .all()
            .map { CollectionCover(id = it["id"] as Long, image = it["image"] as ByteArray) }
            .collectList()
            .awaitSingle()


}
