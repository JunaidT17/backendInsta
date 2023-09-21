package com.example.projetinsta.service;

import com.example.projetinsta.dto.CommentDto;
import com.example.projetinsta.dto.PostDto;
import com.example.projetinsta.dto.UserDto;
import com.example.projetinsta.dto.UserResponseDto;
import com.example.projetinsta.entity.Comment;
import com.example.projetinsta.entity.Post;
import com.example.projetinsta.entity.UserEntity;
import com.example.projetinsta.exception.NotAllowedException;
import com.example.projetinsta.exception.NotFoundException;
import com.example.projetinsta.repository.CommentRepository;
import com.example.projetinsta.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;
    private CommentRepository commentRepository;

    public void savePost(MultipartFile postResource, String text) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) auth.getPrincipal();

        Post post = new Post();
        post.setText(text);
        post.setPostDate(new Date());
        post.setCreator(userEntity);
        post.setLikeFromUsers(new ArrayList<>());
        File file = new File(new ClassPathResource("").getFile() + File.separator + "data");
        file.mkdir();
        String fileName = System.currentTimeMillis() + postResource.getOriginalFilename();
        File destOfFile = new File(file.getPath() + File.separator + fileName);
        postResource.transferTo(destOfFile);
        post.setPostPath(destOfFile.getPath());
        postRepository.save(
                post
        );
    }

    public List<PostDto> findAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(post -> new PostDto(post.getId(), post.getText(), post.getPostDate(), post.getLikeFromUsers().size(),
                post.getComments().stream().map(
                        comment -> new CommentDto(comment.getId(), comment.getText(), new UserResponseDto(comment.getUser().getFirstName(), comment.getUser().getLastName(), comment.getUser().getEmail()))).toList())
        ).toList();
    }

    public PostDto findPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("ticket with id: " + id + " cannot be retrieved");
        }
        return new PostDto(post.get().getId(), post.get().getText(), post.get().getPostDate(), post.get().getLikeFromUsers().size(),
                post.get().getComments().stream().map(
                        comment -> new CommentDto(comment.getId(), comment.getText(), new UserResponseDto(comment.getUser().getFirstName(), comment.getUser().getLastName(), comment.getUser().getEmail()))).toList());
    }

    public void updatePostValue(Long id, MultipartFile postResource, String text) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) auth.getPrincipal();

        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("ticket with id: " + id + " cannot be retrieved");
        }

        if (!post.get().getCreator().getId().equals(userEntity.getId())) {
            throw new NotAllowedException();
        }
        post.get().setText(text);
        File file = new File(new ClassPathResource("").getFile() + File.separator + "data");
        file.mkdir();
        String fileName = System.currentTimeMillis() + postResource.getOriginalFilename();
        File destOfFile = new File(file.getPath() + File.separator + fileName);
        postResource.transferTo(destOfFile);
        post.get().setPostPath(destOfFile.getPath());
        postRepository.save(
                post.get()
        );
    }

    public void deletePost(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) auth.getPrincipal();
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty() && !post.get().getCreator().getId().equals(userEntity.getId())) {
            throw new NotAllowedException();
        }
        postRepository.deleteById(id);
    }

    public ResponseEntity<InputStreamResource> findPostFileById(Long id) throws FileNotFoundException {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("ticket with id: " + id + " cannot be retrieved");
        }
        File file = new File(post.get().getPostPath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public void addLike(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) auth.getPrincipal();
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("the post with id: " + id + " cannot be retrieved");
        }
        if (!post.get().getLikeFromUsers().contains(userEntity)) {
            post.get().getLikeFromUsers().add(userEntity);
        }
        postRepository.save(post.get());
    }

    public void removeLike(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) auth.getPrincipal();
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("the post with id: " + id + " cannot be retrieved");
        }
        post.get().getLikeFromUsers().remove(userEntity);
        postRepository.save(post.get());
    }

    public void addComment(Long id, CommentDto commentDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) auth.getPrincipal();
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("the post with id: " + id + " cannot be retrieved");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setUser(userEntity);
        comment = commentRepository.save(comment);
        post.get().getComments().add(comment);
        postRepository.save(post.get());
    }
}


