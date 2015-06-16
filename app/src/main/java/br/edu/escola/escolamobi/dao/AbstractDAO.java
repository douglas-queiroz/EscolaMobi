package br.edu.escola.escolamobi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.escola.escolamobi.helper.EscolaDbHelper;
import br.edu.escola.escolamobi.model.AbstractModel;

/**
 * Created by douglasqueiroz on 6/15/15.
 */
public abstract class AbstractDAO<T extends AbstractModel> {

    private EscolaDbHelper mDBHelper;
    protected SQLiteDatabase db;

    public AbstractDAO(Context context){
        mDBHelper = new EscolaDbHelper(context);
        db = mDBHelper.getWritableDatabase();
    }

    public long insert(T object){
        return db.insert(getTable(), null, convertToContent(object));
    }

    public void update(T object){
        ContentValues values = convertToContent(object);
        String selection = object.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(object.getId())};

        db.update(getTable(), values, selection, selectionArgs);
    }

    public void delete(T object){
        String selection = object.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(object.getId()) };
        db.delete(getTable(), selection, selectionArgs);
    }

    public List<T> get(){
        Cursor c = db.query(getTable(), getColumns(), null, null, null, null, "_id DESC");
        c.moveToFirst();

        List<T> objectLis = new ArrayList<T>();

        if (c.moveToFirst()) {
            do {
                T object = convertToObject(c);
                objectLis.add(object);
            } while (c.moveToNext());
        }
        c.close();

        return objectLis;
    }

    public T get(int Id){
        String selection = T.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(Id) };
        Cursor c = db.query(getTable(), getColumns(), selection, selectionArgs, null, null, null);

        T object = null;

        if (c.moveToFirst()) {
            object = convertToObject(c);
        }
        return object;
    }

    protected abstract ContentValues convertToContent(T object);

    protected abstract T convertToObject(Cursor cursor);

    protected abstract String getTable();

    protected abstract String[] getColumns();
}



