package com.example.mapper;

import com.example.dto.CreatePostRequest;
import com.example.dto.PostResponse;
import com.example.dto.PostSummaryResponse;
import com.example.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = {
        PostMediaMapper.class,
        PostTargetMapper.class
    }
)
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "reviewNote", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    // Media được tạo riêng bằng PostMediaMapper rồi add vào Post.
    @Mapping(target = "media", ignore = true)

    // socialAccountIds không thể tự chuyển thành PostTarget.
    @Mapping(target = "targets", ignore = true)
    Post toEntity(CreatePostRequest request);

    /*
     * Post -> PostResponse
     *
     * media và targets được MapStruct map tự động
     * thông qua PostMediaMapper và PostTargetMapper.
     */
    @Mapping(target = "userId", source = "user.id")
    PostResponse toResponse(Post post);

    /*
     * Post -> PostSummaryResponse
     *
     * Các field đếm nên lấy từ query/projection,
     * không nên gọi post.getTargets().size() trong danh sách.
     */
    @Mapping(target = "targetCount", ignore = true)
    @Mapping(target = "publishedTargetCount", ignore = true)
    @Mapping(target = "failedTargetCount", ignore = true)
    PostSummaryResponse toSummaryResponse(Post post);

    /*
     * Cập nhật Post hiện có từ request.
     *
     * Không cho request sửa id, user, status,
     * version và các quan hệ con.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "reviewNote", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "targets", ignore = true)
    void updateEntity(
        CreatePostRequest request,
        @MappingTarget Post post
    );
}
