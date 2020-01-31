package at.htl.mirrorhome.utilities

import org.springframework.data.repository.CrudRepository

interface DBDataLoaderRepository: CrudRepository<DBDataLoader, Long> {
}