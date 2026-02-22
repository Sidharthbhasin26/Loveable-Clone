package com.codingshuttle.projects.Loveable_clone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     String email;
     String passwordHash;
     String name;
     String avatarUrl;

     Instant createdAt;
     Instant updatedAt;
     Instant deleteAt;
}
