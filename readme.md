# dns resolver implemented in Kotlin

Based on this fantastic blog post. https://implement-dns.wizardzines.com/

Implement DNS in a weekend. I pretty much followed the same approach as Julia Evans described, but implementedin Kotlin, rather than Python.

## operating instructions

Run the server
```bash
./gradlew run --args="server -p 5354"
```

Query the server for one DNS query
```bash
./gradlew run --args="client -s localhost -p 5354 -d www.facebook.com"
./gradlew run --args="client -s 8.8.8.8 -p 53 -d www.example.com"
```

Query the server to resolve a domain name
```bash
./gradlew run --args="resolver -d www.facebook.com"
```

## to do
* CNAME <in progress>
* pass arguments <in progress>
* test resolver <in progress>
* socket server <in progress>