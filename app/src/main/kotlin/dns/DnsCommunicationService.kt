package dns

import kotlin.random.Random

val TYPE_A = 1
val TYPE_NS = 2
val TYPE_CNAME = 5
val TYPE_SOA = 6
val TYPE_PTR = 12
val TYPE_MX = 15
val TYPE_TXT = 16
val TYPE_AAAA = 28

val CLASS_IN = 1
val DNS_PORT = 53

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

fun headerToBytes(header: DnsHeader): ByteArray {
    var buffer: ByteArray = ByteArray(2 * 6)
    writeTwoBytesToBuffer(buffer, 0, header.id)
    writeTwoBytesToBuffer(buffer, 2, header.flags)
    writeTwoBytesToBuffer(buffer, 4, header.numQuestions)
    writeTwoBytesToBuffer(buffer, 6, header.numAnswers)
    writeTwoBytesToBuffer(buffer, 8, header.numAuthorities)
    writeTwoBytesToBuffer(buffer, 10, header.numAdditionals)
    return buffer;
}

fun parseHeader(buffer: ByteArray): DnsHeader {
    var header = DnsHeader(0,0)
    header.id = readTwoBytesToInt(buffer, 0)
    header.flags = readTwoBytesToInt(buffer, 2)
    header.numQuestions = readTwoBytesToInt(buffer, 4)
    header.numAnswers = readTwoBytesToInt(buffer, 6)
    header.numAuthorities = readTwoBytesToInt(buffer, 8)
    header.numAdditionals = readTwoBytesToInt(buffer, 10)
    return header
}

fun parseHeader(reader: ByteArrayReader): DnsHeader {
    var header = DnsHeader(0,0)
    header.id = readTwoBytesToInt(reader)
    header.flags = readTwoBytesToInt(reader)
    header.numQuestions = readTwoBytesToInt(reader)
    header.numAnswers = readTwoBytesToInt(reader)
    header.numAuthorities = readTwoBytesToInt(reader)
    header.numAdditionals = readTwoBytesToInt(reader)
    return header
}

fun writeTwoBytesToBuffer(buffer: ByteArray, offset: Int, data: Int) {
    buffer[offset] = (data shr 8).toByte()
    buffer[offset+1] = (data shr 0).toByte()
}

fun readTwoBytesToInt(buffer: ByteArray, offset: Int): Int {
    // some bizarre logic here, to read big endian int
    // first byte, shift left 8
    // logical and with 11111111 to handle possible negative sign
    // then second byte, also anded with 11111111
    return (((buffer[offset].toInt() and 0xff) shl 8)) or ((buffer[offset+1].toInt() and 0xff))
}

fun readTwoBytesToInt(reader: ByteArrayReader): Int {
    // some bizarre logic here, to read big endian int
    // first byte, shift left 8
    // logical and with 11111111 to handle possible negative sign
    // then second byte, also anded with 11111111
    return (((reader.readOne().toInt() and 0xff) shl 8)) or ((reader.readOne().toInt() and 0xff))
}

fun writeFourBytesToBuffer(buffer: ByteArray, offset: Int, data: Int) {
    buffer[offset] = (data shr 24).toByte()
    buffer[offset+1] = (data shr 16).toByte()
    buffer[offset+2] = (data shr 8).toByte()
    buffer[offset+3] = (data shr 0).toByte()
}

fun readFourBytesToInt(buffer: ByteArray, offset: Int): Int {
    // some bizarre logic here, to read big endian int
    // first byte, shift left 8
    // logical and with 11111111 to handle possible negative sign
    // then second byte, also anded with 11111111
    // and so on with third and fourth byte
    return (((buffer[offset].toInt() and 0xff) shl 24)) or 
        (((buffer[offset+1].toInt() and 0xff) shl 16)) or
        (((buffer[offset+2].toInt() and 0xff) shl 8)) or
        ((buffer[offset+3].toInt() and 0xff))
}

