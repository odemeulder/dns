package dns

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

class ByteArrayTest {

  var baFixture = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05)
  var readerFixture = ByteArrayReader(baFixture)

  @Test fun testSeek() {
    readerFixture.seek(2)
    assertEquals(0x02, readerFixture.peek())
  }

  @Test fun testPeek() {
    assertEquals(0x00, readerFixture.peek())
    readerFixture.read(2)
    assertEquals(0x02, readerFixture.peek())
    assertEquals(0x02, readerFixture.peek())
  }

  @Test fun testRead() {
    val arr1 = readerFixture.read(2)
    assertEquals(2, arr1.size)
    assertEquals(baFixture[0], arr1[0])
    assertEquals(baFixture[1], arr1[1])
    val arr2 = readerFixture.read(3)
    assertEquals(baFixture[2], arr2[0])
    assertEquals(baFixture[3], arr2[1])
    assertEquals(baFixture[4], arr2[2])
    assertEquals(0x05, readerFixture.peek())
  }

  @Test fun testReadOne() {
    val b1 = readerFixture.readOne()
    assertEquals(baFixture[0], b1)
    val b2 = readerFixture.readOne()
    assertEquals(baFixture[1], b2)
    assertEquals(0x02, readerFixture.peek())
  }

  @Test fun testTell() {
    readerFixture.read(2)
    assertEquals(2, readerFixture.tell())
  }
  
}