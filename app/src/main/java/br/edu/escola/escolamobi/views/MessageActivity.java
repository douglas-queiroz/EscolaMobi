package br.edu.escola.escolamobi.views;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.edu.escola.escolamobi.R;
import br.edu.escola.escolamobi.model.Message;
import br.edu.escola.escolamobi.service.MessageServce;


public class MessageActivity extends ActionBarActivity implements Button.OnClickListener {

    private TextView txtTitle;
    private TextView txtStudent;
    private TextView txtMessage;
    private Button btnOK;

    private Message message;
    private MessageServce servce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        message = (Message) getIntent().getExtras().getSerializable(ListMessageActivity.MESSAGE_ARG);
        servce = new MessageServce(this);

        loadComponets();
        loadInformations();
    }

    private void loadComponets(){
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtStudent = (TextView) findViewById(R.id.txt_student);
        txtMessage = (TextView) findViewById(R.id.txt_message);
        btnOK = (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(this);
    }

    private void loadInformations(){
        txtTitle.setText(message.getTitle());
        txtStudent.setText(message.getStudent());
        txtMessage.setText(message.getMessage());
    }

    @Override
    public void onClick(View v) {
        message.setStatus(Message.Status.READY);
        servce.save(message);

        onBackPressed();
    }
}
