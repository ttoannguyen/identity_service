package com.toan.identity_service.controller;

import com.toan.identity_service.Service.PermissionService;
import com.toan.identity_service.dto.request.ApiResponse;
import com.toan.identity_service.dto.request.PermissionRequest;
import com.toan.identity_service.dto.response.PermissionResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }


    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permissionId}")
    ApiResponse<Void> delete(@PathVariable("permissionId") String permissionName) {
        permissionService.delete(permissionName);
        return ApiResponse.<Void>builder().build();
    }
}
