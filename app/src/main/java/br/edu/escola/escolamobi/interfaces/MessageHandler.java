package br.edu.escola.escolamobi.interfaces;

/**
 * Created by douglas on 12/8/15.
 */
public interface MessageHandler {
    public void onFinish();
    public void onJsonError();
    public void withoutInternet();
}
