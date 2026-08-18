package com.aromaorigem.aromaorigem.security.validation;

import com.aromaorigem.aromaorigem.util.CpfUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        return CpfUtils.isValidCpf(cpf);
    }
}