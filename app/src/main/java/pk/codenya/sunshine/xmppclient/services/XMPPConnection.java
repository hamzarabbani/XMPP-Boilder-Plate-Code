package pk.codenya.sunshine.xmppclient.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ChatMessageListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by hamza on 7/10/2016.
 */
public class XMPPConnection implements ConnectionListener, ChatMessageListener, RosterListener, ChatManagerListener, PingFailedListener {

    private final String mServiceName = "HOST NAME"; //change
    private final String mHost = "SERVER IP"; //change
    private final String TAG = "XMPPConnection";
    private final String mUsername = "USERNAME"; //change
    private final String mPassword = "PASSWORD"; //change
    private XMPPTCPConnection mConnection = null;
    private BroadcastReceiver mReceiver;

    public void connect() throws IOException, XMPPException, SmackException {
        Log.i(TAG, "connect()");
        XMPPTCPConnectionConfiguration.XMPPTCPConnectionConfigurationBuilder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setServiceName(mServiceName);
        builder.setHost(mHost);
        builder.setPort(5222);
        builder.setResource("XMPPClient");
        builder.setUsernameAndPassword(mUsername, mPassword);
        builder.setRosterLoadedAtLogin(true);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);

        try {

            // Allowing all certificates.
            //Automatically verifying
            SSLContext sslcontext=SSLContext.getInstance("TLS");
            sslcontext.init(null,new TrustManager[]{new TrustAllManager()},null);
            builder.setCustomSSLContext(sslcontext);
            builder.setHostnameVerifier(new AllowAllHostnameVerifier());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        mConnection = new XMPPTCPConnection(builder.build());

        mConnection.addConnectionListener(this);
        mConnection.connect();

        if(mConnection.isConnected()) {
            Log.i(TAG, "Connected!");
        } else {
            Log.i(TAG, "Connection Failed");
        }


        mConnection.login();

        PingManager.setDefaultPingInterval(600); //Ping every 10 minutes
        PingManager pingManager = PingManager.getInstanceFor(mConnection);
        pingManager.registerPingFailedListener(this);

//        setupSendMessageReceiver();

        ChatManager.getInstanceFor(mConnection).addChatListener(this);
        mConnection.getRoster().addRosterListener(this);

    }

    private void setupSendMessageReceiver() {
    }



    //Connection Lisnter
    @Override
    public void connected(org.jivesoftware.smack.XMPPConnection connection) {
        Log.i(TAG, "connected()");
    }

    @Override
    public void authenticated(org.jivesoftware.smack.XMPPConnection connection) {
        Log.i(TAG, "authenticated()");
    }

    @Override
    public void connectionClosed() {
        Log.i(TAG, "connectionClosed()");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.i(TAG, "connectionClosedOnError:\n"+e.getMessage());
    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }

    //Chat Message Listner
    @Override
    public void processMessage(Chat chat, Message message) {
        Log.i(TAG, "processMessage()");
        if (message.getType().equals(Message.Type.chat) || message.getType().equals(Message.Type.normal)) {
            if (message.getBody() != null) {
                Log.i(TAG, "Sender: "+message.getFrom());
                Log.i(TAG, "To: "+message.getTo());
                Log.i(TAG, "Message: "+message.getBody());
                Log.i(TAG, "processMessage() BroadCast send");
            }
        }
    }

    //Roster Listner
    @Override
    public void entriesAdded(Collection<String> addresses) {

    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {

    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {

    }

    @Override
    public void presenceChanged(Presence presence) {
        Log.i(TAG, "presenceChanged()");
    }

    //Chat Manager Listener
    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        chat.addMessageListener(this);
        Log.i(TAG, "chatCreated()");
    }

    //Ping Failed Listener
    @Override
    public void pingFailed() {
        Log.i(TAG, "pingFailed()");
    }

    public void disconnect() {
        Log.i(TAG, "disconnect()");
        try {
            if(mConnection.isConnected()){
                mConnection.disconnect();
                Log.i(TAG, "disconnected!");
            } else {
                Log.i(TAG, "Already disconnected!");
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
