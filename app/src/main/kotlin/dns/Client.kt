package dns

import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress

class Client(val host: String = "8.8.8.8", val port: Int = DNS_PORT) {

    // send a single query
  fun sendQuery(domainName: String, recordType: Int): DnsPacket {
    var query = buildQuery(domainName, recordType)
    println(query)
    var socket = DatagramSocket()
    println(host)
    var address = InetAddress.getByName(host)
    var packet: DatagramPacket = DatagramPacket(query, query.size, address, port);
    socket.send(packet)
    var buffer = ByteArray(1024)
    var recvPacket = DatagramPacket(buffer, 1024)
    socket.receive(recvPacket)
    println(buffer.toHex())
    val dnsPacket = parsePacket(ByteArrayReader(buffer))
    return dnsPacket
  }
}