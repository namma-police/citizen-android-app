package nammapolice.ak.com.theateam.nammapolice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service implements Emitter.Listener {

    public static final String BROADCAST_ACTION = "com.nammapolice.socket";

    private  String EVENT = "";

    private SocketBinder binder = new SocketBinder();

    private Socket mSocket;
    private Intent intent;

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class SocketBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
//        HashMap<String, String> current = NammaPolice.getUser(getApplicationContext());
//        EVENT=current.get("USER_ID")+"-waiting-for-help";
//        intent = new Intent(BROADCAST_ACTION);
//        try {
//            mSocket = IO.socket(NammaPolice.SERVER_URL + "/");
//            mSocket.connect();
//        } catch (Exception ex) {
//        }
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HashMap<String, String> current = NammaPolice.getUser(getApplicationContext());
        EVENT = current.get("USER_ID") + "-waiting-for-help";
        this.intent = new Intent(BROADCAST_ACTION);
        try {
            mSocket = IO.socket(NammaPolice.SERVER_URL + "/");
            System.out.println("Socket io running");
            mSocket.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        connectToPolice("");
        System.out.println(EVENT);
        return super.onStartCommand(intent, flags, startId);
    }
    public void connectToPolice(String id) {
        System.out.println(EVENT);
        try {
            if (mSocket.hasListeners(EVENT)) {
                disConnect();
            }

            mSocket.on(EVENT, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            disConnect();
            mSocket.disconnect();
        } catch (Exception ex) {
        }
        super.onDestroy();
    }

    @Override
    public void call(Object... args) {
        intent.putExtra("RESULT", args[0].toString());
        System.out.println("Socket broadcast recieved");
        sendBroadcast(intent);
    }

//    public void connect(String id) {
//        try {
//            if (mSocket.hasListeners(EVENT)) {
//                disConnect();
//            }
//            mSocket.emit("", id);
//            mSocket.on(EVENT, this);
//        } catch (Exception ex) {
//        }
//    }

    public void disConnect() {
        try {
            mSocket.off(EVENT);
        } catch (Exception ex) {
        }
    }
}
