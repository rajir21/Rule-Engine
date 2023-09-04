package com.db.trade.engine.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.db.trade.engine.model.Trade;
import com.mongodb.MongoException;

@Repository
public interface TradeRepository extends MongoRepository<Trade,String> {

	@Query(value = "{ 'tradeId' : ?0 , 'expiredFlag' : 'N' }")
	Optional<Trade> findByTradeId12(String tradeId,String expiredFlag) throws MongoException;
	@Query(value = "{ 'tradeId' : ?0 }")
	Optional<Trade> findByTradeId(String tradeId) throws MongoException;

	@Query(value = "{ 'expiredFlag' : ?0  }")
	List<Trade> findAllExpiryFlagN(String expiredFlag) throws MongoException;
}
