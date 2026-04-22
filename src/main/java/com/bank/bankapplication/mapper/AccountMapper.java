package com.bank.bankapplication.mapper;

import com.bank.bankapplication.model.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

    @Insert("INSERT INTO account (costumer_id, country) VALUES (#{costumerId}, #{country})")
    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    void insertAccount(Account account);

    @Insert("INSERT INTO balance (account_id, amount, currency) VALUES (#{accountId}, #{amount}, #{currency})")
    void insertBalance(@Param("accountId") Long accountId,
                       @Param("amount") java.math.BigDecimal amount,
                       @Param("currency") String currency);
}
