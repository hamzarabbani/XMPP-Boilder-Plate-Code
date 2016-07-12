package pk.codenya.sunshine.xmppclient;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

import pk.codenya.sunshine.xmppclient.services.XMPPConnection;

public class MainActivity extends AppCompatActivity {

    private XMPPConnection client = null;
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect();
    }

    @Override
    protected void onStop(){
        disconnect();
        super.onStop();
        getDelegate().onStop();
    }



    private void connect(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                client = new XMPPConnection();
                try {
                    Looper.prepare();
                    mHandler = new Handler();
                    client.connect();
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void disconnect(View view){
        disconnect();
    }
    private void disconnect(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                client.disconnect();
            }
        });
    }
}
