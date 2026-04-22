package com.bank.bankapplication.mapper;

import com.bank.bankapplication.model.Account;
import com.bank.bankapplication.model.Balance;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Mapper
public interface AccountMapper {

    @Insert("INSERT INTO account (costumer_id, country) VALUES (#{costumerId}, #{country})")
    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    void insertAccount(Account account);


    @Insert("INSERT INTO balance (account_id, amount, currency) VALUES (#{accountId}, #{amount}, #{currency})")
    void insertBalance(@Param("accountId") Long accountId,
                       @Param("amount") java.math.BigDecimal amount,
                       @Param("currency") String currency);


    @Select("SELECT * FROM account WHERE id = #{accountId}")
    @Results(value = {
            @Result(property = "accountId", column = "id"),
            @Result(property = "costumerId", column = "costumer_id"),
            @Result(property = "balances", column = "id",
            many = @Many(select = "findBalancesByAccountId"))
    })
    Account findAccountById(Long accountId);

    @Select("SELECT amount as availableAmount, currency FROM balance WHERE account_id = #{accountId}")
    List<Balance> findBalancesByAccountId(Long accountId);
}
