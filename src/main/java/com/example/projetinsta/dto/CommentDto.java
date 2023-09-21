package com.example.projetinsta.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class CommentDto {
    @Id
    private Long id;
    private String text;
    private UserResponseDto userDto;
}
