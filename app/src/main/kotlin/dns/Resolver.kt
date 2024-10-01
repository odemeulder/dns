package dns

class Resolver(val cache: Cache, val udpClient: UdpInterface) {

  private val rootNs = "198.41.0.4"

  private fun getAnswer(packet: DnsPacket): DnsRecord? {
    // return the first A record in the answers section
    for (record in packet.answers) 
      when (record.type_) {
        TYPE_A -> return record
        TYPE_CNAME -> return resolveToDnsRecord(record.alias!!)
      }
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

  private fun resolveToDnsRecord(domainName: String, recordType: Int = TYPE_A): DnsRecord {
    var nameserver = rootNs
    while (true) {
      print("Querying $nameserver for $domainName\n")
      var query = buildQuery(domainName, recordType)
      println("query $query")
      var response = udpClient.sendQuery(nameserver, query)
      println("response $response")
      val dnsPacket = parsePacket(ByteArrayReader(response))
      println("dnsPacket $dnsPacket")
      getAnswer(dnsPacket)?.let {
        val returnValue = ipToString(it.data)
        cache.set(domainName, returnValue, it.ttl)
        return it
      }
        ?: getNameserverIp(dnsPacket)?.let {
          nameserver = ipToString(it)
        }
          ?: getNameserver(dnsPacket)?. let {
            nameserver = resolve(it, TYPE_A)
          }
    }

  }

  // Recursively resolve a domain name to an IP
  fun resolve(domainName: String, recordType: Int = TYPE_A): String {
    cache.get(domainName) ?. let { return it }
    val record = resolveToDnsRecord(domainName, recordType)
    return ipToString(record.data)
  }
}