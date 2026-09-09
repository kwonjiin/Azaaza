package com.azaaza.habitpet.global.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 파라미터에 붙여 인증된 사용자의 id를 바로 주입받는다.
 *
 *   @GetMapping
 *   public List<HabitResponse> list(@LoginUser Long userId) { ... }
 *
 * 컨트롤러가 SecurityContextHolder를 직접 건드리지 않게 해서, "userId는 항상 토큰에서만
 * 온다"는 규칙을 컨트롤러 코드 레벨에서 강제한다(요청 바디/경로에 userId 필드를 두는 실수를 원천 차단).
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
