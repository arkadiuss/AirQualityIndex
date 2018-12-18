package model.mapper

interface Mapper<T, D> {
    fun map(target: T) : D
}