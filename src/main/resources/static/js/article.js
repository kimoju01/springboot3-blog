// 글 삭제 - 세션 기반 요청 (폼 로그인)
// const deleteButton = document.getElementById('delete-btn');
//
// if (deleteButton) { // delete-btn 요소가 존재하는 경우에만 이벤트 리스너 작동
//     deleteButton.addEventListener('click', event => {
//         let id = document.getElementById('article-id').value;
//         fetch(`/api/articles/${id}`, {  // fetch 함수로 서버에 DELETE 요청 보냄. ${id}는 let id로 저장한 값. 동적으로 URL 생성
//             method: 'DELETE'
//         })
//             .then(() => {   // fetch()가 잘 완료되면 then()이 연이어 실행. DELETE 요청이 성공적으로 처리되면 콜백 함수 실행
//                 alert('삭제가 완료되었습니다.');
//                 location.replace('/articles');  // /articles로 리디렉션
//             });
//     });
// }

// 글 삭제 - 토큰 기반 요청 (OAuth2 로그인)
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        function success() {
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        }
        function fail() {
            alert('삭제에 실패했습니다.');
            location.replace('/articles');
        }
        httpRequest('DELETE', '/api/articles/' + id, null, success, fail);
    });
}

// 글 수정 - 세션 기반 요청 (폼 로그인)
// const modifyButton = document.getElementById('modify-btn');
//
// if (modifyButton) {
//     modifyButton.addEventListener('click', event => {
//         let params = new URLSearchParams(location.search);  // URLSearchParamas 객체를 통해 현재 URL의 쿼리 문자열을 파싱하고 쿼리 매개변수 추출
//         let id = params.get('id');
//
//         fetch(`/api/articles/${id}`, {
//             method: 'PUT',
//             headers: {
//                 "Content-Type": "application/json", // 데이터를 JSON 형식으로 지정
//             },
//             body: JSON.stringify({  // PUT 요청 body에 보낼 데이터를 JSON 형식으로 문자열화해 설정
//                 title: document.getElementById('title').value,
//                 content: document.getElementById('content').value
//             })
//         })
//             .then(() => {
//                 alert('수정이 완료되었습니다.');
//                 location.replace(`/articles/${id}`);
//             });
//     });
// }

// 글 수정 - 토큰 기반 요청 (OAuth2 로그인)
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        });
        function success() {
            alert('수정이 완료되었습니다.');
            location.replace('/articles/' + id);
        }
        function fail() {
            alert('수정에 실패했습니다.');
            location.replace('/articles/' + id);
        }
        httpRequest('PUT', '/api/articles/' + id, body, success, fail);
    });
}

// 글 등록 - 세션 기반 요청 (폼 로그인)
// const createButton = document.getElementById('create-btn');
// if (createButton) {
//     createButton.addEventListener('click', event => {
//         let id = document.getElementById('article-id').value;
//         fetch(`/api/articles`, {
//             method: 'POST',
//             headers: {
//                 "Content-Type": "application/json",
//             },
//             body: JSON.stringify({
//                 title: document.getElementById('title').value,
//                 content: document.getElementById('content').value
//             })
//         })
//             .then(() => {
//                 alert('등록이 완료되었습니다.');
//                 location.replace(`/articles`);
//             });
//     });
// }

// 글 등록 - 토큰 기반 요청 (OAuth2 로그인)
const createButton = document.getElementById('create-btn');

if (createButton) {
    // 등록 버튼을 클릭하면 /api/articles로 요청 보냄
    createButton.addEventListener('click', event => {
        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        });
        function success() {
            alert('등록이 완료되었습니다.');
            location.replace('/articles');
        }
        function fail() {
            alert('등록에 실패했습니다.');
            location.replace('/articles');
        }
        httpRequest('POST', '/api/articles', body, success, fail);
    });
}

// 특정 키에 해당하는 쿠키 가져오는 함수
function getCookie(key) {
    var result = null;
    // ;을 기준으로 분리해 배열로 저장
    var cookie = document.cookie.split(";");
    // 배열 순회하면서 쿠키 찾음
    cookie.some(function (item) {
        item = item.replace(" ", "");   // 쿠키 문자열에서 공백 제거

        var dic = item.split("=");  // 쿠키 문자열을 = 기준으로 분리해 key와 value로 나눔

        if (key === dic[0]) {   // 현재 순회 중인 쿠키의 key가 매개변수로 받은 key와 동일한지 확인
            result = dic[1];    // 일치하면 해당 쿠키의 value를 결과값으로 설정하고 순회 중단
            return true;
        }
    });

    return result;
}
// HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {
    // fetch 함수로 비동기적으로 HTTP 요청 보냄
    fetch(url, {
        method: method, // HTTP 메서드 설정
        headers: {  // 요청 헤더 설정
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),    // 로컬 스토리지에서 액세스 토큰을 가져와 Authorization 헤더에 추가
            'Content-Type': 'application/json', // 요청 본문의 데이터 타입을 JSON으로 지정
        },
        body: body, // 요청 본문 설정
    }).then(response => {   // fetch의 응답 처리하는 Promise 객체의 then 메서드 호출
        if (response.status === 200 || response.status === 201) {   // 응답이 200이나 201이면 성공 콜백 함수 호출
            return success();
        }
        // 쿠키에서 리프레시 토큰 가져옴
        const refresh_token = getCookie('refresh_token');
        // POST 요청을 보낼 때 액세스 토큰도 함께 보냄.
        // 만약 응답에 권한이 없다는 에러가 발생하면 리프레시 토큰과 함께 새로운 액세스 토큰 요청, 전달받은 액세스 토큰으로 다시 API 요청
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),    // 액세스 토큰을 Authorization 헤더에 추가
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),   // 쿠키에서 가져온 리프레시 토큰을 요청 본문에 추가
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then(result => { // 새로운 액세스 토큰 발급 성공 시 로컬 스토리지값의 액세스 토큰 값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem('access_token', result.accessToken);
                    // 초기에 호출한 httpRequest 함수를 재귀적으로 호출해 다시 API 요청 수행
                    httpRequest(method, url, body, success, fail);
                })
                .catch(error => fail());    // 새로운 액세스 토큰 발급 중 에러 발생 시 실패 콜백 함수 호출
        } else {
            return fail();
        }
    });
}