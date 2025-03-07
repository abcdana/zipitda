package com.danahub.zipitda.common.aop;

import java.lang.annotation.*;


// 게시글 수정/삭제 시 권한 체크를 수행하는 어노테이션
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostAuthorizationCheck {
}