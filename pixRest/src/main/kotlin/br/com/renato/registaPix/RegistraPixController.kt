package br.com.renato.registaPix


import br.com.pix.KeyManagerServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{idCliente}")
class RegistraPixController(@Inject private val gRpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun registra(@PathVariable idCliente: UUID, @Valid @Body request: NovaChavePixRequest): HttpResponse<Any> {

        logger.info("Criando chave pix do cliente: $idCliente")
        val response = gRpcClient.cadastroChavePix(request.paraModeloGrpc(idCliente))

        return HttpResponse.created(location(idCliente, response.idPix))
    }

    private fun location(idCliente: UUID, idPix: String) = HttpResponse.uri("/api/v1/clientes/$idCliente/pix/$idPix")
}
