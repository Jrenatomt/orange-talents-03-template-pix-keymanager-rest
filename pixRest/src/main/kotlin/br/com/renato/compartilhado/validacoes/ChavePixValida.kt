package br.com.renato.compartilhado.validacoes

import br.com.renato.registaPix.NovaChavePixRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Constraint(validatedBy = [ChavePixValidaValidator::class])
annotation class ChavePixValida(
    val message: String = "Chave pix invalida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ChavePixValidaValidator : ConstraintValidator<ChavePixValida, NovaChavePixRequest> {

    override fun isValid(value: NovaChavePixRequest?,
                         annotationMetadata: AnnotationValue<ChavePixValida>,
                         context: ConstraintValidatorContext): Boolean {

        if (value?.tipoChave == null) {
            return false
        }

        return value.tipoChave.valida(value.chave)
    }
}