package com.example.mapper;

import com.example.dto.PublishAttemptResponse;
import com.example.entity.PublishAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublishAttemptMapper {

    @Mapping(
        target = "postTargetId",
        source = "postTarget.id"
    )
    PublishAttemptResponse toResponse(
        PublishAttempt entity
    );
}