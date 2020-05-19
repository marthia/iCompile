package me.marthia.icompile.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_user")
class User(
    @PrimaryKey
    var username: String,
    var password: String
) {
}