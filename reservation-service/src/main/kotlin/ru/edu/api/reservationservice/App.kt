package ru.edu.api.reservationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity.ok
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import reactor.core.publisher.toFlux

@SpringBootApplication
@EnableDiscoveryClient
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

@Configuration
class AppConfig {
    @Bean
    @LoadBalanced
    fun restTemplate() = RestTemplate()
}

@RestController
@RequestMapping(value = "/api")
class ReservationController(private val reservationService: ReservationService) {

    @GetMapping(value = "/getFreeAll")
    fun getAvaailableTickets() = reservationService.getAllTickets()

}

@Service
class ReservationService(private val restTemplate: RestTemplate) {

    fun getAllTickets() = restTemplate.getForEntity("TICKET_SERVICE/api/allTickets", Array<Ticket>::class.java).body!!.toList().toFlux()

}

data class Ticket(val id: Long? = null, val seat: String, val status: Int, val price: Double)
