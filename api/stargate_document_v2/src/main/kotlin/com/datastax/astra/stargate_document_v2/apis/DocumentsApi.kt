package com.datastax.astra.stargate_document_v2.apis

import com.datastax.astra.stargate_document_v2.infrastructure.CollectionFormats.*
import com.datastax.astra.stargate_document_v2.models.DocumentResponseWrapper
import com.datastax.astra.stargate_document_v2.models.InlineResponse200
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface DocumentsApi {
    /**
     * Add a new document to {collection-id}
     *
     * Responses:
     *  - 201: resource created
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 409: Conflict
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param body document
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @POST("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}")
    @Headers("Accept: application/json", "Content-Type: application/json")
    suspend fun addDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Body body: RequestBody, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>

    /**
     * Add a new document to {collection-id}
     *
     * Responses:
     *  - 201: resource created
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 409: Conflict
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param body name of the document collection
     * @return [Unit]
     */
    @POST("api/rest/v2/namespaces/{namespace-id}/collections")
    @Headers("Accept: application/json", "Content-Type: application/json")
    suspend fun createCollection(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Body body: RequestBody): Response<Unit>

    /**
     * Delete a collection
     *
     * Responses:
     *  - 204: resource deleted
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @DELETE("api/rest/v2/schemas/namespaces/{namespace-id}/collections/{collection-id}")
    suspend fun deleteCollectionSchema(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>

    /**
     * Delete a  document
     *
     * Responses:
     *  - 204: resource deleted
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @DELETE("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}")
    suspend fun deleteDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>

    /**
     * Delete a sub document by {document-path}
     *
     * Responses:
     *  - 204: resource deleted
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param documentPath a JSON path
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @DELETE("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path}")
    suspend fun deleteSubDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Path("document-path") documentPath: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>

    /**
     * Get a collection
     *
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param pretty format results (optional)
     * @param raw unwrap results (optional)
     * @return [DocumentResponseWrapper]
     */
    @GET("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}")

    suspend fun getCollection(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null, @Query("raw") raw: kotlin.Boolean? = null, @Query("page-size") pageSize: String): Response<DocumentResponseWrapper>

    /**
     * Get a document by {document-id}
     *
     * Responses:
     *  - 200:
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param pretty format results (optional)
     * @param fields URL escaped, comma delimited list of keys to include (optional)
     * @param raw unwrap results (optional)
     * @return [DocumentResponseWrapper]
     */
    @GET("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}")
    suspend fun getDocById(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null, @Query("fields") fields: kotlin.String? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<DocumentResponseWrapper>

    /**
     * Get a sub document by {document-path}
     *
     * Responses:
     *  - 200:
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param documentPath a JSON path
     * @param pretty format results (optional)
     * @param fields URL escaped, comma delimited list of keys to include (optional)
     * @param raw unwrap results (optional)
     * @return [Unit]
     */
    @GET("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path}")
    suspend fun getSubDocByPath(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Path("document-path") documentPath: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null, @Query("fields") fields: kotlin.String? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<Unit>

    /**
     * List collections in a namespace
     *
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param pretty format results (optional)
     * @param raw unwrap results (optional)
     * @return [InlineResponse200]
     */
    @GET("api/rest/v2/namespaces/{namespace-id}/collections")
    suspend fun listCollections(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse200>

    /**
     * Replace a document
     *
     * Responses:
     *  - 200: resource updated
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param body document
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @PUT("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}")
    @Headers("Accept: application/json", "Content-Type: application/json")
    suspend fun replaceDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Body body: RequestBody, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>

    /**
     * Replace a sub document
     *
     * Responses:
     *  - 201: resource created
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param documentPath a JSON path
     * @param body document
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @PUT("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path}")
    suspend fun replaceSubDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Path("document-path") documentPath: kotlin.String, @Body body: kotlin.Any, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>

    /**
     * Search for documents in {collection-id}
     *
     * Responses:
     *  - 200:
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param pretty format results (optional)
     * @param where URL escaped JSON query using the following keys:  | Key | Operation | |-|-| | $lt | Less Than | | $lte | Less Than Or Equal To | | $gt | Greater Than | | $gte | Greater Than Or Equal To | | $ne | Not Equal To | | $in | Contained In | | $exists | A value is set for the key | | $select | This matches a value for a key in the result of a different query | | $dontSelect | Requires that a key’s value not match a value for a key in the result of a different query | | $all | Contains all of the given values | | $regex | Requires that a key’s value match a regular expression | | $text | Performs a full text search on indexed fields |  (optional)
     * @param fields URL escaped, comma delimited list of keys to include (optional)
     * @param pageSize restrict the number of returned items (max 100) (optional)
     * @param pageState move the cursor to a particular result (optional)
     * @param sort keys to sort by (optional)
     * @param raw unwrap results (optional)
     * @return [DocumentResponseWrapper]
     */
    @GET("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}")
    suspend fun searchDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null, @Query("where") where: kotlin.Any? = null, @Query("fields") fields: kotlin.String? = null, @Query("page-size") pageSize: kotlin.Int? = null, @Query("page-state") pageState: kotlin.String? = null, @Query("sort") sort: kotlin.Any? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<DocumentResponseWrapper>

    /**
     * Update part of a document
     *
     * Responses:
     *  - 200: resource updated
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param body document
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @PATCH("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}")
    @Headers("Accept: application/json", "Content-Type: application/json")
    suspend fun updatePartOfDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Body body: RequestBody, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>

    /**
     * Update part of a sub document by {document-path}
     *
     * Responses:
     *  - 200: resource updated
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param collectionId name of the document collection
     * @param documentId the id of the document
     * @param documentPath a JSON path
     * @param body document
     * @param pretty format results (optional)
     * @return [Unit]
     */
    @PATCH("api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path}")
    suspend fun updatePartOfSubDoc(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Path("collection-id") collectionId: kotlin.String, @Path("document-id") documentId: kotlin.String, @Path("document-path") documentPath: kotlin.String, @Body body: kotlin.Any, @Query("pretty") pretty: kotlin.Boolean? = null): Response<Unit>
}
