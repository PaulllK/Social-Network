package com.example.socialnetwork.repositories;

import com.example.socialnetwork.domain.User;

public interface Repository<E> {

    public abstract void add(E entity);

    public abstract void delete(int id);

    // public void update(E entity);

    public abstract E find(E entity);
}
