package dns

import java.nio.charset.StandardCharsets
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress

class App {
    val greeting: String
        get() {
            return "Olivier's DNS resolver"
        }
}

fun main() {
    println(App().greeting)
    testQuery()
}


fun testQuery() {
    var query = buildQuery("example.com", TYPE_A)
    // println(query.toHex());
    var socket = DatagramSocket()
    var address = InetAddress.getByName("8.8.8.8")
    var packet: DatagramPacket = DatagramPacket(query, query.size, address, DNS_PORT);
    socket.send(packet)
    var buffer = ByteArray(1024)
    var recvPacket = DatagramPacket(buffer, 1024)
    socket.receive(recvPacket)
    // println(buffer.toHex())
    val dnsPacket = parsePacket(ByteArrayReader(buffer))
    // println(dnsPacket)
    println(ipToString(dnsPacket.answers[0].data))
}


