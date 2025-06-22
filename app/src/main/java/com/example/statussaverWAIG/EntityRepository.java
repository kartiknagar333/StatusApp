package com.example.statussaverWAIG;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EntityRepository {
    private DAO entityDAO;
    private LiveData<List<Entity>> alluser;

    public EntityRepository(Application application){
        EntityDatabase database = EntityDatabase.getInstance(application);
        entityDAO = database.userDAO();
        alluser = entityDAO.getAlluser();
    }

    public void insert(Entity user){
        new InserEntity(entityDAO).execute(user);
    }

    public void update(Entity user){
        new UpdateEntity(entityDAO).execute(user);
    }

    public void delete(Entity user){
        new DeleteEntity(entityDAO).execute(user);
    }

    public void deleteAlluser(){
        new InserEntity(entityDAO).execute();
    }

    public LiveData<List<Entity>> getAllusers(){
        return alluser;
    }

    private static class InserEntity extends AsyncTask<Entity,Void,Void>{
        private DAO dao;
        private InserEntity(DAO dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Entity... entities) {
            dao.insert(entities[0]);
            return null;
        }
    }
    private static class UpdateEntity extends AsyncTask<Entity,Void,Void>{
        private DAO dao;
        private UpdateEntity(DAO dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Entity... entities) {
            dao.update(entities[0]);
            return null;
        }
    }
    private static class DeleteEntity extends AsyncTask<Entity,Void,Void>{
        private DAO dao;
        private DeleteEntity(DAO dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Entity... entities) {
            dao.delete(entities[0]);
            return null;
        }
    }
    private static class DeleteAllEntity extends AsyncTask<Void,Void,Void>{
        private DAO dao;
        private DeleteAllEntity(DAO dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAlluser();
            return null;
        }
    }
}
