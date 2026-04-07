package com.sethy.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserInfo {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
