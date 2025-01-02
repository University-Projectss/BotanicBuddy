package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.Post;
import com.mops.bb_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends CrudRepository<Post, UUID> {
    @Query("SELECT p FROM Post p WHERE p.user <> :user")
    Page<Post> findAllNotOwnedByUser(Pageable pageable, User user);
}
