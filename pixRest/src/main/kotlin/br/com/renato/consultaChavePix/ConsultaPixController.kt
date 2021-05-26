package br.com.renato.consultaChavePix

import br.com.pix.ConsultaChaveRequest
import br.com.pix.KeyManagerConsultaServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{idCliente}")
class ConsultaPixController(@Inject private val gRpcClient: KeyManagerConsultaServiceGrpc.KeyManagerConsultaServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/{idPix}")
    fun detalhaPix(@QueryValue idCliente: UUID, @QueryValue idPix: UUID): HttpResponse<Any> {

        logger.info("Consultando chave pix id: $idPix do cliente: $idCliente")

        val response = gRpcClient.consultaChave(
            ConsultaChaveRequest.newBuilder()
                .setPixEClienteId(
                    ConsultaChaveRequest.ConsultaPorPixEClienteId.newBuilder()
                        .setIdPix(idPix.toString())
                        .setIdCliente(idCliente.toString())
                ).build()
                )

        return HttpResponse.ok(DetalhesChavePixResponse(response))
    }
}