fun readFourBytesToInt(reader: ByteArrayReader): Int {
    // some bizarre logic here, to read big endian int
    // first byte, shift left 8
    // logical and with 11111111 to handle possible negative sign
    // then second byte, also anded with 11111111
    // and so on with third and fourth byte
    return (((reader.readOne().toInt() and 0xff) shl 24)) or 
        (((reader.readOne().toInt() and 0xff) shl 16)) or
        (((reader.readOne().toInt() and 0xff) shl 8)) or
        ((reader.readOne().toInt() and 0xff))
}

fun questionToBytes(question: DnsQuestion): ByteArray {
    var buffer = ByteArray(question.name.size + 4)
    val nameSize = question.name.size
    question.name.copyInto(buffer, 0, 0, nameSize)
    writeTwoBytesToBuffer(buffer, nameSize, question.type_)
    writeTwoBytesToBuffer(buffer, nameSize + 2, question.class_)
    return buffer
}

fun parseQuestion(buffer: ByteArray): DnsQuestion {
    val (name, ptr) = decodeDnsName(buffer, 0)
    val type_ = readTwoBytesToInt(buffer, ptr+1)
    val class_ = readTwoBytesToInt(buffer, ptr+3)
    return DnsQuestion(encodeDnsName(name), type_, class_)
}

fun parseQuestion(reader: ByteArrayReader): DnsQuestion {
    val name = decodeDnsName(reader)
    val type_ = readTwoBytesToInt(reader)
    val class_ = readTwoBytesToInt(reader)
    return DnsQuestion(encodeDnsName(name), type_, class_)
}


fun encodeDnsName(domainName: String): ByteArray {
    var parts: List<String> = domainName.split(".")
    var rv: ByteArray = byteArrayOf();
    for (p in parts) {
        rv += p.length.toByte()
        rv += p.toByteArray()
    }
    rv += 0x0
    return rv;
}


public fun buildQuery(domainName: String, recordType: Int): ByteArray {
    var rv: ByteArray = byteArrayOf()
    // The encoding for the flags is defined in section 4.1.1 of RFC 1035. 
    val flags: Int = 0
    val id_ = Random.nextInt(0, 65536)
    val header = DnsHeader(id_, flags, numQuestions = 1)
    val question = DnsQuestion(encodeDnsName(domainName), recordType, CLASS_IN)
    rv += headerToBytes(header)
    rv += questionToBytes(question)
    return rv
}

fun parseRecord(buffer: ByteArray): DnsRecord {
    val(name, ptr) = decodeDnsName(buffer, 0)
    val type_ = readTwoBytesToInt(buffer, ptr+1)
    val class_ = readTwoBytesToInt(buffer, ptr+3)
    val ttl = readFourBytesToInt(buffer, ptr+5)
    val dataLen = readTwoBytesToInt(buffer, ptr+9)
    val data: ByteArray = byteArrayOf()
    buffer.copyInto(data, 0, ptr+1, ptr+1+dataLen)
    return DnsRecord(encodeDnsName(name), type_, class_, ttl, data, "") // todo
}
fun parseRecord(reader: ByteArrayReader): DnsRecord {
    val name = decodeDnsName(reader)
    val type_ = readTwoBytesToInt(reader)
    val class_ = readTwoBytesToInt(reader)
    val ttl = readFourBytesToInt(reader)
    val dataLen = readTwoBytesToInt(reader)
    val location = reader.tell()
    val data: ByteArray = reader.read(dataLen)
    var alias: String = "" 
    if ( type_  == TYPE_CNAME ) {
        reader.seek(location)
        alias =  decodeDnsName(reader)
    }
    return DnsRecord(encodeDnsName(name), type_, class_, ttl, data, alias)
}

