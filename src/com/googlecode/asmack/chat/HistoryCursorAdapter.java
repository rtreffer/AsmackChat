package com.googlecode.asmack.chat;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * HistoryCursorAdapter is a chat history cursor adapter, used as a data model
 * for the chat history list view.
 */
public class HistoryCursorAdapter extends CursorAdapter {

    /**
     * The logging tag, HistoryCursorAdapter.
     */
    private static final String TAG = HistoryCursorAdapter.class.getSimpleName();

    /**
     * The local user jid. 
     */
    private final String from;

    /**
     * Create a new chat history adapter, bound to a certain context/query pair.
     * @param context The context of this adapter.
     * @param from The local jid, to identify local and remote messages.
     * @param c The current cursor instance.
     * @param autoRequery True to requery the database on demand.
     */
    public HistoryCursorAdapter(
        Context context,
        String from,
        Cursor c,
        boolean autoRequery
    ) {
        super(context, c, autoRequery);
        this.from = from;
    }

    /**
     * Create a new list view.
     * @param context The view context.
     * @param cursor The database cursor.
     * @param parent The parent view group.
     * @return A new inflated history row.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((Activity)context).getLayoutInflater()
                .inflate(R.layout.history_row, null);
    }

    /**
     * Write the values of the current database cursor to the current view.
     * @param view The view to be altered.
     * @param context The view contxt.
     * @param cursor The database cursor, scrolled to the current row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView author = (TextView) view.findViewById(R.id.Author);
        String src = cursor.getString(cursor.getColumnIndex("src"));
        if (src.startsWith(from)) {
            view.setBackgroundColor(Color.rgb(192, 192, 192));
        } else {
            view.setBackgroundColor(Color.rgb(255, 255, 255));
        }
        author.setText(XMPPUtils.getUser(src));
        String message = cursor.getString(cursor.getColumnIndex("msg"));
        TextView msg = (TextView) view.findViewById(R.id.Msg);
        msg.setText(message);
        long timestamp = cursor.getLong(cursor.getColumnIndex("ts"));
        Date d = new Date(timestamp);
        TextView time = (TextView) view.findViewById(R.id.Time);
        time.setText(d.toLocaleString());
    }

    /**
     * Create a new database query suitable for this view.
     * @param context The query/database context.
     * @param accountJid The local account jid.
     * @param jid The remote jid.
     * @return A new database cursor.
     */
    public static Cursor query(Context context, String accountJid, String jid) {
        SQLiteDatabase database = Database.getDatabase(context, null);
        Log.d(TAG, "via=" + accountJid);
        Log.d(TAG, "jid=" + jid);
        Cursor cursor = database.query(
            "msg",
            new String[]{"src", "dst", "ts", "msg", "_id"},
            "via=? AND jid=?",
            new String[]{accountJid, jid},
            null,
            null,
            "_id ASC"
        );
        Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority("jabber-chat-db");
        builder.appendPath(accountJid);
        builder.appendPath(jid);
        cursor.setNotificationUri(context.getContentResolver(), builder.build());
        return cursor;
    }

}
