package br.edu.escola.escolamobi.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.edu.escola.escolamobi.R;
import br.edu.escola.escolamobi.interfaces.MessageHandler;
import br.edu.escola.escolamobi.model.Message;
import br.edu.escola.escolamobi.model.User;
import br.edu.escola.escolamobi.service.MessageServce;
import br.edu.escola.escolamobi.util.HttpUtil;


public class LoginActivity extends ActionBarActivity {

    private String SENDER_ID = "55062672796";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "MEUFILHO";

    private MessageServce service;
    private SharedPreferences prefs;

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView txtErrorMsg;
    private Button btnAccess;
    private String regId;
    private Context context;

    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);
        service = new MessageServce(this);
        loadComponents();
        registerInBackground();

        prefs = getPreferences(context);
    }

    private void loadComponents(){
        edtUsername = (EditText) findViewById(R.id.edt_login_username);
        edtPassword = (EditText) findViewById(R.id.edt_login_password);

        btnAccess = (Button) findViewById(R.id.btn_login_access);
        txtErrorMsg = (TextView) findViewById(R.id.txt_error_msg);
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(((Activity) context), "",
                        "Por favor, aguarde...", true);

                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... urls) {

                        return HttpUtil.GET(urls[0]);
                    }

                    // onPostExecute displays the results of theAsyncTask.
                    @Override
                    protected void onPostExecute(String result) {
                        dialog.dismiss();

                        if (result.equals("null")) {
                            txtErrorMsg.setText("Usuário ou senha incorreto!");
                            txtErrorMsg.setVisibility(View.VISIBLE);
                            return;
                        }
                        try {
                            JSONObject userJson = new JSONObject(result);
                            int id = userJson.getInt(User.ID_KEY);
                            String name = userJson.getString(User.NAME_KEY);
                            String login = userJson.getString(User.LOGIN_KEY);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(User.ID_KEY, id);
                            editor.putString(User.NAME_KEY, name);
                            editor.putString(User.LOGIN_KEY, login);
                            editor.commit();

                            getMessagesOnServer(id);

                        } catch (JSONException e) {
                            Log.e("DOUGLAS", "Erro no parsing do JSON", e);
                        }
                    }
                }.execute(HttpUtil.SERVER + "/login_parent?login=" + username + "&password=" + password + "&reg_id=" + regId);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerInBackground() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] params) {
                    String msg = "";
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(context);
                        }
                        regId = gcm.register(SENDER_ID);
                        int appVersion = getAppVersion(context);
                        msg = "Device registered, registration ID=" + regId;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(User.REG_ID, regId);
                        editor.putInt(User.VERSION, appVersion);
                        editor.commit();
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                    return msg;
                }

                @Override
                protected void onPostExecute(Object o) {
                    //mDisplay.append(o + "\n");
                }
            }.execute(null, null, null);
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");

                finish();
            }
            return false;
        }
        return true;
    }

    private SharedPreferences getPreferences(Context ctx){
        return getSharedPreferences(User.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void getMessagesOnServer(int userId){
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
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setText("Ocorreu um erro de comunicação!");
                callListActivity();
            }

            @Override
            public void withoutInternet() {
                dialog.dismiss();
                Toast toast = Toast.makeText(context, "Não foi possível conectar ao servidor!",Toast.LENGTH_LONG);
                toast.show();
                callListActivity();
            }
        });
    }

    private void callListActivity(){
        Intent intent = new Intent(this, ListMessageActivity.class);
        startActivity(intent);
        finish();
    }
}
