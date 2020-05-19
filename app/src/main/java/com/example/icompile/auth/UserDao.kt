package me.marthia.icompile.auth

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao  {

    @Query("SELECT * FROM t_user")
    fun getUser(): List<User>

    @Query("SELECT * FROM t_user WHERE username LIKE :user AND password LIKE :pass")
    fun login(user: String, pass: String): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user : User)

    @Update
    fun updateUser(user: User)

    @Query("DELETE FROM t_user WHERE username LIKE :username")
    fun deleteUser(username: String)
}