package dns

data class DnsHeader(
    var id: Int, 
    var flags: Int, 
    var numQuestions: Int = 0,
    var numAnswers: Int = 0,
    var numAuthorities: Int = 0,
    var numAdditionals: Int = 0
)

data class DnsQuestion(
    var name: ByteArray,
    var type_: Int,
    var class_: Int
)

data class DnsRecord(
    var name: ByteArray,
    var type_: Int,
    var class_: Int,
    var ttl: Int,
    var data: ByteArray,
)

data class DnsPacket(
  var header: DnsHeader,
  var questions: ArrayList<DnsQuestion> = arrayListOf(),
  var answers: ArrayList<DnsRecord> = arrayListOf(),
  var authorities: ArrayList<DnsRecord> = arrayListOf(),
  var additionals: ArrayList<DnsRecord> = arrayListOf(),
) 
