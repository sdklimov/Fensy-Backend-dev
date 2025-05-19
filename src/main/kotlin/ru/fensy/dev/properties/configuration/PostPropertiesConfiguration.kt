package ru.fensy.dev.properties.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import ru.fensy.dev.properties.PostProperties

/**
 * Настройка свойств [PostProperties]
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PostProperties::class)
class PostPropertiesConfiguration