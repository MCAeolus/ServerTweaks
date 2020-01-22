package me.aeolus.servertweaks.modules

interface Module {

    fun onload() {

    }

    fun create()

    fun close()

}