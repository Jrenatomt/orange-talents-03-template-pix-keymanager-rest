package br.com.renato.removePix

import br.com.pix.KeyManagerRemoveServiceGrpc
import br.com.pix.RemocaoChaveRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{idCliente}")
class RemovePixController(@Inject private val grpcClient: KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Delete("/pix/{idPix}")
    fun remove(@PathVariable idCliente: UUID, idPix: UUID): HttpResponse<Any>{

        logger.info("Removendo chave pix id: $idPix do cliente: $idCliente")

        grpcClient.removerChavePix(
            RemocaoChaveRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .setIdPix(idPix.toString())
                .build())

        return HttpResponse.ok()
    }
}
