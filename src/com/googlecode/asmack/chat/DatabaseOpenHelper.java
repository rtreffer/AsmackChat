package com.googlecode.asmack.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper to tearup and upgrade message databases. Versions are split
 * into minor and major version parts. Major version upgrades will be handled
 * with a drop, whereas minor version upgrades will preserver the content.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    /**
     * Current major database version.
     */
    private final static short MAJOR = 3;

    /**
     * Current minor database version.
     */
    private final static short MINOR = 0;

    /**
     * Combined version ((MAJOR &lt;&lt; 16) + MINOR).
     */
    private final static int VERSION = MINOR + (MAJOR << 16);

    /**
     * Open the database, performing a data upgrade if needed.
     * @param context The cntext used to open the database.
     * @param name The database name.
     * @param factory A CursorFactory.
     */
    public DatabaseOpenHelper(Context context, String name, CursorFactory factory) {
        super(context, name, factory, VERSION);
    }

    /**
     * Called when a new database is created.
     * @param db The database to initialize.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE msg(" +
                "_id INTEGER," +
                "src TEXT," +
                "dst TEXT," +
                "jid TEXT," +
                "via TEXT," +
                "msg TEXT," +
                "ts INTEGER," +
                "PRIMARY KEY(_id)" +
            ")"
        );
        db.execSQL("CREATE INDEX lookup ON msg(via, jid, _id ASC)");
    }

    /**
     * Called when there is a database update.
     * @param db The database in need of an update.
     * @param oldVersion The old on disk database version.
     * @param newVersion The new and current target verstion.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int oldMajor = oldVersion >> 16;
        int newMajor = newVersion >> 16;

        if (oldMajor != newMajor) {
            db.execSQL("DROP TABLE msg");
            onCreate(db);
            return;
        }
    }

}
