package br.edu.escola.escolamobi.service;

import java.util.List;

import br.edu.escola.escolamobi.dao.AbstractDAO;
import br.edu.escola.escolamobi.model.AbstractModel;

/**
 * Created by douglasqueiroz on 6/15/15.
 */
public abstract class AbstractService <T extends AbstractModel> {

    public long save(T object){
        if(object.getId() == 0){
            return getDao().insert(object);
        }else{
            getDao().update(object);
            return object.getId();
        }
    }

    public void delete(T object){
        getDao().delete(object);
    }

    public List<T> get(){
        return getDao().get();
    }

    public T get(int Id){
        return (T) getDao().get(Id);
    }

    public abstract AbstractDAO getDao();
}
