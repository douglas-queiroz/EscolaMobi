package br.edu.escola.escolamobi.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.escola.escolamobi.dao.AbstractDAO;
import br.edu.escola.escolamobi.dao.MessageDAO;
import br.edu.escola.escolamobi.interfaces.MessageHandler;
import br.edu.escola.escolamobi.model.Message;
import br.edu.escola.escolamobi.util.HttpUtil;

/**
 * Created by douglasqueiroz on 6/15/15.
 */
public class MessageServce extends AbstractService<Message> {

    private MessageDAO dao;
    private Context ctx;

    public MessageServce(Context context){
        this.ctx = context;
        dao = new MessageDAO(context);
    }

    @Override
    public MessageDAO getDao() {
        return dao;
    }

    public Message getLast(){
        return dao.getLast();
    }

    public void getMessagesOnServer(Context ctx, int userId, final MessageHandler handler){
        if(HttpUtil.isConnected(ctx)){
            Message lastMessage = this.getLast();
            int lastId = 0;
            if (lastMessage != null){
                lastId = lastMessage.getIdService();
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
                            message.setIdService(jObject.getInt("id"));

                            if (!jObject.isNull("student")){
                                JSONObject jStudent = jObject.getJSONObject("student");
                                message.setDestination(jStudent.getString("name"));
                            }else{
                                JSONObject jStudent = jObject.getJSONObject("students_class");
                                message.setDestination(jStudent.getString("name"));
                            }

                            dao.insert(message);
                        }


                    } catch (JSONException e) {
                        Log.e("DOUGLAS", e.getMessage());
                        handler.onJsonError();
                    }finally {
                        handler.onFinish();
                    }
                }
            }.execute(HttpUtil.SERVER+"/get_messages.json?user_id="+userId+"&last_id="+lastId);
        }else{
            handler.withoutInternet();
        }
    }
}
