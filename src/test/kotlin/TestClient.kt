import com.datastax.astra.devops_v2.apis.DBOperationsApi
import com.datastax.astra.jetbrains.utils.AstraClientBase
import com.datastax.astra.stargate_document_v2.apis.DocumentsApi
import com.datastax.astra.stargate_rest_v2.apis.DataApi
import com.datastax.astra.stargate_rest_v2.apis.SchemasApi

object TestClient: AstraClientBase()
    {
        fun setToken(token: String){
            accessToken=token
        }

}