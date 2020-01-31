package at.htl.mirrorhome.utilities

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name="DBDataLoader")
data class DBDataLoader(
    @Id @GeneratedValue val id: Long = -1,
    @Version val version: Long = -1,
    val firstDBImport: Instant = Instant.now()
)