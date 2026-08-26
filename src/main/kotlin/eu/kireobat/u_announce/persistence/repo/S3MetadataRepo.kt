package eu.kireobat.u_announce.persistence.repo

import eu.kireobat.u_announce.persistence.entity.S3MetadataEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface S3MetadataRepo: JpaRepository<S3MetadataEntity, Long> {
    fun findByMediaKey(mediaKey: String): Optional<S3MetadataEntity>
}