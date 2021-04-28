import kotlinx.serialization.Serializable

@Serializable
data class DomainMeta(
    val name: String,
    val description: String,
    val swedishLong: String,
    val swedishShort: String,
    val owner: String? = null,
    val domainType: String,
    val domainVersions: List<DomainVersion>?
)

@Serializable
data class DomainVersion(
    val tag: String,
    val S_review: String? = null,
    val I_review: String? = null,
    val T_review: String? = null
)
