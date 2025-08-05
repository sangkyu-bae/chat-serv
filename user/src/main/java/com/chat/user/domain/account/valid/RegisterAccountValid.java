package com.chat.user.domain.account.valid;

import com.chat.user.domain.account.dto.request.RegisterAccount;
import jakarta.validation.constraints.AssertTrue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class RegisterAccountValid implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RegisterAccount.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterAccount registerAccount = (RegisterAccount) target;

        if (!registerAccount.getCheckPassword().equals(registerAccount.getPassword())) {
            errors.rejectValue("password","invalid.password");
        }

    }
}
