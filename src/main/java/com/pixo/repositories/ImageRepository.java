package com.pixo.repositories;

import org.springframework.data.repository.CrudRepository;

import com.pixo.models.*;

public interface ImageRepository extends CrudRepository<Image,Long> {
        public Iterable<Image> findAllByUser(User user);
}
