package br.edu.escola.escolamobi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.edu.escola.escolamobi.model.Message;

/**
 * Created by douglasqueiroz on 6/15/15.
 */
public class MessageDAO extends AbstractDAO<Message> {
    public MessageDAO(Context context) {
        super(context);
    }

    @Override
    protected ContentValues convertToContent(Message object) {
        ContentValues values = new ContentValues();
        values.put(Message.COLUMN_TITLE, object.getTitle());
        values.put(Message.COLUMN_MESSAGE, object.getMessage());
        values.put(Message.COLUMN_ID_SERVER, object.getIdService());
        values.put(Message.COLUMN_DESTINATION, object.getDestination());
        values.put(Message.COLUMN_UPDATED_AT, object.getUpdatedAt());

        return values;
    }

    @Override
    protected Message convertToObject(Cursor cursor) {
        Message message = new Message();

        int column_id;

        column_id = cursor.getColumnIndex(Message.COLUMN_ID);
        message.setId(cursor.getInt(column_id));

        column_id = cursor.getColumnIndex(Message.COLUMN_TITLE);
        message.setTitle(cursor.getString(column_id));

        column_id = cursor.getColumnIndex(Message.COLUMN_MESSAGE);
        message.setMessage(cursor.getString(column_id));

        column_id = cursor.getColumnIndex(Message.COLUMN_ID_SERVER);
        message.setIdService(cursor.getInt(column_id));

        column_id = cursor.getColumnIndex(Message.COLUMN_DESTINATION);
        message.setDestination(cursor.getString(column_id));

        column_id = cursor.getColumnIndex(Message.COLUMN_UPDATED_AT);
        message.setUpdatedAt(cursor.getString(column_id));

        return message;
    }

    @Override
    protected String getTable() {
        return Message.TABLE;
    }

    @Override
    protected String[] getColumns() {
        String[] columns = {Message.COLUMN_ID,
                Message.COLUMN_TITLE,
                Message.COLUMN_MESSAGE,
                Message.COLUMN_ID_SERVER,
                Message.COLUMN_DESTINATION,
                Message.COLUMN_UPDATED_AT};

        return columns;
    }

    public Message getLast(){
        Cursor curso = db.query(Message.TABLE, getColumns(), null, null, null, null, Message.COLUMN_ID + " DESC", "1");
        Message message = null;
        if(curso.getCount()!=0){
            curso.moveToFirst();
            message = convertToObject(curso);
        }
        curso.close();

        return message;
    }
}
