package com.codingshuttle.projects.Loveable_clone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

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

     String username;
     String password;
     String name;


     @CreationTimestamp
     Instant createdAt;

     @CreationTimestamp
     Instant updatedAt;

     Instant deleteAt; // soft delete
}
