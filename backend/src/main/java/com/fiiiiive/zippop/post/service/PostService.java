package com.fiiiiive.zippop.post.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.post.model.dto.*;
import com.fiiiiive.zippop.post.model.entity.Post;
import com.fiiiiive.zippop.post.model.entity.PostImage;
import com.fiiiiive.zippop.post.model.entity.PostLike;
import com.fiiiiive.zippop.post.repository.PostImageRepository;
import com.fiiiiive.zippop.post.repository.PostLikeRepository;
import com.fiiiiive.zippop.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CustomerRepository customerRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public CreatePostRes register(CustomUserDetails customUserDetails, List<String> fileNames, CreatePostReq dto) throws BaseException {
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_REGISTER_FAIL_INVALID_MEMBER));
        Post post = Post.builder()
                .title(dto.getPostTitle())
                .content(dto.getPostContent())
                .likeCount(0)
                .customerEmail(customUserDetails.getEmail())
                .customer(customer)
                .build();
        postRepository.save(post);
        for(String fileName : fileNames){
            PostImage postImage = PostImage.builder()
                    .url(fileName)
                    .post(post)
                    .build();
            postImageRepository.save(postImage);
        }
        return CreatePostRes.builder().postIdx(post.getIdx()).build();
    }

    public SearchPostRes search(Long postIdx) throws BaseException {
        Post post = postRepository.findByPostIdx(postIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_SEARCH_BY_IDX_FAIL));
        List<SearchPostImageRes> searchPostImageResList = new ArrayList<>();
        for(PostImage postImage: post.getPostImageList()) {
            SearchPostImageRes searchPostImageRes = SearchPostImageRes.builder()
                    .postImageIdx(postImage.getIdx())
                    .imageUrl(postImage.getUrl())
                    .createdAt(postImage.getCreatedAt())
                    .updatedAt(postImage.getUpdatedAt())
                    .build();
            searchPostImageResList.add(searchPostImageRes);
        }
        return SearchPostRes.builder()
                .postIdx(post.getIdx())
                .customerEmail(post.getCustomerEmail())
                .postTitle(post.getTitle())
                .postContent(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentList().size())
                .searchPostImageResList(searchPostImageResList)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public Page<SearchPostRes> searchAllAsGuest(String keyword, int page, int size) throws BaseException {
        Page<Post> result = null;
        if(keyword != null){
            result = postRepository.findAllByKeyword(keyword, PageRequest.of(page, size))
            .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_SEARCH_ALL_FAIL));
        } else {
            result = postRepository.findAllPageable(PageRequest.of(page, size))
            .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_SEARCH_ALL_FAIL));
        }
        Page<SearchPostRes> getPostResPage = result.map(post-> {
        List<SearchPostImageRes> searchPostImageResList = new ArrayList<>();
        for (PostImage postImage : post.getPostImageList()) {
            SearchPostImageRes searchPostImageRes = SearchPostImageRes.builder()
                    .postImageIdx(postImage.getIdx())
                    .imageUrl(postImage.getUrl())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
            searchPostImageResList.add(searchPostImageRes);
        }
        return SearchPostRes.builder()
                .postIdx(post.getIdx())
                .customerEmail(post.getCustomer().getEmail())
                .postTitle(post.getTitle())
                .postContent(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentList().size())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .searchPostImageResList(searchPostImageResList)
                .build();
        });
        return getPostResPage;
    }

    public Page<SearchPostRes> searchAllAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        Page<Post> result = postRepository.findByCustomerEmail(customUserDetails.getEmail(), PageRequest.of(page, size))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_SEARCH_BY_CUSTOMER_FAIL));
        Page<SearchPostRes> getPostResPage = result.map(post-> {
            List<PostImage> postImages = post.getPostImageList();
            List<SearchPostImageRes> searchPostImageResList = new ArrayList<>();
            for (PostImage postImage : postImages) {
                SearchPostImageRes searchPostImageRes = SearchPostImageRes.builder()
                        .postImageIdx(postImage.getIdx())
                        .imageUrl(postImage.getUrl())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .build();
                searchPostImageResList.add(searchPostImageRes);
            }
            return SearchPostRes.builder()
                    .postIdx(post.getIdx())
                    .postTitle(post.getTitle())
                    .postContent(post.getContent())
                    .likeCount(post.getLikeCount())
                    .searchPostImageResList(searchPostImageResList)
                    .customerEmail(post.getCustomer().getEmail())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        });
        return getPostResPage;
    }

    @Transactional
    public void like(CustomUserDetails customUserDetails, Long postIdx) throws BaseException{
        Post post = postRepository.findByPostIdx(postIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_LIKE_FAIL_POST_NOT_FOUND));
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_LIKE_FAIL_INVALID_MEMBER));
        Optional<PostLike> result = postLikeRepository.findByCustomerIdxAndPostIdx(customUserDetails.getIdx(), postIdx);
        if (result.isEmpty()) {
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .customer(customer)
                    .build();
            postLikeRepository.save(postLike);
        } else {
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            postLikeRepository.deleteByCustomerIdxAndPostIdx(customer.getIdx(), postIdx);
        }
    }

    @Transactional
    public UpdatePostRes update(CustomUserDetails customUserDetails, Long postIdx, UpdatePostReq dto, List<String> fileNames) throws BaseException {
        Post post = postRepository.findByPostIdx(postIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_UPDATE_FAIL_NOT_FOUND_POST));
        if(!Objects.equals(post.getCustomerEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.POST_UPDATE_FAIL_INVALID_MEMBER);
        }
        post.setTitle(dto.getPostTitle());
        post.setContent(dto.getPostContent());
        postRepository.save(post);

        // PostImage 업데이트
        // 기존 이미지 URL 목록 불러오기
        List<String> existingUrls = new ArrayList<>();
        for (PostImage postImage : post.getPostImageList()) {
            existingUrls.add(postImage.getUrl());
        }

        // 삭제할 이미지 URL (기존에 있었으나 새 목록에 없는 것들)
        for (PostImage postImage : post.getPostImageList()) {
            if (!fileNames.contains(postImage.getUrl())) {
                postImageRepository.deleteById(postImage.getIdx());
            }
        }

        // 추가할 이미지 URL (새 목록에 있는데 기존 목록에는 없는 것들)
        for (String fileName : fileNames) {
            if (!existingUrls.contains(fileName)) {
                PostImage postImage = PostImage.builder()
                        .url(fileName)
                        .post(post)
                        .build();
                postImageRepository.save(postImage);
            }
        }
        // UpdatePostRes 반환
        return UpdatePostRes.builder().postIdx(post.getIdx()).build();
    }

    @Transactional
    public void delete(CustomUserDetails customUserDetails, Long postIdx) throws BaseException{
        Post post = postRepository.findByPostIdx(postIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POST_DELETE_FAIL_POST_NOT_FOUND));
        if(!Objects.equals(post.getCustomerEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.POST_DELETE_FAIL_INVALID_MEMBER);
        }
        postRepository.deleteById(postIdx);
    }
}
