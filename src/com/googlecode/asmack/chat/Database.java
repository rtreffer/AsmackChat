package com.googlecode.asmack.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Database helper to ensure that there is just one sqlite database.
 */
public class Database {

    /**
     * The internal sqlite database instance.
     */
    private static SQLiteDatabase DATABASE = null;

    /**
     * Retrieve a sqlite database instance, shared between all clients.
     * @param context The context to use for opening the database.
     * @param factory A cursor factory.
     * @return A SQLiteDatabase instance of the messages database.
     */
    public static synchronized SQLiteDatabase getDatabase(
        Context context,
        CursorFactory factory
    ) {
        if (DATABASE == null) {
            DatabaseOpenHelper helper =
                    new DatabaseOpenHelper(context, "messages", factory);
            DATABASE = helper.getWritableDatabase();
        }
        return DATABASE;
    }

}
