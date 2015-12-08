package br.edu.escola.escolamobi.views;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.List;

import br.edu.escola.escolamobi.R;
import br.edu.escola.escolamobi.model.Message;
import br.edu.escola.escolamobi.service.MessageServce;

public class ListMessageActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private MessageServce servce;
    private MessageApater messageApater;
    private AbsListView lvMessages;
    private List<Message> messages;

    public static final String MESSAGE_ARG = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_message);
        lvMessages = (AbsListView) findViewById(R.id.list_message);

        servce = new MessageServce(this);

        messages = servce.get();
        loadList(messages);
        lvMessages.setOnItemClickListener(this);
    }

    private void loadList(List<Message> messages){
        messageApater = new MessageApater(this, messages);
        ((AdapterView<ListAdapter>) lvMessages).setAdapter(messageApater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_message, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(this.MESSAGE_ARG, messages.get(position));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        messages = servce.get();
        loadList(messages);
    }
}
