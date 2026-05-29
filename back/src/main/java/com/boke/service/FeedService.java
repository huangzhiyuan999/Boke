package com.boke.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boke.dto.FeedRequest;
import com.boke.dto.FeedVO;
import com.boke.entity.*;
import com.boke.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService extends ServiceImpl<FeedItemMapper, FeedItem> {

    private final FeedImageMapper feedImageMapper;
    private final FeedLikeMapper feedLikeMapper;
    private final FeedCommentMapper feedCommentMapper;
    private final UserMapper userMapper;

    public List<FeedVO> getFeeds(Long currentUserId) {
        List<FeedItem> items = baseMapper.findAllWithAuthor();
        return items.stream()
                .map(item -> toFeedVO(item, currentUserId))
                .collect(Collectors.toList());
    }

    public List<FeedVO> getFollowingFeeds(Long userId) {
        List<FeedItem> items = baseMapper.findFollowingFeeds(userId);
        return items.stream()
                .map(item -> toFeedVO(item, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedVO createFeed(FeedRequest req, Long authorId) {
        FeedItem item = new FeedItem();
        item.setAuthorId(authorId);
        item.setContent(req.getContent());
        item.setTopic(req.getTopic());
        item.setLikeCount(0L);
        item.setCommentCount(0L);
        item.setRepostCount(0L);
        save(item);

        if (req.getImages() != null && !req.getImages().isEmpty()) {
            for (int i = 0; i < req.getImages().size(); i++) {
                FeedImage img = new FeedImage();
                img.setFeedId(item.getId());
                img.setImageUrl(req.getImages().get(i));
                img.setSortOrder(i);
                feedImageMapper.insert(img);
            }
        }

        return toFeedVO(item, authorId);
    }

    @Transactional
    public void deleteFeed(Long id, Long userId) {
        FeedItem item = getById(id);
        if (item == null) throw new RuntimeException("动态不存在");
        if (!item.getAuthorId().equals(userId)) throw new RuntimeException("无权删除");
        removeById(id);
    }

    @Transactional
    public void likeFeed(Long feedId, Long userId) {
        if (feedLikeMapper.exists(feedId, userId) > 0) return;
        FeedLike like = new FeedLike();
        like.setFeedId(feedId);
        like.setUserId(userId);
        like.setCreatedAt(LocalDateTime.now());
        feedLikeMapper.insert(like);
        baseMapper.incrementLikeCount(feedId);
    }

    @Transactional
    public void unlikeFeed(Long feedId, Long userId) {
        if (feedLikeMapper.exists(feedId, userId) == 0) return;
        feedLikeMapper.delete(feedId, userId);
        baseMapper.decrementLikeCount(feedId);
    }

    public List<FeedComment> getComments(Long feedId) {
        return feedCommentMapper.findByFeedId(feedId);
    }

    @Transactional
    public FeedComment addComment(Long feedId, Long userId, String content) {
        FeedComment comment = new FeedComment();
        comment.setFeedId(feedId);
        comment.setUserId(userId);
        comment.setContent(content);
        feedCommentMapper.insert(comment);
        baseMapper.incrementCommentCount(feedId);
        return comment;
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        FeedComment comment = feedCommentMapper.selectById(commentId);
        if (comment == null) throw new RuntimeException("评论不存在");
        if (!comment.getUserId().equals(userId)) throw new RuntimeException("无权删除");
        feedCommentMapper.deleteById(commentId);
        baseMapper.decrementCommentCount(comment.getFeedId());
    }

    private FeedVO toFeedVO(FeedItem item, Long currentUserId) {
        FeedVO vo = new FeedVO();
        vo.setId(item.getId());
        vo.setAuthorId(item.getAuthorId());
        vo.setUserId(item.getAuthorId());
        vo.setContent(item.getContent());
        vo.setTopic(item.getTopic());
        vo.setTopicCount((long) feedLikeMapper.countByUserId(item.getAuthorId()));

        List<String> imageUrls = feedImageMapper.findUrlsByFeedId(item.getId());
        vo.setImages(imageUrls.isEmpty() ? null : imageUrls);

        vo.setLikes(item.getLikeCount() != null ? item.getLikeCount() : 0);
        vo.setComments(item.getCommentCount() != null ? item.getCommentCount() : 0);
        vo.setReposts(item.getRepostCount() != null ? item.getRepostCount() : 0);

        if (item.getCreatedAt() != null) {
            vo.setTime(formatRelativeTime(item.getCreatedAt()));
        }

        User author = userMapper.selectById(item.getAuthorId());
        if (author != null) {
            vo.setAuthor(author.getUsername());
            vo.setAvatarColor(author.getAvatarColor() != null ? author.getAvatarColor()
                    : "linear-gradient(135deg, #5B8C5A, #7EC8A8)");
        }

        if (currentUserId != null) {
            Integer liked = feedLikeMapper.isLiked(item.getId(), currentUserId);
            vo.setLiked(liked != null);
        }
        return vo;
    }

    private String formatRelativeTime(LocalDateTime dt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dt.format(fmt);
    }
}
