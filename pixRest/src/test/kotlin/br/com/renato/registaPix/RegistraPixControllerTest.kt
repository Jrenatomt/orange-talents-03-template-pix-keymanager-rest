package br.com.renato.registaPix


import br.com.pix.KeyManagerServiceGrpc
import br.com.pix.RegistroChaveResponse
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest
internal class RegistraPixControllerTest() {

    @field:Inject
    lateinit var registraStub: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @AfterEach
    fun teardown() {
        Mockito.reset(registraStub)
    }

    @Test
    fun `deve registrar chave com dados validos`() {
        val chavePix = UUID.randomUUID().toString()
        val idPix = UUID.randomUUID().toString()
        val idCliente = UUID.randomUUID().toString()

        val respostaGrpc = grpcResponse(chavePix, idPix)

        given(registraStub.cadastroChavePix(Mockito.any())).willReturn(respostaGrpc)

        val novaChavePixRequest = novaChavePixRequest(
            TipoChaveRequest.EMAIL,
            "teste@teste.com.br",
            TipoContaRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/v1/clientes/$idCliente/pix", novaChavePixRequest)
        val response = client.toBlocking().exchange(request, NovaChavePixRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(idPix))
    }

    @Test
    fun `deve retornar 422 quando chave existir`() {
        val idCliente = UUID.randomUUID().toString()

        given(registraStub.cadastroChavePix(Mockito.any()))
            .willThrow(StatusRuntimeException(Status.ALREADY_EXISTS))

        val novaChavePixRequest = novaChavePixRequest(
            TipoChaveRequest.EMAIL,
            "teste@teste.com.br",
            TipoContaRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/v1/clientes/$idCliente/pix", novaChavePixRequest)
        val thrown = assertThrows<HttpClientResponseException> {
            (client.toBlocking().exchange(request, NovaChavePixRequest::class.java))
        }

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.status)
    }

    @Test
    fun `deve retornar 400 quando for informado algum dado invalido`() {
        val idCliente = UUID.randomUUID().toString()

        val novaChavePixRequest = novaChavePixRequest(
            TipoChaveRequest.EMAIL,
            "teste",
            TipoContaRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/v1/clientes/$idCliente/pix", novaChavePixRequest)
        val thrown = assertThrows<HttpClientResponseException> {
            (client.toBlocking().exchange(request, NovaChavePixRequest::class.java))
        }

        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)
    }


    private fun novaChavePixRequest(
        tipoChave: TipoChaveRequest,
        chave: String?,
        tipoContaRequest: TipoContaRequest
    ): NovaChavePixRequest? {
        return NovaChavePixRequest(tipoChave, chave, tipoContaRequest)
    }

    private fun grpcResponse(chavePix: String, idPix: String): RegistroChaveResponse? {
        return RegistroChaveResponse.newBuilder()
            .setChavePix(chavePix)
            .setIdPix(idPix)
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockFactory {
        @Singleton
        fun stubMock() =
            Mockito.mock(KeyManagerServiceGrpc.KeyManagerServiceBlockingStub::class.java)

    }
}

