package br.edu.escola.escolamobi.model;

import java.util.Date;

/**
 * Created by douglasqueiroz on 6/13/15.
 */
public class User {
    public static final String ID_KEY = "id";
    public static final String LOGIN_KEY = "login";
    public static final String NAME_KEY = "name";
    public static final String REG_ID = "registration_id";
    public static final String VERSION = "version";

    private String login;
    private String name;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
