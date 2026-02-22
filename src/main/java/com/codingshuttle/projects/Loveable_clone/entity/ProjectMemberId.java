package com.codingshuttle.projects.Loveable_clone.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable

public class ProjectMemberId  {
    Long projectId;
    Long userId;
}
