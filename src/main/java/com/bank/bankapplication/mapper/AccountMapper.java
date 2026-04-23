package com.bank.bankapplication.mapper;

import com.bank.bankapplication.model.Account;
import com.bank.bankapplication.model.Balance;
import com.bank.bankapplication.model.Transaction;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountMapper {

    @Insert("INSERT INTO account (customer_id, country) VALUES (#{customerId}, #{country})")
    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    void insertAccount(Account account);


    @Insert("INSERT INTO balance (account_id, amount, currency) VALUES (#{accountId}, #{amount}, #{currency})")
    void insertBalance(@Param("accountId") Long accountId,
                       @Param("amount") BigDecimal amount,
                       @Param("currency") String currency);


    @Select("SELECT id, customer_id, country FROM account WHERE id = #{accountId} FOR UPDATE")
    @Results(value = {
            @Result(property = "accountId", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "balances", column = "id",
            many = @Many(select = "findBalancesByAccountId"))
    })
    Account findAccountById(Long accountId);

    @Select("SELECT amount AS availableAmount, currency FROM balance WHERE account_id = #{accountId}")
    List<Balance> findBalancesByAccountId(Long accountId);

    @Insert("INSERT INTO transaction (account_id, amount, currency, direction, description, balance_after_transaction) " +
    "VALUES (#{accountId}, #{amount}, #{currency}, #{direction}, #{description}, #{balanceAfterTransaction})")
    @Options(useGeneratedKeys = true, keyProperty = "transactionId")
    void insertTransaction(Transaction transaction);

    @Update("UPDATE balance SET amount = amount + #{change} " +
    "WHERE account_id = #{accountId} AND currency = #{currency}")
    int updateBalance(@Param("accountId")  Long accountId,
                      @Param("currency") String currency,
                      @Param("change") BigDecimal change);

    @Select("SELECT amount FROM balance WHERE account_id = #{accountId} AND currency = #{currency}")
    BigDecimal getBalanceAmount(@Param("accountId") Long accountId,
                                @Param("currency") String currency);

    @Select("SELECT id AS transactionId, account_id AS accountId, amount, currency, direction, description, balance_after_transaction AS balanceAfterTransaction " +
    "FROM transaction WHERE account_id = #{accountId}")
    List<Transaction> findTransactionByAccountId(Long accountId);
}


