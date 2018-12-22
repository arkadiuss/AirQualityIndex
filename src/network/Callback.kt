package network

abstract class Callback<T> {
    abstract fun onReceive(res: T)
}