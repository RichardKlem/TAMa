package tama.blockCleaning.data.entity

import java.util.*

data class Cleaning(
    val id: Int,
    val name: String,
    val from: Date,
    val to: Date,
    val sId: Int, // street id
    val section: Section,
)
