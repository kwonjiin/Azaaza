import { useCallback, useEffect, useState } from "react";

/**
 * "마운트 시 API 호출 → loading/data/error 관리"가 페이지마다 반복돼서 훅으로 뺐다.
 * fetcher는 useCallback으로 감싸서 넘겨야 무한 재요청을 피할 수 있다(의존성 배열에 그대로 들어감).
 *
 *   const { data, loading, error, refetch } = useFetch(
 *     useCallback(() => getDashboard(), [])
 *   );
 */
export default function useFetch(fetcher, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetcher();
      setData(result);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  useEffect(() => {
    load();
  }, [load]);

  return { data, loading, error, refetch: load };
}
