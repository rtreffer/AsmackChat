package com.googlecode.asmack.chat;

import java.net.URLEncoder;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

/**
 * The Chat notification listener updates the database, generates notifications
 * and enforces reloads of the UI.
 */
public class ChatNotificationListener implements PacketListener {

    /**
     * The internal database instance.
     */
    private final SQLiteDatabase database;

    /**
     * The system wide notification manager.
     */
    private final NotificationManager notificationManager;

    /**
     * The context of this listener.
     */
    private final Context context;

    /**
     * 
     * @param context
     */
    public ChatNotificationListener(Context context) {
        this.context = context;
        database = Database.getDatabase(context, null);
        notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Handle a single smack packet, discarding anything but Message.
     * @param packet The smack packet.
     */
    @Override
    public void processPacket(Packet packet) {
        if (!(packet instanceof Message)) {
            return;
        }
        Message msg = (Message)packet;
        String text = msg.getBody();
        if (text == null || text.trim().length() == 0) {
            return;
        }
        Log.d("CNL", "READ: " + msg);
        ContentValues values = new ContentValues();

        String bareFrom = XMPPUtils.getBareJid(msg.getFrom());
        String bareTo = XMPPUtils.getBareJid(msg.getTo());

        values.put("ts", System.currentTimeMillis());
        values.put("jid", bareFrom);
        values.put("src", msg.getFrom());
        values.put("dst", msg.getTo());
        values.put("via", bareTo);
        values.put("msg", text.trim());
        database.insert("msg", "_id", values);

        Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority("jabber-chat-db");
        builder.appendPath(bareTo);
        builder.appendPath(bareFrom);
        context.getContentResolver().notifyChange(builder.build(), null);

        setNotification(bareFrom, bareTo);
    }

    /**
     * Create a new notification for a given local/remote user pair.
     * @param from The local account jid.
     * @param to The remote account jid.
     */
    private final void setNotification(String from, String to) {
        String tag = from + "/" + to;
        Intent intent = new Intent("android.intent.action.SENDTO");
        Uri uri = Uri.parse("imto://jabber/" + URLEncoder.encode(tag));
        intent.setData(uri);
        Notification notify = new Notification(
            R.drawable.icon,
            "New chat",
            System.currentTimeMillis()
        );
        notify.setLatestEventInfo(
            context,
            "New chat message",
            "from " + from,
            PendingIntent.getActivity(context, 0, intent, 0)
        );
        notificationManager.notify(tag, 1, notify);
    }

}
