package com.learn.reditt.service;

import com.learn.reditt.dto.CommentsDto;
import com.learn.reditt.exceptions.PostNotFoundException;
import com.learn.reditt.model.Comment;
import com.learn.reditt.model.NotificationEmail;
import com.learn.reditt.model.Post;
import com.learn.reditt.model.User;
import com.learn.reditt.repository.CommentRepository;
import com.learn.reditt.repository.PostRepository;
import com.learn.reditt.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL = "";
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void save(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        Comment comment = mapToComment(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " post a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }


    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById((postId))
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        return commentRepository.findByPost(post)
                .stream()
                .map(comment -> mapToDto(comment))
                .collect(Collectors.toList());
    }


    private Comment mapToComment(CommentsDto commentsDto, Post post, User user) {
        Comment comment = new Comment();

        Post post1 = postRepository.getById(commentsDto.getPostId());
        comment.setPost(post1);
        comment.setCreatedDate(Instant.now());
        comment.setText(commentsDto.getText());
        comment.setUser(user);

        return comment;
    }

    private CommentsDto mapToDto(Comment comment) {
        CommentsDto commentsDto = new CommentsDto();

        commentsDto.setId(comment.getId());
        commentsDto.setPostId(comment.getPost().getPostId());
        commentsDto.setCreatedDate(comment.getCreatedDate());
        commentsDto.setText(comment.getText());
        commentsDto.setUserName(comment.getUser().getUsername());

        return commentsDto;
    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));

        return commentRepository.findAllByUser(user)
                .stream()
                .map(comment -> mapToDto(comment))
                .collect(Collectors.toList());
    }
}
