package ru.fensy.dev.configuration

import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import ru.fensy.dev.configuration.scalar.UploadScalar

/**
 * Конфигурация для graphQl
 */
@Configuration(proxyBeanMethods = false)
class GraphQlConfiguration {

    /**
     * Подключение расширения для скаляров
     */
    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder: RuntimeWiring.Builder ->
            wiringBuilder
                .scalar(ExtendedScalars.Json)
        }
    }

    @Bean
    fun uploadScalarConfigurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder ->
            wiringBuilder.scalar(UploadScalar.Upload)
        }
    }

}
