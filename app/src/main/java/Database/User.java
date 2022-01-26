package Database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User {

    //public values for currently logged in user
    //current user id
    @Ignore
    public static int userId;
    //current user goal
    @Ignore
    public static int userGoal;
    //current user username
    @Ignore
    public static String loggedInUser = "Someone";

    //constructor
    public User(String username, String password, int goal) {
        this.username = username;
        this.password = password;
        this.goal = goal;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_name")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    //value of user's height goal
    @ColumnInfo(name = "goal")
    private int goal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}
