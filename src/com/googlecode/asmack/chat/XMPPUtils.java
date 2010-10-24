package com.googlecode.asmack.chat;

/**
 * XMPP utilities.
 */
public class XMPPUtils {

    /**
     * Get the bare jid from a full jid by cuttting any resource parts.
     * @param resourceJid The resource jid.
     * @return A bare jid.
     */
    public final static String getBareJid(String resourceJid) {
        int index = resourceJid.indexOf('/');
        if (index == -1) {
            return resourceJid;
        }
        return resourceJid.substring(0, index);
    }

    /**
     * Retrieve the user part of a jid.
     * @param jid The bare or full jid.
     * @return The user part of the jid.
     */
    public final static String getUser(String jid) {
        int index = jid.indexOf('@');
        if (index == -1) {
            index = jid.indexOf('.');
            if (index == -1) {
                return jid;
            }
        }
        return jid.substring(0, index);
    }
}
