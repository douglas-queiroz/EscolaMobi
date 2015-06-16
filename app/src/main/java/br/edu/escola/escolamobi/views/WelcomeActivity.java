package br.edu.escola.escolamobi.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.escola.escolamobi.R;
import br.edu.escola.escolamobi.model.Message;
import br.edu.escola.escolamobi.model.User;
import br.edu.escola.escolamobi.service.MessageServce;
import br.edu.escola.escolamobi.util.HttpUtil;


public class WelcomeActivity extends ActionBarActivity {

    private int userId;
    private MessageServce service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
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
        if(HttpUtil.isConnected(this)){
            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);

            final Message lastMessage = service.getLast();
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

                            if(lastMessage.getIdService() != message.getIdService()) {
                                service.save(message);
                            }
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
        service.updateOnService(this);
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
