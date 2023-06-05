package com.allweb.springbootblogh2.service;

import com.allweb.springbootblogh2.exception.BadRequestException;
import com.allweb.springbootblogh2.exception.DataNotFoundException;
import com.allweb.springbootblogh2.model.entity.Post;
import com.allweb.springbootblogh2.model.entity.Tag;
import com.allweb.springbootblogh2.repository.PostRepository;
import com.allweb.springbootblogh2.repository.TagRepository;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;

  private final TagRepository tagRepository;

  public List<Post> getAllPosts(String title) {
    List<Post> postList;
    if (title == null) {
      postList = postRepository.findAll();
    } else {
      postList = postRepository.findByTitleContaining(title);
    }
    return postList;
  }

  public Post getById(Long id) {
    Optional<Post> post = postRepository.findById(id);
    if (post.isPresent()) {
      return post.get();
    } else {
      throw new DataNotFoundException(
          MessageFormat.format("Post id {0} not found", String.valueOf(id)));
    }
  }

  public Post createOrUpdate(Post postRequest) {
    Optional<Post> existingPost = postRepository.findById(postRequest.getId());

    if (existingPost.isPresent()) {
      Post postUpdate = existingPost.get();

      postUpdate.setTitle(postRequest.getTitle());
      postUpdate.setBody(postRequest.getBody());

      // save foreign key
      postUpdate.setAuthor(postRequest.getAuthor());

      return postRepository.save(postUpdate);
    } else {
      return postRepository.save(postRequest);
    }
  }

  public List<Tag> getAllTagsByPostId(Long id) {
    if (!postRepository.existsById(id)) {
      throw new DataNotFoundException(
          MessageFormat.format("Post id {0} not found", String.valueOf(id)));
    }

    List<Tag> tagList = postRepository.findById(id).get().getTagList();
    return tagList;
  }

  public Tag addTag(Long postId, Tag tagRequest) {
    Tag existingPost =
        postRepository
            .findById(postId)
            .map(
                post -> {
                  Optional<Tag> existingTag = tagRepository.findById(tagRequest.getId());
                  if (tagRequest.getId() != 0) {
                    if (existingTag.isPresent()) {
                      post.addTag(existingTag.get());
                      postRepository.save(post);
                      return existingTag.get();
                    } else {
                      throw new DataNotFoundException(
                          MessageFormat.format(
                              "Tag id {0} not found", String.valueOf(tagRequest.getId())));
                    }
                  } else {
                    // create new tag
                    post.addTag(tagRequest);
                    return tagRepository.save(tagRequest);
                  }
                })
            .orElseThrow(
                () ->
                    new DataNotFoundException(
                        MessageFormat.format("Post id {0} not found", String.valueOf(postId))));

    return existingPost;
  }

  public void deleteTagFromPost(Long postId, Long tagId) {
    Optional<Post> post = postRepository.findById(postId);
    if (post.isPresent()) {
      post.get().removeTag(tagId);
      postRepository.save(post.get());
    } else {
      throw new BadRequestException("Delete error, please check ID and try again");
    }
  }

  public void deleteById(Long id) {
    Optional<Post> post = postRepository.findById(id);
    if (post.isPresent()) {
      postRepository.deleteById(id);
    } else {
      throw new BadRequestException("Delete error, please check ID and try again");
    }
  }
}
