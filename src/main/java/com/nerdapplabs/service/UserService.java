package com.nerdapplabs.service;

import org.springframework.validation.Errors;

import com.nerdapplabs.model.*;

public interface UserService {
        
	    void save(RegisterUser registerUser);
        void delete(String email);
       // RegisterUser findByEmail(String email);
		void validateLogin(RegisterUser registerUser, Errors error);
		void validateRegister(RegisterUser registerUser, Errors error);
}
