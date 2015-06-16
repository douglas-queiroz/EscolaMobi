package br.edu.escola.escolamobi.model;

import java.io.Serializable;

/**
 * Created by douglasqueiroz on 6/13/15.
 */
public class Message extends AbstractModel implements Serializable{



    public static final String TABLE = "message";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_STUDENT = "student";
    public static final String COLUMN_ID_SERVER = "id_server";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String SQL_CREATE = "CREATE TABLE " + TABLE + "( "
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_MESSAGE + " TEXT,"
            + COLUMN_STUDENT + " TEXT,"
            + COLUMN_ID_SERVER + " INTEGER,"
            + COLUMN_STATUS + " INTEGER,"
            + COLUMN_UPDATED_AT + " TEXT)";
    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE;
    private static final long serialVersionUID = 8318470044063034283L;

    private String title;
    private String message;
    private String student;
    private int idService;
    private Status status;
    private String updatedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum Status{
        NOT_READY(0), READY(1), UPDATED(2);

        private int index;

        private Status(int index){
            this.index = index;
        }

        public int getIndex(){
            return index;
        }
    }
}
