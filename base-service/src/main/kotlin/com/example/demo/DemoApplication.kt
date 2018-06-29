package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.permanentRedirect
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToServerSentEvents
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import java.net.URI
import java.time.Duration
import javax.annotation.PostConstruct

//@EnableEurekaClient
@SpringBootApplication
@EnableConfigurationProperties(Props::class)
class DemoApplication(private val props: Props) {
    @PostConstruct
    fun init() {
        println("\n\n${props.some.propertyName}\n\n")
    }
}

@ConfigurationProperties
class Props {
    var test: String = "notSet"
    var some: Some = Some()

    class Some {
        var propertyName: String = "notSet"
    }
}

@Configuration
class SimpleConfig(private val handler: CustomerHandler) {

    @Bean
    fun routerFunctions() =
            router {
                accept(MediaType.TEXT_HTML).nest {
                    GET("/") { permanentRedirect(URI("index.html")).build() }
                    GET("/see") { ok().render("see") }
                    GET("/customers", handler::customersView)
                }
                "/api".nest {
                    accept(MediaType.APPLICATION_JSON)
                            .nest {
                                GET("/customers", handler::findAll)
                            }
                    accept(MediaType.TEXT_EVENT_STREAM)
                            .nest {
                                GET("/customers", handler::stream)
                            }
                }
                resources("/**", ClassPathResource("/static"))
            }
                    /*.filter { serverRequest, handlerFunction -> try {
                        handlerFunction.handle(serverRequest)
                    } catch (ex: Exception) { println(ex.message) }}*/

}

@Component
class CustomerHandler {
    private val userRepository = mapOf(
            "b1zdelnick" to Customer(name = "b1zdelnick", email = "kestama@list.ru", age = 31),
            "cilkobigballs" to Customer(name = "cilkobigballs", email = "ptsilko@list.ru", age = 20),
            "captainilya" to Customer(name = "captainilya", email = "iluha.dam.tebe.vuho@list.ru", age = 21))

    private val users = Flux.just(
            Customer(name = "b1zdelnick", email = "kestama@list.ru", age = 31),
            Customer(name = "cilkobigballs", email = "ptsilko@list.ru", age = 20),
            Customer(name = "captainilya", email = "iluha.dam.tebe.vuho@list.ru", age = 21))

    private val usersStream = Flux
            .zip(Flux.interval(Duration.ofSeconds(1)), users.repeat())
            .map { it.t2 }

    fun findAll(r: ServerRequest) = ok().body(users)

    fun stream(r: ServerRequest) = ok().bodyToServerSentEvents(usersStream)

    fun customersView(r: ServerRequest) = ok().render("customers", mapOf("customers" to users.map { it.name }))
}

data class Customer(val name: String, val email: String, val age: Int)

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
