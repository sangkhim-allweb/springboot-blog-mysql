package com.allweb.springbootblogmysql.service;

import com.allweb.springbootblogmysql.exception.BadRequestException;
import com.allweb.springbootblogmysql.exception.DataNotFoundException;
import com.allweb.springbootblogmysql.model.entity.Author;
import com.allweb.springbootblogmysql.repository.AuthorRepository;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {

  private final AuthorRepository authorRepository;

  public List<Author> getAllAuthors() {
    List<Author> authorList = authorRepository.findAll();
    return authorList;
  }

  public Author getById(Long id) {
    Optional<Author> author = authorRepository.findById(id);
    if (author.isPresent()) {
      return author.get();
    } else {
      throw new DataNotFoundException(
          MessageFormat.format("Author id {0} not found", String.valueOf(id)));
    }
  }

  public Author createOrUpdate(Author authorRequest) {
    Optional<Author> existingAuthor = authorRepository.findById(authorRequest.getId());

    if (existingAuthor.isPresent()) {
      Author authorUpdate = existingAuthor.get();

      authorUpdate.setName(authorRequest.getName());

      return authorRepository.save(authorUpdate);
    } else {
      return authorRepository.save(authorRequest);
    }
  }

  public void deleteById(Long id) {
    Optional<Author> author = authorRepository.findById(id);
    if (author.isPresent()) {
      authorRepository.deleteById(id);
    } else {
      throw new BadRequestException("Delete error, please check ID and try again");
    }
  }
}