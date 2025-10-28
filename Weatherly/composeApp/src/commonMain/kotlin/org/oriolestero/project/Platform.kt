package org.oriolestero.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform