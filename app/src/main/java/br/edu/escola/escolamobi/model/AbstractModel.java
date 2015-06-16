package br.edu.escola.escolamobi.model;

import java.io.Serializable;

/**
 * Created by douglasqueiroz on 6/15/15.
 */
public abstract class AbstractModel implements Serializable {

    private static final long serialVersionUID = -6330513521737472337L;

    public static final String COLUMN_ID = "_id";

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
