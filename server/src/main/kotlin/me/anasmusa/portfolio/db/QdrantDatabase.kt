package me.anasmusa.portfolio.db

import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import io.qdrant.client.QueryFactory.nearest
import io.qdrant.client.ValueFactory.value
import io.qdrant.client.VectorsFactory.vectors
import io.qdrant.client.WithPayloadSelectorFactory.enable
import io.qdrant.client.grpc.Collections
import io.qdrant.client.grpc.Points
import io.qdrant.client.grpc.Points.PointStruct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.anasmusa.portfolio.ai.AI
import me.anasmusa.portfolio.core.AppJson
import java.io.File
import java.nio.ByteBuffer
import java.security.MessageDigest

object QdrantDatabase {

    private val qdrant = QdrantClient(
        QdrantGrpcClient.newBuilder("127.0.0.1", 6334, false)
            .build()
    )

    suspend fun init() = withContext(Dispatchers.IO){
        if (!qdrant.collectionExistsAsync("resume").get()){
            qdrant.createCollectionAsync(
                "resume",
                Collections.VectorParams.newBuilder()
                    .setSize(768)
                    .setDistance(Collections.Distance.Cosine)
                    .build()
            ).get()
        }

        if (qdrant.countAsync("resume").get() == 0L) {
            val folder = File("data")
            folder.mkdirs()
            val file = File(folder, "context_cache.json")
            if (file.exists()){
                insert(
                    AppJson.decodeFromString(file.readText())
                )
            }
        }
    }

    fun stringToLong(value: String): Long {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(value.toByteArray())
        return ByteBuffer.wrap(bytes.sliceArray(0..7)).long
    }

    suspend fun save(json: JsonObject): Boolean {
        val folder = File("data")
        folder.mkdirs()
        val file = File(folder, "context_cache.json")
        file.writeText(AppJson.encodeToString(json))
        return insert(json)
    }

    private suspend fun insert(json: JsonObject): Boolean = withContext(Dispatchers.IO) {
        try {
            val ids = json.map { stringToLong(it.key) }
            val removed = qdrant.queryAsync(
                Points.QueryPoints.newBuilder()
                    .setCollectionName("resume")
                    .build()
            ).get().filter {
                !ids.contains(it.id.num)
            }
            if (removed.isNotEmpty()) {
                qdrant.deleteAsync(
                    "resume",
                    removed.map { it.id }
                )
            }
            json.forEach { (key, element) ->
                val id = key
                val text = element.jsonPrimitive.toString()

                try {
                    val item = qdrant.queryAsync(
                        Points.QueryPoints.newBuilder()
                            .setCollectionName("resume")
                            .setQuery(nearest(id(stringToLong(id))))
                            .build()
                    ).get()?.firstOrNull()
                    if (item != null && item.payloadMap["text"]?.stringValue == text)
                        return@forEach
                } catch (_: Exception) {}

                val embedding = AI.embed(text)
                qdrant.upsertAsync(
                    "resume",
                    embedding.map {
                        PointStruct.newBuilder()
                            .setId(id(stringToLong(id)))
                            .setVectors(vectors(it))
                            .putPayload("text", value(text))
                            .build()
                    }
                ).get()
            }
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun find(vector: List<Float>): List<String>{
        return qdrant.searchAsync(
            Points.SearchPoints.newBuilder()
                .setCollectionName("resume")
                .addAllVector(vector)
                .setWithPayload(enable(true))
                .setLimit(8)
                .build())
            .get().map {
                it.payloadMap["text"]?.stringValue ?: ""
            }
    }

}