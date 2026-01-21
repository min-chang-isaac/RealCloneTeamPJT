package com.example.join.entity;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String content;
    
    // ❌ 삭제: private int likeCount;
    // ❌ 삭제: private boolean likedByMe;
    
    @Transient
    private List<Comment> comments = new ArrayList<>();
    
    // ✅ 추가: 좋아요 정보 (Transient)
    @Transient
    private int likeCount;
    
    @Transient
    private boolean likedByMe;
    
    public static class Comment {
        private Long id;
        private String content;
        private String author;
        private LocalDateTime createdAt;
        
        // ❌ 삭제: private int likeCount;
        // ❌ 삭제: private boolean likedByMe;
        
        private List<Comment> replies = new ArrayList<>();
        
        // ✅ 추가: 좋아요 정보 (런타임에만 사용)
        private int likeCount;
        private boolean likedByMe;
        
        public Comment(Long id, String content, String author) {
            this.id = id;
            this.content = content;
            this.author = author;
            this.createdAt = LocalDateTime.now();
        }
        
        // getter / setter (그대로 유지)
        public Long getId() { return id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public int getLikeCount() { return likeCount; }
        public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
        
        public boolean isLikedByMe() { return likedByMe; }
        public void setLikedByMe(boolean likedByMe) {
            this.likedByMe = likedByMe;
        }
        
        public String getAuthor() { return author; }
        public List<Comment> getReplies() { return replies; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { 
            this.createdAt = createdAt; 
        }
    }
    
    // Post getter / setter (그대로 유지)
    public Long getId() {
        return Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public boolean isLikedByMe() {
        return likedByMe;
    }    
    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }
    public List<Comment> getComments() {
        return comments;
    }
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
    public void removeComment(Long commentId) {
        comments.removeIf(c -> c.getId().equals(commentId));
    }
    public Comment findCommentById(Long id) {
        return comments.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}