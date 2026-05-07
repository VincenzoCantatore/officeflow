package com.officeflow.dto.response;

import com.officeflow.domain.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRoleEnum role;
}
