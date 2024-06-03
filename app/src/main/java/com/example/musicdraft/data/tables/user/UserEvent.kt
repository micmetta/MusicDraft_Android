package com.example.musicdraft.data.tables.user

sealed interface UserEvent {
    object SaveUser: UserEvent
    data class SetEmailUser(val email: String): UserEvent
    data class SetNicknamedUser(val nickname: String): UserEvent
    data class SetPasswordUser(val password: String): UserEvent
    data class SetPointsUser(val points: String): UserEvent
//    object ShowDialog: UserEvent
//    object HideDialog: UserEvent
//    data class SortUsers(val sortType: SortType): UserEvent
    data class DeleteUser(val user: User): UserEvent
}