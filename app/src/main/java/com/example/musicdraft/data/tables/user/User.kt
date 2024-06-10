package com.example.musicdraft.data.tables.user
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var email: String,
    var nickname: String,
    var password: String,
    var isOnline: Boolean,
    var points: Int
)
//{
//    fun doesMatchSearchQUery(query: String): Boolean{
//        val matchingCombinations = listOf(
//            "${nickname}",
//            "${email}",
//            "${nickname.first()}",
//            "${email.first()}"
//        )
//
//        return matchingCombinations.any {
//            it.contains(query, ignoreCase = true) // ignoreCase mi permette di eliminare il Case Sensitive
//        }
//    }
//}
