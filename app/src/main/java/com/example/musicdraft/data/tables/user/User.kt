package com.example.musicdraft.data.tables.user
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val nickname: String,
    val password: String,
    val isOnline: Boolean,
    val points: Int
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
