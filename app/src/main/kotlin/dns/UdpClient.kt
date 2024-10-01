package dns

import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress

interface UdpInterface {
  fun sendQuery(ipAddress: String, query: ByteArray): ByteArray
}

open class UdpClient(val port: Int) : UdpInterface {

  override fun sendQuery(ipAddress: String, query: ByteArray): ByteArray {
    var socket = DatagramSocket()
    var address = InetAddress.getByName(ipAddress)
    var packet = DatagramPacket(query, query.size, address, port)
    println("packet $packet, address $address, port $port")
    socket.send(packet)
    println("package sent")
    var buffer = ByteArray(1024)
    var recvPacket = DatagramPacket(buffer, 1024)
    socket.receive(recvPacket)
    println("recvPacket $recvPacket")
    return buffer
  }

}