package at.htl.mirrorhome.user.calendar

import org.springframework.data.repository.CrudRepository

interface CalendarSourceRepository: CrudRepository<CalendarSource, Long> {
}