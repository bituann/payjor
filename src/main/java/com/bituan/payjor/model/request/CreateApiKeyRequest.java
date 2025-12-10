package com.bituan.payjor.model.request;

import com.bituan.payjor.model.enums.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateApiKeyRequest {
    private String name;
    private List<Permission> permissions;
    private String expiry;
}
