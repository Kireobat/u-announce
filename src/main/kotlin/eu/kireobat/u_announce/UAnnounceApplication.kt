package eu.kireobat.u_announce

import com.fasterxml.jackson.annotation.JsonFormat.DEFAULT_TIMEZONE
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
@ConfigurationPropertiesScan
class UAnnounceApplication

fun main(args: Array<String>) {
	runApplication<UAnnounceApplication>(*args)
}

@PostConstruct
fun init() {
	// Set timezone to UTC
	TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIMEZONE))
}
