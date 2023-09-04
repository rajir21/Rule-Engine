package com.db.trade.engine;

import com.db.trade.engine.model.Condition;
import com.db.trade.engine.model.Event;
import com.db.trade.engine.model.TradeEvent;
import com.db.trade.engine.model.Rule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

@SpringBootApplication
//@EnableScheduling
@EnableMongoRepositories
public class TradeEngineApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TradeEngineApplication.class, args);

	}

}
