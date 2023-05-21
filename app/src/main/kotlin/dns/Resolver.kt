package dns

class Resolver() {

  private fun getAnswer(packet: DnsPacket): ByteArray? {
    // return the first A record in the answers section
    for (record in packet.answers) 
      if (record.type_ == TYPE_A) 
        return record.data
    return null
  }

  private fun getNameserverIp(packet: DnsPacket): ByteArray? {
    // return the first A record in the additional section
    for (record in packet.additionals) 
      if (record.type_ == TYPE_A) 
        return record.data
    return null
  }

  private fun getNameserver(packet:DnsPacket): String? {
    // return the first NS record in the authority section
    for (record in packet.authorities)
      if (record.type_ == TYPE_NS)
        return decodeDnsName(ByteArrayReader(record.data))
    return null
  }

  fun resolve(domainName: String, recordType: Int = TYPE_A): String {
    val rootNs = "198.41.0.4"
    var nameserver = rootNs
    while (true) {
      print("Querying $nameserver for $domainName\n")
      var response = sendQuery(nameserver, domainName, recordType)
      getAnswer(response)?.let {
        return ipToString(it)
      }
        ?: getNameserverIp(response)?.let {
          nameserver = ipToString(it)
        }
          ?: getNameserver(response)?. let {
            nameserver = resolve(it, TYPE_A)
          }
    }
  }
}