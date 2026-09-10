import { useCallback, useState } from "react";

/**
 * "제출 중 상태 + 에러 + try/catch/finally"가 로그인, 회원가입, 동물/습관 생성·수정,
 * 습관 기록, 일기 생성, 습관 삭제까지 컴포넌트마다 손으로 반복돼 있었다(코드리뷰 지적) —
 * 그중 하나(습관 삭제)는 이미 에러를 그냥 삼키는 쪽으로 구현이 갈라져 있었을 정도라 훅으로 뺐다.
 *
 * 성공/실패 이후에 뭘 할지(리다이렉트, 폼 리셋, refetch)는 호출부마다 다르므로 여기서
 * 관여하지 않는다 — submit()은 실패 시 에러를 상태에 담아두는 동시에 다시 던져서,
 * 호출부가 "성공했을 때만 하는 일"을 await 뒤에 안전하게 이어갈 수 있게 한다.
 *
 *   const { submit, submitting, error } = useSubmit(login);
 *   const handleSubmit = async (e) => {
 *     e.preventDefault();
 *     try {
 *       await submit(form);
 *       navigate("/dashboard");
 *     } catch {
 *       // error는 이미 훅이 들고 있음 — 여기선 성공 시 후속 동작만 신경 쓰면 됨
 *     }
 *   };
 */
export default function useSubmit(action) {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const submit = useCallback(
    async (...args) => {
      setSubmitting(true);
      setError(null);
      try {
        return await action(...args);
      } catch (err) {
        setError(err);
        throw err;
      } finally {
        setSubmitting(false);
      }
    },
    [action]
  );

  return { submit, submitting, error, setError };
}
