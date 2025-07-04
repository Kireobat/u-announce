package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.persistence.entity.PlatformEntity
import eu.kireobat.u_announce.persistence.repo.PlatformRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PlatformService(private val platformRepo: PlatformRepo) {

    fun getPlatform(platformId: Long): PlatformEntity {
        return platformRepo.findById(platformId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find platform with id ($platformId)") }
    }
}
