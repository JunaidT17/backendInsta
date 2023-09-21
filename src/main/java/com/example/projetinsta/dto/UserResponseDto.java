package com.example.projetinsta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResponseDto {
    private final String firstName;
    private final String lastName;
    private final String email;
}
