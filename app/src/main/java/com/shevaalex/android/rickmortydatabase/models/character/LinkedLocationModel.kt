package com.shevaalex.android.rickmortydatabase.models.character


class LinkedLocationModel(
        val name: String,
        val url: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinkedLocationModel) return false

        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }

    override fun toString(): String {
        return "LinkedLocationModel(name='$name', url='$url')"
    }
}