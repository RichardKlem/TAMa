package tama.blockCleaning.data.entity

data class Section(
    val id: Int,
    val name: String,
    val privateRoad: Boolean,
    val streetId: Int,
    val waypoints: Array<Waypoint>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Section

        if (id != other.id) return false
        if (name != other.name) return false
        if (privateRoad != other.privateRoad) return false
        if (streetId != other.streetId) return false
        if (!waypoints.contentEquals(other.waypoints)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + privateRoad.hashCode()
        result = 31 * result + streetId
        result = 31 * result + waypoints.contentHashCode()
        return result
    }
}
