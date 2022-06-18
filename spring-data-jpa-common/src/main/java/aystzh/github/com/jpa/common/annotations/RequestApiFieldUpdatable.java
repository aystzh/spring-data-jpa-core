package aystzh.github.com.jpa.common.annotations;

import java.lang.annotation.*;

/**
 * 声明API Bean的属性可用于更新
 * Created by zhanghuan on 2022/5/9.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestApiFieldUpdatable {
}
