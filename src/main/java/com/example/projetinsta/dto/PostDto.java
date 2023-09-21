package com.example.projetinsta.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String text;
    private Date postDate;
    private int numberOfLike;
    private List<CommentDto> commentsDto;

}
