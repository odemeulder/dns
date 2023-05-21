package dns

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertEquals

internal infix fun Any?.shouldBe(other: Any?) {
  assertEquals(this, other)
    // assertThat(this).isEqualTo(other)
}

class TimedCacheTest {

    private val timedCache = TimedCache()

    @Test
    fun `before the cache expiration the cached value is still available`() {
        timedCache.set("someKey", "someValue", 1)

        Thread.sleep(ONE_SECOND / 2)
        timedCache.get("someKey") shouldBe "someValue"
    }

    @Test
    fun `after the cache expiration the cached value is no more available`() {
        timedCache.set("someKey", "someValue", 1)

        Thread.sleep(ONE_SECOND + 1)
        timedCache.get("someKey") shouldBe null
    }

    @Test
    fun `putting again a value on an expired cache entry renews its time validity`() {
        timedCache.set("someKey", "someValue", 1)

        Thread.sleep(ONE_SECOND + 1)

        timedCache.set("someKey", "anotherValue", 1)
        timedCache.get("someKey") shouldBe "anotherValue"
    }

    companion object {
        private const val ONE_SECOND: Long = 1000
    }
}