fun recordToBytes(record: DnsRecord): ByteArray {
    var namesize = record.name.size
    var buffer = ByteArray(namesize + 14)
    var offset = namesize
    record.name.copyInto(buffer, 0, 0, offset)
    writeTwoBytesToBuffer(buffer, offset, record.type_)
    offset += 2
    writeTwoBytesToBuffer(buffer, offset, record.class_)
    offset += 2
    writeFourBytesToBuffer(buffer, offset, record.ttl)
    offset += 4
    writeTwoBytesToBuffer(buffer, offset, 4) // len is 4
    offset += 2
    record.data.copyInto(buffer, offset, 0, 4) // read bytes 0, 1, 2, and 3 into buffer, starting at offset namesize + 8
    return buffer
}

// returns the DnsName and a pointer to the index of the ByteArray where the reading stopped.
fun decodeDnsName(buffer: ByteArray, offset: Int): Pair<String, Int> {
    val rv: ArrayList<String> = arrayListOf()
    var ptr = offset
    var length = buffer[ptr].toInt() and 0xff // the and here is to flip the sign
    while (length != 0) {
        // c0 is 0011 1111
        if (length and 0xc0 != 0x00) {
            val (name, _) = decodeCompressedName(buffer, ptr, length)
            rv.add(name)
            break
        } else {
            val part = String(buffer.sliceArray(ptr+1..ptr+length), Charsets.ISO_8859_1)
            rv.add(part)
            ptr = ptr+1+length
            length = buffer[ptr].toInt()    
        }
    }
    return Pair(rv.joinToString(".") { it }, ptr)
} 

fun decodeCompressedName(buffer: ByteArray, offset: Int, length: Int): Pair<String, Int> {
    // 0x3f is 0011 1111, using 'and' to take the bottom 6th bits and combine with the next byte
    val pointerBytes = byteArrayOf((length and 0x3f).toByte() , buffer[offset + 1])
    val pointer = readTwoBytesToInt(pointerBytes, 0)
    val (name, _) = decodeDnsName(buffer, pointer)
    return Pair(name, offset+1)
}

// returns the DnsName 
fun decodeDnsName(reader: ByteArrayReader): String {
    val rv: ArrayList<String> = arrayListOf()
    var b = reader.readOne()
    var length = b.toInt() and 0xff
    while (length != 0) {
        // c0 is 0011 1111
        if (length and 0xc0 != 0x00) {
            val name = decodeCompressedName(reader, length)
            rv.add(name)
            break
        } else {
            val part = String(reader.read(length), Charsets.ISO_8859_1)
            rv.add(part)
            length = reader.readOne().toInt()
        }
    }
    return rv.joinToString(".") { it }
} 

fun decodeCompressedName(reader: ByteArrayReader, length: Int): String {
    // 0x3f is 0011 1111, using 'and' to take the bottom 6th bits and combine with the next byte
    val pointerBytes = byteArrayOf((length and 0x3f).toByte() , reader.readOne())
    val currPosition = reader.tell()
    val pointerReader = ByteArrayReader(pointerBytes)
    val pointer = readTwoBytesToInt(pointerReader)
    reader.seek(pointer)
    val name = decodeDnsName(reader)
    reader.seek(currPosition)
    return name
}

fun parsePacket(reader: ByteArrayReader): DnsPacket {
  val packet = DnsPacket(DnsHeader(1, 1))
    val header = parseHeader(reader)
    packet.header = header
    for (i in 0 until header.numQuestions) packet.questions.add(parseQuestion(reader))
    for (i in 0 until header.numAnswers) packet.answers.add(parseRecord(reader))
    for (i in 0 until header.numAuthorities) packet.authorities.add(parseRecord(reader))
    for (i in 0 until header.numAdditionals) packet.additionals.add(parseRecord(reader))
    return packet
}

fun ipToString(ip: ByteArray): String {
  return ip.map({ it.toInt() and 0xff}).joinToString(".")
}

fun stringToIp(s: String): ByteArray = s.split('.').map({ it.toInt().toByte() }).toByteArray()
