package dns

import java.util.LinkedHashMap
import java.util.concurrent.ConcurrentHashMap

interface Cache {
  fun get(key: String): String?
  fun set(key: String, value: String, ttl: Int = 36000000)
}

class TimedCache() : Cache {
  private val hashMap = ConcurrentHashMap<String, TimedEntry>()

  override fun get(key: String): String? {
    val timedEntry = hashMap.get(key)
    if (timedEntry == null || timedEntry.isExpired()) {
      return null
    }
    return timedEntry.value
  }

  override fun set(key: String, value: String, ttl: Int) { 
    hashMap.set(key, TimedEntry(value, ttl)) 
  }

  private class TimedEntry(val value: String, val maxDurationInSeconds: Int) {
    private val createTime: Long = now()
    fun isExpired(): Boolean = (now() - createTime) > (maxDurationInSeconds * 1000)
    private fun now() = System.currentTimeMillis()
  }

}