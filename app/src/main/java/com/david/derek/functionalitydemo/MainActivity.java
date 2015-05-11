package com.david.derek.functionalitydemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SmsListener.SmsListenerEvent{

    private static final String TAG = "MainActivity";

    private static Context mContext;

    private static Button buttonSendTxt;
    private static Button buttonSendEmail;
    private static Button buttonBindTxt;
    private static Button buttonBindEmail;

    private static EditText etPhone;
    private static EditText etSenderEmail;
    private static EditText etReceiverEmail;
    private static EditText etTxt;


    private static boolean isTxtBinded = false;
    private static boolean isEmailBinded = false;

    private static BroadcastReceiver smsListener;

    public static Context getmContext(){
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this.getApplicationContext();

        buttonSendTxt = (Button) findViewById(R.id.buttonSentTxt);
        buttonSendEmail = (Button) findViewById(R.id.buttonSentTxt);
        buttonBindTxt = (Button) findViewById(R.id.buttonTxtListener);
        buttonBindEmail = (Button) findViewById(R.id.buttonEmailListener);

        etPhone = (EditText) findViewById(R.id.phone);
        etSenderEmail = (EditText) findViewById(R.id.editTextSenderEmail);
        etReceiverEmail = (EditText) findViewById(R.id.editTextReceiverEmail);
        etTxt = (EditText) findViewById(R.id.editTextTxt);

        smsListener = new SmsListener();

        buttonSendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "SMS sending message" + etTxt.getText().toString() + " to:" + etPhone.getText().toString(),
                        Toast.LENGTH_SHORT).show();
                sendSMS(etPhone.getText().toString(), etTxt.getText().toString());
            }
        });
        //register sms listener
        buttonBindTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTxtBinded){
//                    binded, so we unbind
                    unregisterReceiver(smsListener);
                    buttonBindTxt.setText("register sms listener");
                    Toast.makeText(getBaseContext(), "smsListener unregistered", Toast.LENGTH_SHORT).show();
                } else {
                    // Register a broadcast receiver
/*                    IntentFilter intentFilter = new IntentFilter("android.intent.action.DATA_SMS_RECEIVED");
                    intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
                    intentFilter.setPriority(10);
                    intentFilter.addDataScheme("sms");
                    intentFilter.addDataAuthority("*", "6734");*/

                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                    registerReceiver(smsListener, intentFilter);
                    buttonBindTxt.setText("unregister sms listener");
                    Toast.makeText(getBaseContext(), "smsListener registered", Toast.LENGTH_SHORT).show();
                }
                isTxtBinded = !isTxtBinded;
            }
        });
        //register email listener
        buttonBindEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailBinded){
//                    binded, so we unbind
                    buttonBindEmail.setText("register email listener");
//                    Toast.makeText(getBaseContext(), "smsListener unregistered", Toast.LENGTH_SHORT);
                } else {
                    // Register a broadcast receiver
                    IntentFilter intentFilter = new IntentFilter("android.intent.action.DATA_SMS_RECEIVED");
                    intentFilter.setPriority(10);
                    intentFilter.addDataScheme("sms");
                    intentFilter.addDataAuthority("*", "6734");
                    registerReceiver(smsListener, intentFilter);
                    buttonBindEmail.setText("unregister email listener");
                    Toast.makeText(getBaseContext(), "emailListener registered", Toast.LENGTH_SHORT);
                }
                isEmailBinded = !isEmailBinded;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
 * BroadcastReceiver mBrSend; BroadcastReceiver mBrReceive;
 */
    private void sendSMS(String phoneNumber, String message) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0,
                new Intent(mContext, SmsSentReceiver.class), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, 0,
                new Intent(mContext, SmsDeliveredReceiver.class), 0);
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
            }
            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                    sentPendingIntents, deliveredPendingIntents);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getBaseContext(), "SMS sending failed...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMessageReceived(String sender, String message) {
        String result = "";
        result += "Received a new message from:" + sender + "\n" +
                message;
        etTxt.setText(result);
        Log.d(TAG, result);
    }

    public class SmsDeliveredReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    public class SmsSentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show();

                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "SMS generic failure", Toast.LENGTH_SHORT)
                            .show();

                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "SMS no service", Toast.LENGTH_SHORT)
                            .show();

                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "SMS null PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context, "SMS radio off", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
