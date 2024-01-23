// 글 삭제
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) { // delete-btn 요소가 존재하는 경우에만 이벤트 리스너 작동
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {  // fetch 함수로 서버에 DELETE 요청 보냄. ${id}는 let id로 저장한 값. 동적으로 URL 생성
            method: 'DELETE'
        })
            .then(() => {   // fetch()가 잘 완료되면 then()이 연이어 실행. DELETE 요청이 성공적으로 처리되면 콜백 함수 실행
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');  // /articles로 리디렉션
            });
    });
}

// 글 수정
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);  // URLSearchParamas 객체를 통해 현재 URL의 쿼리 문자열을 파싱하고 쿼리 매개변수 추출
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json", // 데이터를 JSON 형식으로 지정
            },
            body: JSON.stringify({  // PUT 요청 body에 보낼 데이터를 JSON 형식으로 문자열화해 설정
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
}