package br.edu.escola.escolamobi.views;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.escola.escolamobi.R;
import br.edu.escola.escolamobi.model.Message;

/**
 * Created by douglasqueiroz on 6/15/15.
 */
public class MessageApater extends ArrayAdapter<Message> {

    public MessageApater(Context context, List<Message> items) {
        super(context, R.layout.message_adapter, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.message_adapter, null);

        }

        Message m = getItem(position);

        if (m != null) {
            TextView txtTile = (TextView) v.findViewById(R.id.txt_title);
            TextView txtMessage = (TextView) v.findViewById(R.id.txt_message);

            if (txtTile != null) {
                txtTile.setText(m.getTitle());
                if(m.getStatus() == Message.Status.NOT_READY){
                    txtTile.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }

            if (txtMessage != null) {
                txtMessage.setText(m.getMessage());
            }
        }

        return v;
    }
}
