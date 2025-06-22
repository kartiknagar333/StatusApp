package com.example.statussaverWAIG;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EntityViewModel extends AndroidViewModel {

    private EntityRepository repository;
    private LiveData<List<Entity>> alluser;

    public EntityViewModel(@NonNull Application application) {
        super(application);
        repository = new EntityRepository(application);
        alluser = repository.getAllusers();
    }

    public void insert(Entity user){
        repository.insert(user);
    }

    public void update(Entity user){
        repository.update(user);
    }

    public void delete(Entity user){
        repository.delete(user);
    }

    public void deleteAll(Entity user){
        repository.deleteAlluser();
    }

    public LiveData<List<Entity>> getAlluser(){
        return alluser;
    }
}
