package com.codingshuttle.projects.Loveable_clone.entity;

import com.codingshuttle.projects.Loveable_clone.enums.PreviewStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {
    Long id;

    Project project;

    String nameSpace;
    String podName;
    String previewUrl;

    PreviewStatus status;

    Instant startedAt;
    Instant createdAt;
    Instant terminatedAt;
}
