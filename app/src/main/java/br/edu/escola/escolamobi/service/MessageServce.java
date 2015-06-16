package br.edu.escola.escolamobi.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    public void updateOnService(Activity ctx){
        if(HttpUtil.isConnected(ctx)) {
            final List<Message> messages = dao.getReady();
            if (messages.isEmpty()){
                return;
            }
            String ids = "[";
            for (int i = 0; i < messages.size(); i++) {
                ids += messages.get(i).getIdService();
                if (i + 1 < messages.size())
                    ids += ",";
            }
            ids += "]";

            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("ids", ids));

            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... urls) {

                    return HttpUtil.POST(urls[0], params);
                }

                @Override
                protected void onPostExecute(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("status").equals("ok")) {
                            for (int i = 0; i < messages.size(); i++) {
                                Message message = messages.get(i);
                                message.setStatus(Message.Status.UPDATED);
                                save(message);
                            }
                        }


                    } catch (JSONException e) {
                        Log.e("DOUGLAS", "Erro no parsing do JSON", e);
                    }
                }
            }.execute(HttpUtil.SERVER + "/check");
        }
    }
}
