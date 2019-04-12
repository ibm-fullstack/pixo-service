package com.pixo.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pixo.models.FileItemEntity;
import com.pixo.models.Image;
import com.pixo.models.User;

@Repository
public interface FileItemRepository extends CrudRepository<FileItemEntity, Long> {
	public Iterable<FileItemEntity> findAllByUser(User user);
}