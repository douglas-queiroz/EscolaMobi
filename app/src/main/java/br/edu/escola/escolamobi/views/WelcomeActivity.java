package br.edu.escola.escolamobi.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.escola.escolamobi.R;
import br.edu.escola.escolamobi.interfaces.MessageHandler;
import br.edu.escola.escolamobi.model.Message;
import br.edu.escola.escolamobi.model.User;
import br.edu.escola.escolamobi.service.MessageServce;
import br.edu.escola.escolamobi.util.HttpUtil;


public class WelcomeActivity extends ActionBarActivity {

    private int userId;
    private MessageServce service;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = this;
        service = new MessageServce(this);

        this.verifyLogin();
    }

    private void verifyLogin(){
        final SharedPreferences pref = getGCMPreferences(this);
        userId = pref.getInt(User.ID_KEY, 0);

        if(userId == 0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            getMessagesOnServer();

        }
    }

    private void getMessagesOnServer(){
        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Aguarde, carregando...", true);
        service.getMessagesOnServer(this, userId, new MessageHandler() {

            @Override
            public void onFinish() {
                dialog.dismiss();
                callListActivity();
            }

            @Override
            public void onJsonError() {
                Toast toast = Toast.makeText(context, "Não foi possível conectar ao servidor!",Toast.LENGTH_LONG);
                toast.show();
                callListActivity();
            }

            @Override
            public void withoutInternet() {
                dialog.dismiss();
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setText("Não foi possível conectar ao servidor!");
                callListActivity();
            }
        });
    }

    private void callListActivity(){
        Intent intent = new Intent(this, ListMessageActivity.class);
        startActivity(intent);
        finish();
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(User.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}
