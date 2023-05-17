package dns

class ByteArrayReader (val buffer: ByteArray) {

  private var offset: Int = 0

  fun seek(ptr: Int) { 
    offset = ptr 
  }

  fun tell(): Int = offset

  fun peek(): Byte = buffer[offset]
  
  fun read(num: Int): ByteArray {
    val rv = buffer.sliceArray(offset..offset+num-1)
    offset = offset+num
    return rv
  }

  fun readOne(): Byte {
    val rv = buffer[offset]
    offset += 1
    return rv
  }

}