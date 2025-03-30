package com.toan.identity_service.mapper;

import com.toan.identity_service.dto.request.UserCreationRequest;
import com.toan.identity_service.dto.request.UserUpdateRequest;
import com.toan.identity_service.dto.response.UserResponse;
import com.toan.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}
