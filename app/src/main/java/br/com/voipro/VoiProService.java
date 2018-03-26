package br.com.voipro;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneFriendList;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;

import java.nio.ByteBuffer;

import br.com.voipro.entity.UserEntity;

public class VoiProService extends Service implements LinphoneCoreListener{
    private LinphoneCore linphoneCore;
    private static LinphoneCore staticLinphoneCore;
    private LinphoneCoreFactory linphoneCoreFactory;
    private LinphoneAuthInfo linphoneAuthInfo;
    private LinphoneProxyConfig linphoneProxyConfig;
    private LinphoneAddress linphoneAddress;
    private LinphoneCall linphoneCall;
    private boolean iterate;

    public static LinphoneCore instance(){
        return staticLinphoneCore;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final UserEntity user = (UserEntity)intent.getSerializableExtra("user");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startServer(user);
                } catch (LinphoneCoreException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startServer(UserEntity userEntity) throws LinphoneCoreException, InterruptedException {
        iterate = true;
        linphoneCoreFactory  = LinphoneCoreFactory.instance();
        linphoneCore = linphoneCoreFactory.createLinphoneCore(this, getApplicationContext());
        linphoneCore.enableSpeaker(true);
        linphoneAddress = linphoneCoreFactory.createLinphoneAddress(userEntity.getUser(), userEntity.getHost(), "DISPLAY NAME");
        linphoneAddress.setTransport(LinphoneAddress.TransportType.LinphoneTransportUdp);
        linphoneProxyConfig = linphoneCore.createProxyConfig(getSIPAddress(userEntity.getUser(), userEntity.getHost()), "sip:" + linphoneAddress.getDomain(), null, true);
        linphoneProxyConfig.setAddress(linphoneAddress);
        linphoneAuthInfo = linphoneCoreFactory.createAuthInfo(linphoneAddress.getUserName(), userEntity.getPassword(), null, linphoneAddress.getDomain());
        linphoneCore.addAuthInfo(linphoneAuthInfo);
        linphoneCore.addProxyConfig(linphoneProxyConfig);
        linphoneCore.setDefaultProxyConfig(linphoneProxyConfig);
        staticLinphoneCore = linphoneCore;

        while(iterate){
            linphoneCore.iterate();
            Thread.sleep(1);
        }
    }

    private String getSIPAddress(String user, String host){
        return "sip:" + user + "@" + host;
    }

    private void notifyUser(String title){
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tvStatus, title);

        Intent intent = new Intent(this, StartStopReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.btStartStop, pi);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(remoteViews)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setAutoCancel(false);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public class StartStopReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("CLICOU BOY");
        }
    }

    @Override
    public void authInfoRequested(LinphoneCore linphoneCore, String s, String s1, String s2) {
        System.out.println("");
    }

    @Override
    public void authenticationRequested(LinphoneCore linphoneCore, LinphoneAuthInfo linphoneAuthInfo, LinphoneCore.AuthMethod authMethod) {

    }

    @Override
    public void callStatsUpdated(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCallStats linphoneCallStats) {
        System.out.println("");
    }

    @Override
    public void newSubscriptionRequest(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend, String s) {

    }

    @Override
    public void notifyPresenceReceived(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend) {

    }

    @Override
    public void dtmfReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, int i) {

    }

    @Override
    public void notifyReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneAddress linphoneAddress, byte[] bytes) {
        System.out.println("");
    }

    @Override
    public void transferState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state) {

    }

    @Override
    public void infoReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneInfoMessage linphoneInfoMessage) {
        System.out.println("");
    }

    @Override
    public void subscriptionStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, SubscriptionState subscriptionState) {

    }

    @Override
    public void publishStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, PublishState publishState) {

    }

    @Override
    public void show(LinphoneCore linphoneCore) {

    }

    @Override
    public void displayStatus(LinphoneCore linphoneCore, String s) {
         notifyUser(s);
    }

    @Override
    public void displayMessage(LinphoneCore linphoneCore, String s) {

    }

    @Override
    public void displayWarning(LinphoneCore linphoneCore, String s) {

    }

    @Override
    public void fileTransferProgressIndication(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, int i) {

    }

    @Override
    public void fileTransferRecv(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, byte[] bytes, int i) {

    }

    @Override
    public int fileTransferSend(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, ByteBuffer byteBuffer, int i) {
        return 0;
    }

    @Override
    public void globalState(LinphoneCore linphoneCore, LinphoneCore.GlobalState globalState, String s) {

    }

    @Override
    public void registrationState(LinphoneCore linphoneCore, LinphoneProxyConfig linphoneProxyConfig, LinphoneCore.RegistrationState registrationState, String s) {

    }

    @Override
    public void configuringStatus(LinphoneCore linphoneCore, LinphoneCore.RemoteProvisioningState remoteProvisioningState, String s) {

    }

    @Override
    public void messageReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneChatMessage linphoneChatMessage) {

    }

    @Override
    public void messageReceivedUnableToDecrypted(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneChatMessage linphoneChatMessage) {

    }

    @Override
    public void callState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state, String s) {
        updateCallState(linphoneCore, linphoneCall, state, s);
    }

    @Override
    public void callEncryptionChanged(LinphoneCore linphoneCore, LinphoneCall linphoneCall, boolean b, String s) {

    }

    @Override
    public void notifyReceived(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, String s, LinphoneContent linphoneContent) {

    }

    @Override
    public void isComposingReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom) {

    }

    @Override
    public void ecCalibrationStatus(LinphoneCore linphoneCore, LinphoneCore.EcCalibratorStatus ecCalibratorStatus, int i, Object o) {

    }

    @Override
    public void uploadProgressIndication(LinphoneCore linphoneCore, int i, int i1) {

    }

    @Override
    public void uploadStateChanged(LinphoneCore linphoneCore, LinphoneCore.LogCollectionUploadState logCollectionUploadState, String s) {

    }

    @Override
    public void friendListCreated(LinphoneCore linphoneCore, LinphoneFriendList linphoneFriendList) {

    }

    @Override
    public void friendListRemoved(LinphoneCore linphoneCore, LinphoneFriendList linphoneFriendList) {

    }

    @Override
    public void networkReachableChanged(LinphoneCore linphoneCore, boolean b) {

    }

    private void updateCallState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state, String s){
        switch(state.value()){
            case 1: {
                try {
                    linphoneCore.acceptCall(linphoneCall);
                } catch (LinphoneCoreException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 6:{
                LinphoneCallParams linphoneCallParams = linphoneCall.getCurrentParams();
                linphoneCallParams.setRecordFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Teste/xkkk.mp3");
                linphoneCall.startRecording();
                System.out.println(state.value());

                break;
            }
            case 13: {
                linphoneCall.stopRecording();
                break;
            }
        }
    }
}
