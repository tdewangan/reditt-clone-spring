package com.learn.reditt.repository;

import com.learn.reditt.model.Comment;
import com.learn.reditt.model.Post;
import com.learn.reditt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
