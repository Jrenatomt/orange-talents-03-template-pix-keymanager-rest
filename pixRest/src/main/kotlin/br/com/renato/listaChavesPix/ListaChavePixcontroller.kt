package br.com.renato.listaChavesPix

import br.com.pix.KeyManagerConsultaServiceGrpc
import br.com.pix.KeyManagerListaServiceGrpc
import br.com.pix.ListaChavesRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{idCliente}/pix")
class ListaChavePixcontroller(@Inject private val gRpcClient: KeyManagerListaServiceGrpc.KeyManagerListaServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get
    fun listaChaves(@QueryValue idCliente: UUID): HttpResponse<Any> {

        logger.info("Consultando chaves do cliente: $idCliente")

        val response = gRpcClient.listaChaves(
            ListaChavesRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .build()
        )

        val chaves = response.chavesPixList.map { ChavePixResponse(it) }
        return HttpResponse.ok(chaves)
    }
}