package common

fun <A,B,C,D> List<Triple<A,B,C>>.mapThird(fnc: (C) -> D): List<Triple<A,B,D>>{
    return map { t -> Triple(t.first, t.second, fnc(t.third)) }
}

fun <A,B,C> List<Pair<A,B>>.mapSecond(fnc: (B) -> C): List<Pair<A,C>>{
    return map { t -> Pair(t.first, fnc(t.second)) }
}