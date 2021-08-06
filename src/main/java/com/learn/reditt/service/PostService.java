package com.learn.reditt.service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.learn.reditt.dto.PostRequest;
import com.learn.reditt.dto.PostResponse;
import com.learn.reditt.exceptions.PostNotFoundException;
import com.learn.reditt.exceptions.SubredditNotFoundException;
import com.learn.reditt.model.*;
import com.learn.reditt.repository.CommentRepository;
import com.learn.reditt.repository.PostRepository;
import com.learn.reditt.repository.SubredditRepository;
import com.learn.reditt.repository.UserRepository;
import javafx.geometry.Pos;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
        postRepository.save(mapToPost(postRequest, subreddit, authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(post -> mapToDto(post))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(post -> mapToDto(post)).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(post -> mapToDto(post))
                .collect(toList());
    }

    private Post mapToPost(PostRequest postRequest, Subreddit subreddit, User currentUser) {
        Post post = Post.builder()
                .postName(postRequest.getPostName())
                .url(postRequest.getUrl())
                .description(postRequest.getDescription())
                .user(currentUser)
                .createdDate(Instant.now())
                .subreddit(subreddit)
                .build();
        return post;
    }

    private PostResponse mapToDto(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getPostId());
        postResponse.setPostName(post.getPostName());
        postResponse.setUrl(post.getUrl());
        postResponse.setDescription(post.getDescription());
        postResponse.setUserName(post.getUser().getUsername());
        postResponse.setSubredditName(post.getSubreddit().getName());
        postResponse.setVoteCount(post.getVoteCount());
        postResponse.setCommentCount(commentRepository.findByPost(post).size());
        postResponse.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
        return postResponse;
    }

//    private boolean checkVoteType(Post post, VoteType voteType) {
//        if (authService.isLoggedIn()) {
//            Optional<Vote> voteForPostByUser =
//                    voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
//                            authService.getCurrentUser());
//            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
//                    .isPresent();
//        }
//        return false;
//    }
}
