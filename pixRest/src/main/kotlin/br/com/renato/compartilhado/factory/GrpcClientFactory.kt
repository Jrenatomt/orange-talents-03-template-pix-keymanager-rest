package br.com.renato.compartilhado.factory


import br.com.pix.KeyManagerConsultaServiceGrpc
import br.com.pix.KeyManagerRemoveServiceGrpc
import br.com.pix.KeyManagerServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
open class GrpcClientFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun registraChaveClientStub() = KeyManagerServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun deletaChaveClientStub() = KeyManagerRemoveServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun detalheChaveClientStub() = KeyManagerConsultaServiceGrpc.newBlockingStub(channel)

}