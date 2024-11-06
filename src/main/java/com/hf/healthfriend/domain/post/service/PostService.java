package com.hf.healthfriend.domain.post.service;

import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import com.hf.healthfriend.domain.like.repository.LikeRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.dto.response.PostGetResponse;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.global.exception.CustomException;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.global.exception.ErrorCode;
import com.hf.healthfriend.global.util.file.FileUrlResolver;
import com.hf.healthfriend.global.util.file.MultipartFileUploader;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final RedissonClient redissonClient;

    private final FileUrlResolver fileUrlResolver;
    private final MultipartFileUploader multipartFileUploader;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Long save(PostWriteRequest postWriteRequest, MultipartFile imageFile) throws IOException {
        Long memberId = postWriteRequest.getWriterId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        String imagePath = null;
        if(imageFile != null && !imageFile.isEmpty()) {
            log.info("filePath={}",imageFile.getOriginalFilename());
            imagePath = storeProfileImage(imageFile);
        }
        Post post = postWriteRequest.toEntity(member,fileUrlResolver.resolveFileUrl(imagePath));
        return postRepository.save(post).getPostId();
    }

    public Long update(PostWriteRequest postWriteRequest, Long postId){
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        post.update(postWriteRequest.getTitle(), postWriteRequest.getContent(), PostCategory.valueOf(postWriteRequest.getCategory()));
        return post.getPostId();
    }

    public PostGetResponse get(Long postId, boolean canUpdateViewCount, CommentSortType sortType) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        if(canUpdateViewCount) {
            post.updateViewCount(post.getViewCount());
        }
        List<CommentDto> commentList = commentService.getCommentsOfPost(postId,sortType);
        String imagePath = fileUrlResolver.resolveFileUrl(post.getImagePath());
        return PostGetResponse.of(post, commentList,imagePath);
    }

    public void delete(Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        post.delete();
        likeRepository.deleteLikeByPostId(postId);
    }

    public List<PostListObject> getList(int pageNumber, int size,FitnessLevel fitnessLevel, PostCategory postCategory, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        return postRepository.getList(fitnessLevel, postCategory, keyword, pageable);
    }

    public List<PostListObject> getPopularList(int pageNumber, int size,FitnessLevel fitnessLevel, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet("popular_posts");
        List<Long> postIdList = new ArrayList<>( sortedSet.readAll().stream().toList());
        return postRepository.getPopularList(postIdList,fitnessLevel,keyword,pageable);
    }

    private String storeProfileImage(MultipartFile profileImage) {
        String originalFilename = profileImage.getOriginalFilename();
        String filePath = this.fileUrlResolver.generateFilePath(originalFilename, "image");
        try {
            multipartFileUploader.uploadFile(filePath, profileImage);
            return filePath;
        } catch (IOException e) {
            log.error("[FATAL] 파일 출력 중 Error 발생", e);
            return null;
        }
    }


}


