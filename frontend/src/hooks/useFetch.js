import { useCallback, useEffect, useRef, useState } from "react";

/**
 * "마운트 시 API 호출 → loading/data/error 관리"가 페이지마다 반복돼서 훅으로 뺐다.
 * fetcher는 useCallback으로 감싸서 넘겨야 무한 재요청을 피할 수 있다(의존성 배열에 그대로 들어감).
 *
 *   const { data, loading, error, refetch } = useFetch(
 *     useCallback(() => getDashboard(), [])
 *   );
 *
 * requestIdRef: 필터가 바뀌는 페이지(예: 일기 목록의 동물 필터)에서 새 요청이 나간 뒤에도
 * 이전 요청이 나중에 응답할 수 있다 — 그 "늦게 도착한 낡은 응답"이 최신 상태를 덮어쓰지
 * 않도록, 응답을 반영하기 직전에 "내가 아직 가장 최근 요청인가"를 확인한다.
 */
export default function useFetch(fetcher, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const requestIdRef = useRef(0);

  const load = useCallback(async () => {
    const requestId = ++requestIdRef.current;
    setLoading(true);
    setError(null);
    try {
      const result = await fetcher();
      if (requestId === requestIdRef.current) setData(result);
    } catch (err) {
      if (requestId === requestIdRef.current) setError(err);
    } finally {
      if (requestId === requestIdRef.current) setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  useEffect(() => {
    load();
  }, [load]);

  return { data, loading, error, refetch: load };
}
