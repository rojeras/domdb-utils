import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.cli.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

suspend fun main(args: Array<String>) {
    val parser = ArgParser("domdb-utils")
    val processDomain by parser.argument(ArgType.String, description = "Domain to process | all")
    parser.parse(args)

    val client: io.ktor.client.HttpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 25_000
        }
    }

    client.get<List<ServiceDomain>>("http://api.ntjp.se/dominfo/v1/servicedomains.json")
    client.close()

    val domainMetaList = mutableListOf<DomainMeta>()
    for (domain in DomainArr) {
        val domainMeta = processDomain(domain)
        if (processDomain.equals(domain.name)) {
            val fileName = "$processDomain.domainmeta.json"
            File(fileName).writeText(Json.encodeToString(domainMeta))
            println("\n$fileName created")
            System.exit(0)
        }
        if (processDomain.equals("all")) {
            domainMetaList.add(domainMeta)
        }
    }

    if (processDomain.equals("all")) {
        val fileName = "all.domainmeta.json"
        File(fileName).writeText(Json.encodeToString(domainMetaList))
        println("\n$fileName created")
    } else {
        System.err.println("\n$processDomain is not a recognized domain name")
    }
}

fun processDomain(domain: ServiceDomain): DomainMeta {
    val versions: List<Version> =
        if (domain.versions == null)
            listOf()
        else
            domain.versions
                .filter { !it.hidden }
                .filter { !it.name.contains("RC") }
                .filter { !it.name.contains("trunk") }
                .sortedBy { it.name }
                .reversed()

    val domainVersionList = mutableListOf<DomainVersion>()

    var S_review: String = ""
    var I_review: String = ""
    var T_review: String = ""

    for (version in versions) {
        for (review in version.reviews) {
            when (review.reviewProtocol.code) {
                "aor-s" -> S_review = review.reviewOutcome.symbol
                "aor-i" -> I_review = review.reviewOutcome.symbol
                "aor-t" -> T_review = review.reviewOutcome.symbol
            }
        }

        domainVersionList.add(
            DomainVersion(
                tag = version.name,
                S_review = S_review,
                I_review = I_review,
                T_review = T_review,
            )
        )
    }

    val domainVersionListToAdd: List<DomainVersion>? =
        if (domainVersionList.size > 0) domainVersionList
        else null

    return DomainMeta(
        name = domain.name,
        description = domain.description,
        swedishLong = domain.swedishLong,
        swedishShort = domain.swedishShort,
        domainType = domain.domainType.name,
        owner = domain.owner,
        domainVersions = domainVersionListToAdd
    )
}
