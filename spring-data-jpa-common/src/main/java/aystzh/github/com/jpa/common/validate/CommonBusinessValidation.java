package aystzh.github.com.jpa.common.validate;

import aystzh.github.com.jpa.common.bean.AbstractBaseInfo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * 通用新增修改业务逻辑校验器
 * Created by zhanghuan on 2022/6/16.
 */
@Slf4j
public class CommonBusinessValidation<T extends Annotation, S extends AbstractBaseInfo, R> implements ConstraintValidator<T, S> {

    @Resource
    protected R r;

    protected Predicate<S> predicate = c -> true;

    @Override
    public boolean isValid(S value, ConstraintValidatorContext context) {
        return r == null || predicate.test(value);
    }

}

