package com.boke.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boke.dto.FollowVO;
import com.boke.entity.Follow;
import com.boke.entity.User;
import com.boke.mapper.FollowMapper;
import com.boke.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService extends ServiceImpl<FollowMapper, Follow> {

    private final UserMapper userMapper;

    @Transactional
    public void follow(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new RuntimeException("不能关注自己");
        }
        if (baseMapper.isFollowing(followerId, followedId) != null) {
            throw new RuntimeException("已关注");
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowedId(followedId);
        follow.setCreatedAt(LocalDateTime.now());
        save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followedId) {
        baseMapper.unfollow(followerId, followedId);
    }

    public List<FollowVO> getFollowing(Long userId) {
        List<User> users = baseMapper.findFollowingUsers(userId);
        return users.stream().map(u -> {
            FollowVO vo = new FollowVO();
            vo.setId(u.getId());
            vo.setName(u.getUsername());
            vo.setAvatarColor(u.getAvatarColor());
            vo.setBio(u.getBio());
            vo.setFollowed(true);
            return vo;
        }).collect(Collectors.toList());
    }

    public List<FollowVO> getFollowers(Long userId) {
        List<User> users = baseMapper.findFollowerUsers(userId);
        return users.stream().map(u -> {
            FollowVO vo = new FollowVO();
            vo.setId(u.getId());
            vo.setName(u.getUsername());
            vo.setAvatarColor(u.getAvatarColor());
            vo.setBio(u.getBio());
            vo.setFollowed(baseMapper.isFollowing(userId, u.getId()) != null);
            return vo;
        }).collect(Collectors.toList());
    }

    public List<FollowVO> getSuggestions(Long userId) {
        List<User> users = userMapper.selectList(null);
        return users.stream()
                .filter(u -> !u.getId().equals(userId))
                .filter(u -> baseMapper.isFollowing(userId, u.getId()) == null)
                .limit(5)
                .map(u -> {
                    FollowVO vo = new FollowVO();
                    vo.setId(u.getId());
                    vo.setName(u.getUsername());
                    vo.setAvatarColor(u.getAvatarColor());
                    vo.setBio(u.getBio());
                    vo.setFollowed(false);
                    vo.setFans(baseMapper.countFollowers(u.getId()) + " 粉丝");
                    return vo;
                }).collect(Collectors.toList());
    }

    public List<FollowVO> searchUsers(Long currentUserId, String keyword) {
        List<User> users = userMapper.selectList(null);
        return users.stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .filter(u -> u.getUsername().contains(keyword) || (u.getBio() != null && u.getBio().contains(keyword)))
                .limit(20)
                .map(u -> {
                    FollowVO vo = new FollowVO();
                    vo.setId(u.getId());
                    vo.setName(u.getUsername());
                    vo.setAvatarColor(u.getAvatarColor());
                    vo.setBio(u.getBio());
                    vo.setFollowed(baseMapper.isFollowing(currentUserId, u.getId()) != null);
                    return vo;
                }).collect(Collectors.toList());
    }
}
