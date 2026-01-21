function toggleCommentSection() {
    const section = document.getElementById('comment-section');
    section.style.display =
        section.style.display === 'none' ? 'block' : 'none';
}

/* 댓글 수정 토글 */
function toggleEdit(commentId) {
    const content = document.getElementById('comment-content-' + commentId);
    const form = document.getElementById('edit-form-' + commentId);

    const isHidden = form.classList.contains('active');

    content.style.display = isHidden ? 'block' : 'none';
    form.classList.toggle('active');
}

/* 대댓글 입력 토글 */
function toggleReply(commentId) {
    const form = document.getElementById('reply-form-' + commentId);
    form.classList.toggle('active');
}

/* 대댓글 수정 토글 */
function toggleReplyEdit(parentId, replyId) {
    const content = document.getElementById(
        `reply-content-${parentId}-${replyId}`
    );
    const form = document.getElementById(
        `reply-edit-form-${parentId}-${replyId}`
    );

    const isHidden = form.classList.contains('active');

    content.style.display = isHidden ? 'block' : 'none';
    form.classList.toggle('active');
}