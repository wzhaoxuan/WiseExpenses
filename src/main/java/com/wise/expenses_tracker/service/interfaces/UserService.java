package com.wise.expenses_tracker.service.interfaces;

import com.wise.expenses_tracker.model.UserEntity;
import com.wise.expenses_tracker.transferObject.UserDTO;

public interface UserService {

    UserEntity getCurrentUser();
    UserDTO getCurrentUserDTO();
     

}
