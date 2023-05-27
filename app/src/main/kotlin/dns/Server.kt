package dns

import java.net.DatagramSocket
import java.net.DatagramPacket

class Server(val resolver: Resolver) {

  fun listen() {
    val socket = DatagramSocket(5354)
    var running = true;
    var buffer = ByteArray(1024)

    while (running) {
        var packet = DatagramPacket(buffer, buffer.size);
        socket.receive(packet);
        var address = packet.getAddress();
        var port = packet.getPort();

        var dnsPacket = parsePacket(ByteArrayReader(packet.getData()))
        var domainName = decodeDnsName(ByteArrayReader((dnsPacket.questions[0].name)))
        val ip = resolver.resolve(domainName)

        var responseHeader = DnsHeader(dnsPacket.header.id, 0, 0, 1, 0, 0) // one answer
        var responseRecord = DnsRecord(dnsPacket.questions[0].name, TYPE_A, CLASS_IN, 60, stringToIp(ip))
        var rv: ByteArray = byteArrayOf()
        rv += headerToBytes(responseHeader)
        rv += recordToBytes(responseRecord)        
        var response = DatagramPacket(rv, rv.size, address, port)
        socket.send(response);
    }
    socket.close();
}


}