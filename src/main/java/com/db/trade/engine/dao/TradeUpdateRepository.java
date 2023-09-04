package com.db.trade.engine.dao;

import com.db.trade.engine.constants.Constants;
import com.db.trade.engine.model.Trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Calendar;

@Repository
public class TradeUpdateRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	public void upsertExpiredFlag(String id){
		Query query = new Query().addCriteria(Criteria.where("_id").is(id));
		Update updateExpiredFlag = new Update().set("expiredFlag", "Y");
		mongoTemplate.upsert(query, updateExpiredFlag, Trade.class);
	}


	public void audit(Trade trade) {

		trade.setCreatedDate(LocalDate.now());
		trade.set_id(""+trade.get_id()+""+LocalDate.now()+""+Calendar.getInstance().getTimeInMillis());
		mongoTemplate.insert(trade,Constants.TRADE_AUDIT_COLLECTION);
	}
	public void tradeDetails(Trade trade) {
		if (trade!=null && trade.getStatus()!=null) {
			trade.setCreatedDate(LocalDate.now());

			if (trade.getStatus().equals("INSERT"))
				mongoTemplate.insert(trade, Constants.TRADE_COLLECTION);
			else if (trade.getStatus().equals("UPDATE")) {
				Query query = new Query().addCriteria(Criteria.where("tradeId").is(trade.getTradeId()));
				//Query query = new Query().addCriteria(Criteria.where("_id").is(trade.get_id()));
				//query.addCriteria(Criteria.where("expiryFlag").is("N"));
				Update updateVersion = new Update().set("version", trade.getVersion());

				mongoTemplate.upsert(query, updateVersion,Constants.TRADE_COLLECTION);

				Update updateStatus = new Update().set("status", trade.getStatus());

				mongoTemplate.upsert(query, updateVersion,Constants.TRADE_COLLECTION);
			}
		}
	}
}
