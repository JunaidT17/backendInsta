package com.example.projetinsta.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Date postDate;
    private String postPath;

    @ManyToOne
    @JoinColumn(name = "creator")
    private UserEntity creator;

    @ManyToMany
    @JoinColumn(name = "likeFromUsers")
    private List<UserEntity> likeFromUsers;

    @OneToMany
    private List<Comment> comments;

}


