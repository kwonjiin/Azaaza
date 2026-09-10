/** 로컬 타임존 기준 오늘 날짜를 yyyy-MM-dd로. Date#toISOString()은 UTC라 그대로 쓰면
 *  자정 근처에 날짜가 하루 밀릴 수 있어서 타임존 오프셋을 보정한다. */
export function todayISO() {
  const now = new Date();
  const local = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 10);
}
