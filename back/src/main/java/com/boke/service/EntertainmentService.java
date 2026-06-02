package com.boke.service;

import com.boke.dto.*;
import com.boke.entity.*;
import com.boke.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntertainmentService {

    private final EntertainmentItemMapper itemMapper;
    private final EntertainmentGameMapper gameMapper;
    private final EntertainmentGameRankMapper rankMapper;
    private final EntertainmentEventMapper eventMapper;
    private final EntertainmentWalletMapper walletMapper;
    private final EntertainmentEventRecordMapper eventRecordMapper;
    private final EntertainmentGamePlayMapper gamePlayMapper;

    public EntertainmentHomeVO getHome(Long userId) {
        List<EntertainmentItemVO> items = itemMapper.findActiveItems().stream()
                .map(this::toItemVO)
                .collect(Collectors.toList());
        List<EntertainmentGameVO> games = gameMapper.findActiveGames().stream()
                .map(this::toGameVO)
                .collect(Collectors.toList());
        List<EntertainmentEventVO> events = getEvents(userId);
        return new EntertainmentHomeVO(getWallet(userId), items, games, events);
    }

    public List<EntertainmentItemVO> getItems() {
        return itemMapper.findActiveItems().stream().map(this::toItemVO).collect(Collectors.toList());
    }

    public List<EntertainmentGameVO> getGames() {
        return gameMapper.findActiveGames().stream().map(this::toGameVO).collect(Collectors.toList());
    }

    public EntertainmentGameVO getGame(String code) {
        EntertainmentGame game = gameMapper.findActiveByCode(code);
        if (game == null) throw new RuntimeException("游戏不存在");
        return toGameVO(game);
    }

    public List<EntertainmentEventVO> getEvents(Long userId) {
        Set<Long> completedIds = userId == null
                ? Collections.emptySet()
                : new HashSet<>(eventRecordMapper.findCompletedEventIds(userId));
        return eventMapper.findActiveEvents().stream()
                .map(event -> toEventVO(event, completedIds.contains(event.getId())))
                .collect(Collectors.toList());
    }

    public EntertainmentWalletVO getWallet(Long userId) {
        if (userId == null) return new EntertainmentWalletVO(2680, false);
        EntertainmentWallet wallet = ensureWallet(userId);
        boolean checkedIn = LocalDate.now().equals(wallet.getCheckedInDate());
        return new EntertainmentWalletVO(wallet.getCoins(), checkedIn);
    }

    @Transactional
    public EntertainmentWalletVO checkIn(Long userId) {
        EntertainmentWallet wallet = ensureWallet(userId);
        LocalDate today = LocalDate.now();
        if (!today.equals(wallet.getCheckedInDate())) {
            wallet.setCoins(wallet.getCoins() + 80);
            wallet.setCheckedInDate(today);
            walletMapper.updateById(wallet);
        }
        return getWallet(userId);
    }

    @Transactional
    public EntertainmentWalletVO finishEvent(Long userId, Long eventId) {
        EntertainmentEvent event = eventMapper.selectById(eventId);
        if (event == null || !"active".equals(event.getStatus())) throw new RuntimeException("活动不存在");
        ensureWallet(userId);
        if (eventRecordMapper.existsRecord(userId, eventId) == 0) {
            EntertainmentEventRecord record = new EntertainmentEventRecord();
            record.setUserId(userId);
            record.setEventId(eventId);
            record.setCompletedAt(LocalDateTime.now());
            eventRecordMapper.insert(record);
            walletMapper.addCoins(userId, event.getReward());
        }
        return getWallet(userId);
    }

    @Transactional
    public EntertainmentWalletVO playGame(Long userId, String code, Integer score) {
        EntertainmentGame game = gameMapper.findActiveByCode(code);
        if (game == null) throw new RuntimeException("游戏不存在");
        if (userId != null) ensureWallet(userId);
        EntertainmentGamePlay play = new EntertainmentGamePlay();
        play.setUserId(userId);
        play.setGameId(game.getId());
        play.setScore(score != null ? score : 0);
        gamePlayMapper.insert(play);
        return getWallet(userId);
    }

    private EntertainmentWallet ensureWallet(Long userId) {
        EntertainmentWallet wallet = walletMapper.selectById(userId);
        if (wallet == null) {
            wallet = new EntertainmentWallet();
            wallet.setUserId(userId);
            wallet.setCoins(2680);
            walletMapper.insert(wallet);
        }
        return wallet;
    }

    private EntertainmentItemVO toItemVO(EntertainmentItem item) {
        return new EntertainmentItemVO(item.getId(), item.getName(), item.getCategory(), item.getDescription(),
                item.getPrice(), item.getBadge(), item.getStock(), item.getWishCount(), item.getImageUrl());
    }

    private EntertainmentGameVO toGameVO(EntertainmentGame game) {
        List<String> tags = Arrays.stream(Optional.ofNullable(game.getTags()).orElse("").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        List<EntertainmentRankVO> ranks = rankMapper.findTopByGameId(game.getId()).stream()
                .map(rank -> new EntertainmentRankVO(rank.getPlayerName(), rank.getScore()))
                .collect(Collectors.toList());
        return new EntertainmentGameVO(game.getId(), game.getCode(), game.getName(), game.getShortName(),
                game.getGenre(), game.getDescription(), tags, game.getRating(), game.getPlayers(),
                game.getImageUrl(), game.getDetailBackground(), game.getDemoTitle(), game.getDemoText(), ranks);
    }

    private EntertainmentEventVO toEventVO(EntertainmentEvent event, boolean completed) {
        boolean done = completed || Objects.equals(event.getDefaultDone(), 1);
        return new EntertainmentEventVO(event.getId(), event.getIcon(), event.getTitle(), event.getDescription(),
                event.getReward(), done);
    }
}
