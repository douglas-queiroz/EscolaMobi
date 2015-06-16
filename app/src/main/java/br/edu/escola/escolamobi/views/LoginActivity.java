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
import br.edu.escola.escolamobi.model.Message;
import br.edu.escola.escolamobi.model.User;
import br.edu.escola.escolamobi.service.MessageServce;
import br.edu.escola.escolamobi.util.HttpUtil;
import br.edu.escola.escolamobi.util.Mask;


public class LoginActivity extends ActionBarActivity {

    private String SENDER_ID = "980552783328";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "MEUFILHO";

    private MessageServce service;
    private SharedPreferences prefs;

    private EditText edtCpf;
    private EditText edtBirthDay;
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
        edtCpf = (EditText) findViewById(R.id.edt_login_cpf);
        edtCpf.addTextChangedListener(Mask.insert("###.###.###-##", edtCpf));

        edtBirthDay = (EditText) findViewById(R.id.edt_login_birth_day);
        edtBirthDay.addTextChangedListener(Mask.insert("##/##/####", edtBirthDay));

        btnAccess = (Button) findViewById(R.id.btn_login_access);
        txtErrorMsg = (TextView) findViewById(R.id.txt_error_msg);
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(((Activity) context), "",
                        "Loading. Please wait...", true);

                String cpf = edtCpf.getText().toString().replace(".", "").replace("-", "");
                String[] arrayBirth = edtBirthDay.getText().toString().split("/");
                String birth = arrayBirth[2] + "-" + arrayBirth[1] + "-" + arrayBirth[0];

                Log.i("TAG", "CPF = " + cpf + " DATA = " + birth);

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
                            txtErrorMsg.setText("CPF ou data de nascimento incorreto!");
                            txtErrorMsg.setVisibility(View.VISIBLE);
                            return;
                        }
                        try {
                            JSONObject userJson = new JSONObject(result);
                            int id = userJson.getInt(User.ID_KEY);
                            String name = userJson.getString(User.NAME_KEY);
                            String cpf = userJson.getString(User.CPF_KEY);
                            String regId = userJson.getString(User.REG_ID);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(User.ID_KEY, id);
                            editor.putString(User.NAME_KEY, name);
                            editor.putString(User.CPF_KEY, cpf);
                            editor.commit();

                            getMessagesOnServer(id);

                        } catch (JSONException e) {
                            Log.e("DOUGLAS", "Erro no parsing do JSON", e);
                        }
                    }
                }.execute(HttpUtil.SERVER + "/login_parent.json?login=" + cpf + "&senha=" + birth + "&registration_id=" + regId);
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
        if(HttpUtil.isConnected(this)){
            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);

            Message lastMessage = service.getLast();
            String lastMessageDate = "1980-01-01T00:00:00.072Z";
            if (lastMessage != null){
                lastMessageDate = lastMessage.getUpdatedAt();
            }

            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... urls) {

                    return HttpUtil.GET(urls[0]);
                }

                // onPostExecute displays the results of theAsyncTask.
                @Override
                protected void onPostExecute(String result) {
                    try {
                        JSONArray jArray = new JSONArray(result);
                        for(int i = 0; i < jArray.length(); i++){
                            JSONObject jObject = jArray.getJSONObject(i);
                            Message message = new Message();
                            message.setIdService(jObject.getInt("id"));
                            message.setTitle(jObject.getString("title"));
                            message.setMessage(jObject.getString("message"));
                            if (jObject.getString("status").equals("0")) {
                                message.setStatus(Message.Status.NOT_READY);
                            }else{
                                message.setStatus(Message.Status.UPDATED);
                            }
                            message.setUpdatedAt(jObject.getString("created_at"));
                            JSONObject jStudent = jObject.getJSONObject("student");
                            message.setStudent(jStudent.getString("name"));

                            service.save(message);
                        }


                    } catch (JSONException e) {
                        Log.e("DOUGLAS", "Erro no parsing do JSON", e);
                    }finally {
                        dialog.dismiss();
                        callListActivity();
                    }
                }
            }.execute(HttpUtil.SERVER+"/notifications.json?parent_id="+userId+"&created_at="+lastMessageDate);
        }else{
            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText("Não foi possível conectar ao servidor!");
            callListActivity();
        }
    }

    private void callListActivity(){
        Intent intent = new Intent(this, ListMessageActivity.class);
        startActivity(intent);
        finish();
    }
}
