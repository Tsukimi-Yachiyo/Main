package com.yachiyo.tool;


import com.github.kwfilter.util.KeyWordFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SelfValidator implements ConstraintValidator<SensitiveWordFilter, String> {

    @Autowired
    private KeyWordFilter keyWordFilter;

    @Override
    public void initialize(SensitiveWordFilter constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        String result = keyWordFilter.filter_in(s, "0", "0");
        return result.equals(s);
    }
}


