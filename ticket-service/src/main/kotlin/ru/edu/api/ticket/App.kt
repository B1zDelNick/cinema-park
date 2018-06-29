package ru.edu.api.ticket

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity.ok
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class AppConfig {

    @Bean
    fun api() = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()

}

/*@Controller
class ViewController {

    @GetMapping("/api")
    fun swagger() = "swagger-ui.html"

}*/

@Api(value = "Ticket API", description = "Ticket DATA service public methods", tags = ["tickets"])
@RestController
@RequestMapping(value = "/api")
class TicketController(private val ticketService: ITicketService) {

    @ApiOperation(value = "getAllTickets", notes = "Get All available tickets", nickname = "GetAll")
    @GetMapping("/allTickets")
    fun getAllTickets() = ok().body(ticketService.getAllTickets())
}

interface ITicketService {
    fun getAllTickets(): List<Ticket>
}

@Service
class TicketService: ITicketService {

    private val tickets = mutableListOf<Ticket>()

    init {
        for (i in 1..10) {
            tickets.add(Ticket(id = i.toLong(), seat = "A$i", status = 0, price = 240.00))
        }
    }

    override fun getAllTickets() = tickets
}

data class Session(val id: Long? = null, val movie: Movie, val hall: Int, val beginTime: LocalDateTime, val tickets: MutableList<Ticket>)
data class Movie(val id: Long? = null, val ruName: String, val enName: String, val startDate: LocalDate, val endDate: LocalDate, val length: Long)
data class Ticket(val id: Long? = null, val seat: String, val status: Int, val price: Double)
