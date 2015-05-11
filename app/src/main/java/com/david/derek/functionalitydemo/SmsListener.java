package com.david.derek.functionalitydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by derek on 11/05/15.
 */
public class SmsListener extends BroadcastReceiver {

    private static String TAG = "SmsListener";
    private SharedPreferences preferences;
    private static Context mContext;

    public SmsListener(){
    }

    @Override
/*        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null){
                    //---retrieve the SMS message received---
                    try{
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for(int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            String msgBody = msgs[i].getMessageBody();
                            Log.d("SMS onReceive", "Successful: " + msg_from + ":" + msgBody);
                        }
                    }catch(Exception e){
                            Log.d("Exception caught",e.getMessage());
                    }
                }
            }
        }*/
    public void onReceive(Context context, Intent intent) {
        mContext = context.getApplicationContext();
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String sender = smsMessage.getDisplayOriginatingAddress();


                Toast.makeText(context, "Demo: got new message from " + sender + ":" + messageBody, Toast.LENGTH_LONG).show();
/*                SmsListenerEvent sle = ((SmsListenerEvent)MainActivity.getmContext());
                sle.onMessageReceived(
                        sender, messageBody);*/


                Log.d(TAG, "Successful: " + smsMessage.getDisplayOriginatingAddress() + ":" + messageBody);
            }
        }
    }

    public interface SmsListenerEvent{
        public void onMessageReceived(String sender, String message);
    }

    public static final class Constant{
        String PACKAGE_NAME = "com.david.derek.functionalitydemo";
        String NEW_MESSAGE_INTENT = PACKAGE_NAME + ".NEW_MESSAGE";

    }
}
