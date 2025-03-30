package com.toan.identity_service.Service;


import com.toan.identity_service.dto.request.RoleRequest;
import com.toan.identity_service.dto.response.RoleResponse;
import com.toan.identity_service.mapper.RoleMapper;
import com.toan.identity_service.repository.PermissionRepository;
import com.toan.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository
                .findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public void deleted(String role) {
        roleRepository.deleteById(role);
    }

}
