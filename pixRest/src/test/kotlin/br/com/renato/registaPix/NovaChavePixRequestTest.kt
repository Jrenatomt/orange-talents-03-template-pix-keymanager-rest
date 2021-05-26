package br.com.renato.registaPix

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class TipoChaveRequestTest {

    @Nested
    inner class Cpf {

        @ParameterizedTest
        @CsvSource("118.421.000-41", "12345678901", ",", "''")
        fun `valida tipo CPF deve retornar false quando for cpf invalido`(cpf: String?) {
            val result = TipoChaveRequest.CPF.valida(cpf)
            assertFalse(result)
        }
        @Test
        fun `valida tipo CPF deve retornar true quando for cpf valido`() {
            val result = TipoChaveRequest.CPF.valida("11842100041")
            assertTrue(result)
        }
    }
    @Nested
    inner class Celular {
        @ParameterizedTest
        @CsvSource("12345678", "+ojsjdghsia", "+55(85)98871-4077", ",", "''")
        fun `valida tipo Celular deve retornar false quando for celular invalido`(celular: String?) {
            val result = TipoChaveRequest.CELULAR.valida(celular)
            assertFalse(result)
        }
        @Test
        fun `valida tipo Celular deve retornar true quando for celular valido`() {
            val result = TipoChaveRequest.CELULAR.valida("+5585988714077")
            assertTrue(result)
        }
    }
    @Nested
    inner class Email {
        @ParameterizedTest
        @CsvSource("email.email.com","teste@", ",", "''")
        fun `valida tipo Email deve retornar false quando email invalido`(email: String?) {
            val result = TipoChaveRequest.EMAIL.valida(email)
            assertFalse(result)
        }
        @Test
        fun `valida tipo Email deve retornar true quando email valido`() {
            val result = TipoChaveRequest.EMAIL.valida("email@email.com")
            assertTrue(result)
        }
    }
    @Nested
    inner class Aleatoria {
        @ParameterizedTest
        @CsvSource("email@email.com", "1234566", "texto")
        fun `valida tipo Aleatoria deve retornar false quando for preenchida`(chave: String) {
            val result = TipoChaveRequest.ALEATORIA.valida(chave)
            assertFalse(result)
        }
        @ParameterizedTest
        @CsvSource(",", "''")
        fun `valida tipo Aleatoria retorna true quando chave nao for preenchida`(chave: String?) {
            val result = TipoChaveRequest.ALEATORIA.valida(chave)
            assertTrue(result)
        }
    }
}