package com.example.projetinsta.controller;

import com.example.projetinsta.dto.CommentDto;
import com.example.projetinsta.dto.PostDto;
import com.example.projetinsta.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
@CrossOrigin("*")
public class PostController {

    private PostService postService;

    @PostMapping
    public void savePost(@RequestParam MultipartFile post, String text) throws IOException {
        postService.savePost(post, text);
    }

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.findAllPosts();
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable Long id) {
        return postService.findPostById(id);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<InputStreamResource> getPostFileById(@PathVariable Long id) throws FileNotFoundException {
        return postService.findPostFileById(id);
    }

    @PutMapping("/{id}")
    public void updatePostValue(@PathVariable Long id, @RequestParam MultipartFile multipartFile, String text) throws IOException {
        postService.updatePostValue(id, multipartFile, text);
    }

    @DeleteMapping("/{id}")
    public void deletePostById(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @PutMapping("/{id}/likes/add")
    public void addLike(@PathVariable Long id) {
        postService.addLike(id);
    }

    @PutMapping("/{id}/likes/remove")
    public void removeLike(@PathVariable Long id) {
        postService.removeLike(id);
    }

    @PutMapping("/{id}/comments")
    public void addComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        postService.addComment(id, commentDto);
    }
}
