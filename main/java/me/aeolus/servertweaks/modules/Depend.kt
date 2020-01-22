package me.aeolus.servertweaks.modules

import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
annotation class Depend(vararg val dependIDs : String)