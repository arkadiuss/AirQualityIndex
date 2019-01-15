package common

import java.util.stream.Stream

public fun <A,B,C,D> Stream<Triple<A,B,C>>.mapThird(fnc: (C) -> D): Stream<Triple<A,B,C>>{
    map { t -> Triple(t.first, t.second, fnc(t.third)) }
    return this
}