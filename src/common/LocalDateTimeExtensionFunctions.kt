package common

import java.time.LocalDateTime

fun LocalDateTime.isBetween(startDate: LocalDateTime,endDate: LocalDateTime): Boolean{
    return (isAfter(startDate) && isBefore(endDate)) || isEqual(startDate) || isEqual(endDate)
}