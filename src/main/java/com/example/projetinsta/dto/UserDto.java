package com.example.projetinsta.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class UserDto {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

}

