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
) {
    override fun toString(): String = "DnsQuestion(name=${String(name)}, type_=$type_, class_=$class_)"
}

data class DnsRecord(
    var name: ByteArray,
    var type_: Int,
    var class_: Int,
    var ttl: Int,
    var data: ByteArray,
    var alias: String? = null,
) {
    override fun toString(): String {
        var dataString: String
        if (data.size == 4) dataString = ipToString(data) 
        else dataString = String(data) 
        return "DnsRecord(name=[${String(name)}], type_=$type_, class_=$class_, ttl=$ttl, data=[$dataString], alias=$alias)"
    }
}

data class DnsPacket(
  var header: DnsHeader,
  var questions: ArrayList<DnsQuestion> = arrayListOf(),
  var answers: ArrayList<DnsRecord> = arrayListOf(),
  var authorities: ArrayList<DnsRecord> = arrayListOf(),
  var additionals: ArrayList<DnsRecord> = arrayListOf(),
) {
    override fun toString(): String = "$header\nquestions=$questions\nanswers=$answers\nauthorities=$authorities\nadditionals=$additionals"
}

