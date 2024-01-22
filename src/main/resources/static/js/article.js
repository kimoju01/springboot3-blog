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