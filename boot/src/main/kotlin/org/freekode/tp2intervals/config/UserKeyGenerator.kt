package org.freekode.tp2intervals.config

import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.utils.UserContextHolder
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component("userKeyGenerator")
class UserKeyGenerator : KeyGenerator {
    override fun generate(target: Any, method: Method, vararg params: Any?): Any {
        val username = UserContextHolder.username
        val platformKey = if (target is PlatformInfoRepository) {
            target.platform().key
        } else {
            target.javaClass.simpleName
        }

        return if (params.isNotEmpty()) {
            "$username-$platformKey-${params.joinToString("_")}"
        } else {
            "$username-$platformKey"
        }
    }
}
