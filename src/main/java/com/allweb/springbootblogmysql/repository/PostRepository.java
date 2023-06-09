package com.allweb.springbootblogmysql.repository;

import com.allweb.springbootblogmysql.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByTitleContaining(String title);

}
