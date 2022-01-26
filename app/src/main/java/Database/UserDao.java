package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {

    //get user goal
    @Query("SELECT goal FROM user WHERE id = :id")
    int getUserGoal(int id);

    //get user from database - the application only allows for the creation of one user account.
    @Query("SELECT * FROM user")
    User getUser();

    //check for username/password combination
    @Query("SELECT * FROM user WHERE user_name = :username AND password = :password")
    User validateLogin(String username, String password);

    //insert user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    //update user goal
    @Query("UPDATE user SET goal = :goal")
    void updateGoal(int goal);

    //delete all records in user table
    @Query("DELETE FROM user")
    void deleteAllRecords();
}
