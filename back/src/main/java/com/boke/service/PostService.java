package com.boke.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boke.dto.PostDetail;
import com.boke.dto.PostRequest;
import com.boke.entity.*;
import com.boke.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService extends ServiceImpl<PostMapper, Post> {

    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;
    private final UserMapper userMapper;

    public List<PostDetail> getPublishedPosts() {
        List<Post> posts = baseMapper.findAllPublished();
        return posts.stream().map(this::toDetail).collect(Collectors.toList());
    }

    public List<PostDetail> getPublishedByType(String postType) {
        List<Post> posts = baseMapper.findPublishedByType(postType);
        return posts.stream().map(this::toDetail).collect(Collectors.toList());
    }

    public List<PostDetail> getHotPosts() {
        List<Post> posts = lambdaQuery()
                .eq(Post::getIsPublished, 1)
                .eq(Post::getPostType, "post")
                .orderByDesc(Post::getViewCount)
                .orderByDesc(Post::getUpdatedAt)
                .list();
        return posts.stream().map(this::toDetail).collect(Collectors.toList());
    }

    public PostDetail getPostDetail(Long id) {
        Post post = getById(id);
        if (post == null) return null;

        PostDetail detail = toDetail(post);
        // Increment view count
        post.setViewCount((post.getViewCount() == null ? 0 : post.getViewCount()) + 1);
        updateById(post);
        detail.setViewCount(post.getViewCount());
        return detail;
    }

    public List<PostDetail> getPostsByTag(String tagName) {
        Tag tag = tagMapper.findByName(tagName);
        if (tag == null) return Collections.emptyList();

        List<Post> posts = lambdaQuery()
                .eq(Post::getIsPublished, 1)
                .inSql(Post::getId,
                        "SELECT pt.post_id FROM post_tags pt WHERE pt.tag_id = " + tag.getId())
                .orderByDesc(Post::getCreatedAt)
                .list();

        return posts.stream().map(this::toDetail).collect(Collectors.toList());
    }

    @Transactional
    public PostDetail createPost(PostRequest req, Long authorId) {
        Post post = new Post();
        post.setAuthorId(authorId);
        post.setTitle(req.getTitle());
        post.setSummary(req.getSummary());
        post.setCover(req.getCover() != null ? req.getCover() : "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");
        post.setContent(req.getContent());
        post.setPostType(req.getPostType() != null ? req.getPostType() : "post");
        post.setViewCount(0L);
        post.setIsPublished(req.getIsPublished() != null ? req.getIsPublished() : 1);
        save(post);

        saveTags(post.getId(), req.getTags());
        return getPostDetail(post.getId());
    }

    @Transactional
    public PostDetail updatePost(Long id, PostRequest req) {
        Post post = getById(id);
        if (post == null) throw new RuntimeException("文章不存在");

        if (req.getTitle() != null) post.setTitle(req.getTitle());
        if (req.getSummary() != null) post.setSummary(req.getSummary());
        if (req.getCover() != null) post.setCover(req.getCover());
        if (req.getContent() != null) post.setContent(req.getContent());
        if (req.getPostType() != null) post.setPostType(req.getPostType());
        if (req.getIsPublished() != null) post.setIsPublished(req.getIsPublished());
        updateById(post);

        if (req.getTags() != null) {
            postTagMapper.deleteByPostId(id);
            saveTags(id, req.getTags());
        }
        return getPostDetail(id);
    }

    @Transactional
    public void deletePost(Long id) {
        postTagMapper.deleteByPostId(id);
        removeById(id);
    }

    public List<String> getAllTags() {
        List<Tag> tags = tagMapper.selectList(null);
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }

    private void saveTags(Long postId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return;
        for (String name : tagNames) {
            Tag tag = tagMapper.findByName(name);
            if (tag == null) {
                tag = new Tag();
                tag.setName(name);
                tag.setSlug(name.toLowerCase().replace(" ", "-"));
                tagMapper.insert(tag);
            }
            PostTag pt = new PostTag();
            pt.setPostId(postId);
            pt.setTagId(tag.getId());
            postTagMapper.insert(pt);
        }
    }

    private PostDetail toDetail(Post post) {
        PostDetail d = new PostDetail();
        d.setId(post.getId());
        d.setAuthorId(post.getAuthorId());
        d.setTitle(post.getTitle());
        d.setSummary(post.getSummary());
        d.setCover(post.getCover());
        d.setContent(post.getContent());
        d.setPostType(post.getPostType());
        d.setViewCount(post.getViewCount());
        d.setIsPublished(post.getIsPublished());

        List<String> tags = baseMapper.findTagsByPostId(post.getId());
        d.setTags(tags);

        if (post.getCreatedAt() != null) {
            d.setCreatedAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (post.getUpdatedAt() != null) {
            d.setUpdatedAt(post.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (post.getAuthorId() != null) {
            User author = userMapper.selectById(post.getAuthorId());
            if (author != null) {
                d.setAuthorName(author.getUsername());
                d.setAuthorAvatar(author.getAvatarColor());
            }
        }
        return d;
    }
}
