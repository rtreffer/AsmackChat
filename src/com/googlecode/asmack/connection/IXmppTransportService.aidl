package com.googlecode.asmack.connection;

import com.googlecode.asmack.Stanza;

interface IXmppTransportService {

    boolean tryLogin(String jid, String password);
    boolean send(in Stanza stanza);
    String getFullJidByBare(String bare);

}
