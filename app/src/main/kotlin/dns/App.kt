package dns

import java.nio.charset.StandardCharsets
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress

class App {
    val name: String
        get() {
            return "Olivier's DNS resolver"
        }
}

fun main() {
    println(App().name)
    // sendQuery("216.239.32.10", "google.com", TYPE_A)
    val resolver = Resolver()
    val ip = resolver.resolve("invitationconsultants.com")
    println("ip is $ip")
}


fun sendQuery(ipAddress: String, domainName: String, recordType: Int): DnsPacket {
    var query = buildQuery(domainName, recordType)
    // println(query.toHex());
    var socket = DatagramSocket()
    var address = InetAddress.getByName(ipAddress)
    var packet: DatagramPacket = DatagramPacket(query, query.size, address, DNS_PORT);
    socket.send(packet)
    var buffer = ByteArray(1024)
    var recvPacket = DatagramPacket(buffer, 1024)
    socket.receive(recvPacket)
    // println(buffer.toHex())
    val dnsPacket = parsePacket(ByteArrayReader(buffer))
    // println(dnsPacket)
    //println(ipToString(dnsPacket.answers[0].data))
    return dnsPacket
}


