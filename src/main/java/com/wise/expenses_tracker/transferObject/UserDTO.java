package com.wise.expenses_tracker.transferObject;
import com.wise.expenses_tracker.security.user.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    String username;
    Role role;

}
