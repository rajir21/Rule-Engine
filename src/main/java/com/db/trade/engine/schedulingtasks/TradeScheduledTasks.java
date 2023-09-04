
package com.db.trade.engine.schedulingtasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.db.trade.engine.service.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TradeScheduledTasks {
	
	private static final Logger log = LoggerFactory.getLogger(TradeScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Value("${trade.expiry.schedule.enabled}")
	private boolean schedulerEnable;

	@Autowired
    TradeService tradeService;

	@Scheduled(cron = "${trade.expiry.schedule}")
	public void reportCurrentTime() {
		
		log.info("schedulerEnable Flag is {}", schedulerEnable);
		if(schedulerEnable) {
			log.info("The time is now {}", dateFormat.format(new Date()));
			tradeService.updateExpiryFlagOfTrade();
		}
	}
}