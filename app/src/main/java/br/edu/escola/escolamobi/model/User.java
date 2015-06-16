package br.edu.escola.escolamobi.model;

import java.util.Date;

/**
 * Created by douglasqueiroz on 6/13/15.
 */
public class User {
    public static final String ID_KEY = "id";
    public static final String CPF_KEY = "cpf";
    public static final String NAME_KEY = "name";
    public static final String REG_ID = "registration_id";
    public static final String VERSION = "version";

    private String cpf;
    private Date birth;
    private String name;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
