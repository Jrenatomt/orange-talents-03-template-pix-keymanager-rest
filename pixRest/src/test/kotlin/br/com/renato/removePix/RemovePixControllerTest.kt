package br.com.renato.removePix

import br.com.pix.KeyManagerRemoveServiceGrpc
import br.com.pix.RemocaoChaveRequest
import br.com.pix.RemocaoChaveResponse
import br.com.renato.compartilhado.factory.GrpcClientFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RemovePixControllerTest {

    @field:Inject
    lateinit var removeStub: KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve remover chave quando dados validos`() {
        val idPix = UUID.randomUUID()
        val idCliente = UUID.randomUUID()
        val remocaoChaveRequest = remocaoChaveRequest(idPix.toString(), idCliente.toString())
        val remocaoChaveResponse = remocaoChaveResponse(idPix, idCliente)

        given(removeStub.removerChavePix(remocaoChaveRequest)).willReturn(remocaoChaveResponse)

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$idCliente/pix/$idPix")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    fun `deve retornar 404 quando chave nao for encontrada`() {
        val idPix = UUID.randomUUID()
        val idCliente = UUID.randomUUID()
        val remocaoChaveRequest = remocaoChaveRequest(idPix.toString(), idCliente.toString())

        given(removeStub.removerChavePix(remocaoChaveRequest)).willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$idCliente/pix/$idPix")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, thrown.status)
    }

    @Test
    fun `deve retornar 400 quando o id do cliente tiver formato invalido`() {
        val idPix = UUID.randomUUID()
        val idCliente = 123
        val remocaoChaveRequest = remocaoChaveRequest(idPix.toString(), idCliente.toString())

        given(removeStub.removerChavePix(remocaoChaveRequest)).willThrow(StatusRuntimeException(Status.INVALID_ARGUMENT))

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$idCliente/pix/$idPix")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)
    }

    private fun remocaoChaveRequest(idPix: String, idCliente: String): RemocaoChaveRequest {
        return RemocaoChaveRequest.newBuilder()
            .setIdPix(idPix)
            .setIdCliente(idCliente)
            .build()
    }

    private fun remocaoChaveResponse(idPix: UUID, idCliente: UUID): RemocaoChaveResponse {
        return RemocaoChaveResponse.newBuilder()
            .setIdPix(idPix.toString())
            .setIdCliente(idCliente.toString())
            .build()
    }


    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() =
            Mockito.mock(KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub::class.java)
    }
}