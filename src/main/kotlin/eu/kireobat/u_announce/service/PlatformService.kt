package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.persistence.entity.PlatformEntity
import eu.kireobat.u_announce.persistence.repo.PlatformRepo
import jakarta.ws.rs.NotFoundException
import org.springframework.stereotype.Service

@Service
class PlatformService(private val platformRepo: PlatformRepo) {

    fun getPlatform(platformId: Long): PlatformEntity {
        return platformRepo.findById(platformId).orElseThrow { throw NotFoundException("Could not find platform with id ($platformId)") }
    }
}
