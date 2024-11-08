package com.fiiiiive.zippop.comment.service;

import com.fiiiiive.zippop.comment.model.entity.Comment;
import com.fiiiiive.zippop.comment.model.entity.CommentLike;
import com.fiiiiive.zippop.comment.model.dto.CreateCommentReq;
import com.fiiiiive.zippop.comment.model.dto.UpdateCommentReq;
import com.fiiiiive.zippop.comment.model.dto.CreateCommentRes;
import com.fiiiiive.zippop.comment.model.dto.GetCommentRes;
import com.fiiiiive.zippop.comment.model.dto.UpdateCommentRes;
import com.fiiiiive.zippop.comment.repository.CommentLikeRepository;
import com.fiiiiive.zippop.comment.repository.CommentRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.post.repository.PostRepository;
import com.fiiiiive.zippop.post.model.entity.Post;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CustomerRepository customerRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CreateCommentRes register(CustomUserDetails customUserDetails, Long postIdx, CreateCommentReq dto) throws BaseException {
        Post post = postRepository.findById(postIdx)
        .orElseThrow(() ->  new BaseException(BaseResponseMessage.COMMENT_REGISTER_FAIL_INVALID_MEMBER));
        Customer customer = customerRepository.findById(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.COMMENT_REGISTER_FAIL_POST_NOT_FOUND));
        Comment comment = Comment.builder()
                .post(post)
                .customerEmail(customUserDetails.getEmail())
                .content(dto.getContent())
                .likeCount(0)
                .customer(customer)
                .build();
        commentRepository.save(comment);
        return CreateCommentRes.builder()
                .commentIdx(comment.getIdx())
                .customerEmail(customUserDetails.getEmail())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public Page<GetCommentRes> searchAll(int page, int size, Long postIdx) throws BaseException {
        Page<Comment> result = commentRepository.findByPostIdx(postIdx, PageRequest.of(page, size))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.COMMENT_SEARCH_ALL_FAIL));
        Page<GetCommentRes> getCommentResPage = result.map(comment-> GetCommentRes.builder()
                .commentIdx(comment.getIdx())
                .customerEmail(comment.getCustomer().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build()
        );
        return getCommentResPage;
    }

    public Page<GetCommentRes> searchCustomer(int page, int size, CustomUserDetails customUserDetails) throws BaseException {
        Page<Comment> result = commentRepository.findByCustomerEmail(customUserDetails.getEmail(), PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.COMMENT_SEARCH_BY_CUSTOMER_FAIL));
        Page<GetCommentRes> getCommentResPage = result.map(comment-> GetCommentRes.builder()
                .commentIdx(comment.getIdx())
                .customerEmail(comment.getCustomer().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build()
        );
        return getCommentResPage;
    }

    public UpdateCommentRes update(CustomUserDetails customUserDetails, Long commentIdx, UpdateCommentReq dto) throws BaseException {
        Comment comment = commentRepository.findByCommentIdx(commentIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.COMMENT_UPDATE_FAIL_COMMENT_NOT_FOUND));
        if(!Objects.equals(comment.getCustomerEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.COMMENT_UPDATE_FAIL_INVALID_MEMBER);
        }
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
        return UpdateCommentRes.builder()
                .commentIdx(comment.getIdx())
                .customerEmail(customUserDetails.getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public void delete(CustomUserDetails customUserDetails, Long commentIdx) throws BaseException{
        Comment comment = commentRepository.findByCommentIdx(commentIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.COMMENT_DELETE_FAIL_COMMENT_NOT_FOUND));
        if(!Objects.equals(comment.getCustomerEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.COMMENT_DELETE_FAIL_INVALID_MEMBER);
        }
        commentRepository.deleteById(commentIdx);
    }

    @Transactional
    public void like(CustomUserDetails customUserDetails, Long commentIdx) throws BaseException{
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.COMMENT_LIKE_FAIL_INVALID_MEMBER));
        Comment comment = commentRepository.findByCommentIdx(commentIdx)
        .orElseThrow(()-> new BaseException(BaseResponseMessage.COMMENT_LIKE_FAIL_COMMENT_NOT_FOUND));
        Optional<CommentLike> result = commentLikeRepository.findByCustomerEmailAndCommentIdx(customUserDetails.getEmail(), commentIdx);
        if(result.isEmpty()){
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentRepository.save(comment);
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .customer(customer)
                    .build();
            commentLikeRepository.save(commentLike);
        } else {
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentRepository.save(comment);
            commentLikeRepository.deleteByCustomerEmailAndCommentIdx(customer.getEmail(), commentIdx);
        }
    }
}
