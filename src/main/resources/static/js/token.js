// 현재 페이지 URL에서 'token' 키에 해당하는 값 가져옴
const token = searchParam('token')

// 파라미터로 받은 토큰이 있다면 토큰을 로컬 스토리지에 저장
if (token) {
    localStorage.setItem("access_token", token)
}

// 현재 페이지 URL의 쿼리 스트링에 지정된 키에 해당하는 값 반환
function searchParam(key) {
    return new URLSearchParams(location.search).get(key);
}