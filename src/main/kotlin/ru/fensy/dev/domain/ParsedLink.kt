package ru.fensy.dev.domain

data class ParsedLink(
    val id: Long,
    val postId: Long,
    val type: ParsedLinkType,
    val link: String,
    val picture: ByteArray? = null,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParsedLink

        if (id != other.id) return false
        if (postId != other.postId) return false
        if (price != other.price) return false
        if (type != other.type) return false
        if (link != other.link) return false
        if (!picture.contentEquals(other.picture)) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (currency != other.currency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + postId.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + picture.contentHashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + currency.hashCode()
        return result
    }

}
