package Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//database for this application
@Database(entities = {Book.class, User.class}, version =  1, exportSchema = false)
public abstract class TallTaleDatabase extends RoomDatabase {

    //database name
    private static final String DATABASE_NAME = "tall_tale_database";

    //database instance
    private static TallTaleDatabase tallTaleDatabase;

    //singleton
    public static TallTaleDatabase getInstance(Context context) {
        if (tallTaleDatabase == null) {
            tallTaleDatabase = Room.databaseBuilder(context, TallTaleDatabase.class,
                    DATABASE_NAME).allowMainThreadQueries().build();
        }
        return tallTaleDatabase;
    }

    //member DAOs
   public abstract BookDao bookDao();
    public abstract UserDao userDao();
}
