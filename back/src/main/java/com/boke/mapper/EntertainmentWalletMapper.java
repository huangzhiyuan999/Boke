package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.EntertainmentWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EntertainmentWalletMapper extends BaseMapper<EntertainmentWallet> {
    @Update("UPDATE entertainment_wallets SET coins = coins + #{coins}, updated_at = NOW() WHERE user_id = #{userId}")
    int addCoins(@Param("userId") Long userId, @Param("coins") Integer coins);

    @Update("UPDATE entertainment_wallets SET coins = coins - #{coins}, updated_at = NOW() WHERE user_id = #{userId} AND coins >= #{coins}")
    int deductCoins(@Param("userId") Long userId, @Param("coins") Integer coins);
}
