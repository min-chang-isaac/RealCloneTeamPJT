package com.example.join.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.join.entity.Post;
import com.example.join.entity.Post.Comment;
import com.example.join.entity.Like;
import com.example.join.repository.LikeRepository;
import java.time.LocalDateTime;

@Controller
public class PostController {
    
    // ✅ 추가: LikeRepository 주입
    private final LikeRepository likeRepository;
    
    private Post post = new Post();
    private Long commentIdCounter = 1L;
    
    // ✅ 수정: 생성자에 LikeRepository 추가
    public PostController(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
        
        post.setId(1L);
        post.setContent("첫 게시글");
        
        Comment sampleComment = new Comment(1L, "첫 댓글입니다!", "유저1");
        sampleComment.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        post.addComment(sampleComment);
        commentIdCounter = 2L;
    }

    @GetMapping("/post")
    public String post(Model model) {
        // ✅ 추가: 게시글 좋아요 정보 로드
        String currentUser = "user1"; // 임시 사용자 ID
        
        // 게시글 좋아요
        post.setLikeCount((int) likeRepository.countByTargetIdAndTargetType(post.getId(), "POST"));
        post.setLikedByMe(likeRepository.findByTargetIdAndTargetTypeAndUserId(
            post.getId(), "POST", currentUser).isPresent());
        
        // ✅ 추가: 각 댓글/대댓글의 좋아요 정보 로드
        for (Comment comment : post.getComments()) {
            comment.setLikeCount((int) likeRepository.countByTargetIdAndTargetType(
                comment.getId(), "COMMENT"));
            comment.setLikedByMe(likeRepository.findByTargetIdAndTargetTypeAndUserId(
                comment.getId(), "COMMENT", currentUser).isPresent());
            
            for (Comment reply : comment.getReplies()) {
                reply.setLikeCount((int) likeRepository.countByTargetIdAndTargetType(
                    reply.getId(), "REPLY"));
                reply.setLikedByMe(likeRepository.findByTargetIdAndTargetTypeAndUserId(
                    reply.getId(), "REPLY", currentUser).isPresent());
            }
        }
        
        model.addAttribute("post", post);
        model.addAttribute("commentCount", post.getComments().size());
        return "post";
    }
    
    // ✅ 수정: 게시글 좋아요 (Like 테이블 사용)
    @PostMapping("/post/like")
    public String toggleLike() {
        String currentUser = "user1";
        var existingLike = likeRepository.findByTargetIdAndTargetTypeAndUserId(
            post.getId(), "POST", currentUser);
        
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            likeRepository.save(new Like(post.getId(), "POST", currentUser));
        }
        
        return "redirect:/post";
    }
    
    @PostMapping("/post/comment/add")
    public String addComment(@RequestParam String content) {
        if (content != null && !content.trim().isEmpty()) {
            Comment newComment = new Comment(commentIdCounter++, content, "익명");
            newComment.setCreatedAt(LocalDateTime.now());
            post.addComment(newComment);
        }
        return "redirect:/post";
    }
    
    @PostMapping("/post/comment/delete")
    public String deleteComment(@RequestParam Long commentId) {
        post.removeComment(commentId);
        
        // ✅ 추가: 댓글의 좋아요도 함께 삭제
        likeRepository.findByTargetIdAndTargetType(commentId, "COMMENT")
            .forEach(likeRepository::delete);
        
        return "redirect:/post";
    }
    
    @PostMapping("/post/comment/edit")
    public String editComment(
            @RequestParam Long commentId,
            @RequestParam String content) {
        Comment comment = post.findCommentById(commentId);
        if (comment != null && content != null && !content.trim().isEmpty()) {
            comment.setContent(content);
        }
        return "redirect:/post";
    }
    
    // ✅ 수정: 댓글 좋아요 (Like 테이블 사용)
    @PostMapping("/post/comment/like")
    public String toggleCommentLike(@RequestParam Long commentId) {
        String currentUser = "user1";
        var existingLike = likeRepository.findByTargetIdAndTargetTypeAndUserId(
            commentId, "COMMENT", currentUser);
        
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            likeRepository.save(new Like(commentId, "COMMENT", currentUser));
        }
        
        return "redirect:/post";
    }
    
    @PostMapping("/post/comment/reply")
    public String addReply(
            @RequestParam Long parentId,
            @RequestParam String content) {
        Comment parent = post.findCommentById(parentId);
        if (parent != null && content != null && !content.trim().isEmpty()) {
            Comment reply = new Comment(commentIdCounter++, content, "익명");
            reply.setCreatedAt(LocalDateTime.now());
            parent.getReplies().add(reply);
        }
        return "redirect:/post";
    }
    
    // ✅ 수정: 대댓글 좋아요 (Like 테이블 사용)
    @PostMapping("/post/comment/reply/like")
    public String toggleReplyLike(
            @RequestParam Long parentId,
            @RequestParam Long replyId) {
        
        String currentUser = "user1";
        var existingLike = likeRepository.findByTargetIdAndTargetTypeAndUserId(
            replyId, "REPLY", currentUser);
        
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            likeRepository.save(new Like(replyId, "REPLY", currentUser));
        }
        
        return "redirect:/post";
    }
    
    @PostMapping("/post/comment/reply/edit")
    public String editReply(
            @RequestParam Long parentId,
            @RequestParam Long replyId,
            @RequestParam String content) {
        
        Comment parent = post.findCommentById(parentId);
        if (parent != null) {
            Comment reply = parent.getReplies().stream()
                .filter(r -> r.getId().equals(replyId))
                .findFirst()
                .orElse(null);
            
            if (reply != null && content != null && !content.trim().isEmpty()) {
                reply.setContent(content);
            }
        }
        
        return "redirect:/post";
    }
    
    @PostMapping("/post/comment/reply/delete")
    public String deleteReply(
            @RequestParam Long parentId,
            @RequestParam Long replyId) {
        
        Comment parent = post.findCommentById(parentId);
        if (parent != null) {
            parent.getReplies().removeIf(r -> r.getId().equals(replyId));
            
            // ✅ 추가: 대댓글의 좋아요도 함께 삭제
            likeRepository.findByTargetIdAndTargetType(replyId, "REPLY")
                .forEach(likeRepository::delete);
        }
        
        return "redirect:/post";
    }
}