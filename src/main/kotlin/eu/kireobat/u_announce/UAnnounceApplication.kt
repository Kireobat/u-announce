package eu.kireobat.u_announce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class UAnnounceApplication

fun main(args: Array<String>) {
	runApplication<UAnnounceApplication>(*args)
}
