package com.pixo.repositories;

import org.springframework.data.repository.CrudRepository;

import com.pixo.models.*;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

    public Iterable<Comment> findAllByMediaitem(FileItemEntity mediaitem);
}
