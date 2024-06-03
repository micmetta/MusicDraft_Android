package com.example.musicdraft.data.tables.user

data class UserState(
    val users: List<User> = emptyList(),
    val email: String = "",
    val nickname: String = "",
    val password: String = "",
    val points: Int = 0,
//    val sortType: SortType = SortType.EMAIL
)
