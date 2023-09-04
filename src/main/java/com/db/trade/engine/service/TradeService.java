package com.db.trade.engine.service;

import java.time.LocalDate;
import java.util.*;

import com.db.trade.engine.AlertDecision;
import com.db.trade.engine.constants.Constants;
import com.db.trade.engine.dao.TradeRepository;
import com.db.trade.engine.dao.TradeUpdateRepository;
import com.db.trade.engine.model.*;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

	@Value("${trade.engine.validator}")
	private String validatorApiURI;
	
	private static final Logger log = LoggerFactory.getLogger(TradeService.class);
	
	@Autowired
	TradeRepository tradeRepository;
	
	@Autowired
	TradeUpdateRepository tradeUpdateRepository;


	public void  persist(Trade trade){
		trade.setCreatedDate(LocalDate.now());
		System.out.println(trade.toString());
		if(trade.getStatus().equals(("UPDATE")))
			tradeUpdateRepository.tradeDetails( trade);
			else
		   tradeRepository.save(trade);
	}

	public List<Trade> findAll(){
		return  tradeRepository.findAll();
	}

	public void updateExpiryFlagOfTrade(){
		tradeRepository.findAllExpiryFlagN("N").stream().forEach(t -> {
			if (!validateMaturityDate(t)) {
				log.info("Trade which needs to updated {}", t);
				tradeUpdateRepository.upsertExpiredFlag(t.get_id());
			}
		});
	}

	public Trade combincationCheck(Trade trade){
		//if(validateMaturityDate(trade)) {

		boolean tradeStatus;
		try {

			Optional<Trade> exsitingTrade = tradeRepository.findByTradeId(trade.getTradeId());

			if (exsitingTrade!=null && exsitingTrade.isPresent() && exsitingTrade.get().getTradeId()!=null ) {

				boolean expCheck=validateMaturityDate(trade);

				boolean versionCheck = validateVersion(trade,exsitingTrade.get());

				if(expCheck) {
					trade.setStatus("UPDATE");
					trade.setStatusCode("Trade Maturity date Active");
					if(versionCheck    ) {
						trade.setStatus("UPDATE");
					trade.setStatusCode("Trade Maturity date & version is unique"); }
					else  	 { trade.setStatus("REJECT");
						trade.setStatusCode("Trade Version  is lower than the current version  ");
					}
				}else {
					trade.setStatusCode("Trade Maturity date is expired");
					trade.setStatus("REJECT");
				}

			}else{
				trade.setStatusCode("Trade Maturity date & version & Trade ID  is unique");
				tradeStatus= true;
				trade.setStatus("INSERT");
			}

			//return trade;
		}catch(Exception e) {
			audit(trade);
			log.error("Audit ?Exception Occured in isValid {}",Constants.ERROR,e.getMessage());
			// throw new ApiRequestException(Constants.ERROR);
		}
		//}
		audit(trade);
		tradeUpdateRepository.tradeDetails(trade);
		return trade;
	}
	public Object isValid(Trade trade){
		if(validateMaturityDate(trade)) {
			try {
				Optional<Trade> exsitingTrade = tradeRepository.findByTradeId(trade.getTradeId());
				if (exsitingTrade!=null  && exsitingTrade.isPresent()) {
					return validateVersion(trade, exsitingTrade.get());
				}else{
					log.debug("No Existing records inserting ");
					return true;
				}
			}catch(Exception e) {
				audit(trade);
				log.error("Exception Occured in isValid {}",Constants.ERROR,e.getMessage());
				// throw new ApiRequestException(Constants.ERROR);
			}
		}
		return false;
	}
	/*validation 1  During transmission if the
	lower version is being received by the store it will reject the trade and throw an exception.*/
	private boolean validateVersion(Trade trade,Trade oldTrade) {


		if(trade.getVersion() >= oldTrade.getVersion()){
			return true;
		}
		return false;
	}

	//Store should not allow the trade which has less maturity date then today date
	public boolean validateMaturityDate(Trade trade){
		return trade.getMaturityDate().isBefore(LocalDate.now())  ? false:true;
	}

	public void audit(Trade trade) {
		trade.setException(Constants.ERROR);
		tradeUpdateRepository.audit(trade);
	}

	public boolean validate(Trade trade){

		if(validateMaturityDate(trade)) {
			try {
	 		System.out.println(trade.getTradeId() +"OLD Trade ID " );
				Optional<Trade> exsitingTrade  = tradeRepository.findByTradeId(trade.getTradeId());
				if(exsitingTrade!=null && exsitingTrade.isPresent() && exsitingTrade.get()!=null ) {


					Trade tradeEvent = exsitingTrade.get();

					// Create an event that will be tested against the rule. In reality, the event would be read from some external store.


					Rule highValueOrderWidgetsIncRule = new Rule();

					Condition tradeIdCheck = new Condition();
					tradeIdCheck.setField("tradeId");
					tradeIdCheck.setOperator(Condition.Operator.EQUAL_TO);
					tradeIdCheck.setValue(trade.getTradeId());

					Condition versioneqcondition = new Condition();
					versioneqcondition.setField("version");
					versioneqcondition.setOperator(Condition.Operator.LESS_THAN_OR_EQUAL_TO);
					versioneqcondition.setValue(trade.getVersion());

					Condition versionGTcheck = new Condition();
					versionGTcheck.setField("version");
					versionGTcheck.setOperator(Condition.Operator.LESS_THAN_OR_EQUAL_TO);
					versionGTcheck.setValue(trade.getVersion());

					// In reality, you would have multiple rules for different types of events.
					// The eventType property would be used to find rules relevant to the event
					highValueOrderWidgetsIncRule.setEventType(Rule.eventType.TRADE);

					highValueOrderWidgetsIncRule.setConditions(Arrays.asList(tradeIdCheck, versioneqcondition));

					String drl = applyRuleTemplate(tradeEvent, highValueOrderWidgetsIncRule);
					System.out.println("drl"+drl);
					AlertDecision alertDecision = evaluate(drl, tradeEvent);
					System.out.println(alertDecision.getDoAlert());
				}

				if(exsitingTrade!=null && exsitingTrade.isPresent()) {
					System.out.println(exsitingTrade.get().getTradeId() +"OLD Trade ID " );
				}
				if (exsitingTrade.isPresent()) {
					return (boolean)validateVersion(trade, exsitingTrade.get());
				}else{
					return true;
				}
			}catch(Exception e) {
				e.printStackTrace();
				audit(trade);
				log.error("Exception Occured in isValid {}",Constants.ERROR,e.getMessage());
				// throw new ApiRequestException(Constants.ERROR);
			}
		}
		return false;
	}

	static private AlertDecision evaluate(String drl, Event event) throws Exception {
		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		kieFileSystem.write("src/main/resources/rule.drl", drl);//Constnat.TRADE_DRL_PATH
		kieServices.newKieBuilder(kieFileSystem).buildAll();

		KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
		StatelessKieSession statelessKieSession = kieContainer.getKieBase().newStatelessKieSession();

		AlertDecision alertDecision = new AlertDecision();
		statelessKieSession.getGlobals().set("alertDecision", alertDecision);
		statelessKieSession.execute(event);

		return alertDecision;
	}

	static private String applyRuleTemplate(Event event, Rule rule) throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();

		data.put("rule", rule);

		data.put("eventType", event.getClass().getName());

		try{
			Object tes = objectDataCompiler.compile(Arrays.asList(data), Thread.currentThread().getContextClassLoader().getResourceAsStream("rule-template.drl"));
		} catch(Exception e ) {e.printStackTrace();}
		return objectDataCompiler.compile(Arrays.asList(data), Thread.currentThread().getContextClassLoader().getResourceAsStream("rule-template.drl"));
	}

}
