package dns

import kotlinx.cli.*

class App {
    val name: String
        get() {
            return "Olivier's DNS resolver"
        }

}

enum class Mode {
    server,
    client,
    resolver,
}

fun main(args: Array<String>) {
    println(App().name)

    val parser = ArgParser("example")
    val mode by parser.argument(ArgType.Choice<Mode>(), description = "Mode")
    val input by parser.option(ArgType.String, shortName = "d", description = "Domain name to resolve.")
    val host by parser.option(ArgType.String, shortName = "s", description = "Host to query.")
    val port by parser.option(ArgType.String, shortName = "p", description = "Port to query.").default(DNS_PORT.toString())
    parser.parse(args)

    when (mode) {
        Mode.client -> { 
            val client = Client(host ?: "localhost", port.toInt())
            val dnsPacket = client.sendQuery(input ?: "example.com", TYPE_A)
            println(dnsPacket)
        }
        Mode.server -> {
            println("Starting dns server on port $port")
            val server = Server(Resolver(TimedCache(), UdpClient(DNS_PORT)))
            server.listen()
        }
        Mode.resolver -> {
            val resolver = Resolver(TimedCache(), UdpClient(DNS_PORT))
            val ip = resolver.resolve(input ?: "example.com")
            println("ip is $ip")
        }
    }

}





