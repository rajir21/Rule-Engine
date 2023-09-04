package com.db.trade.engine.controller;

import com.db.trade.engine.constants.Constants;
import com.db.trade.engine.exception.InvalidTradeException;
import com.db.trade.engine.model.Trade;
import com.db.trade.engine.service.TradeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@ControllerAdvice
@RequestMapping("/rules")
public class TradeValidateController {
    private static final Logger log = LoggerFactory.getLogger(TradeValidateController.class);
    @Autowired
    TradeService tradeService;
    
    @PostMapping(Constants.ISVALID)
    public ResponseEntity<Object> tradeIsValid(@Valid @RequestBody Trade trade){

        return ResponseEntity.ok(tradeService.isValid(tradeService.combincationCheck(trade)));
    }
    @PostMapping(Constants.VALIDATOR)
    public ResponseEntity<Object> validator(@Valid @RequestBody Trade trade){
       // based on statsu return true /false
        return ResponseEntity.ok(tradeService.isValid(tradeService.combincationCheck(trade)));
    }
    @PostMapping(Constants.TRADE)
    public ResponseEntity<Object> tradeValidateStore(@RequestBody Trade trade){
        try {

          //  return ResponseEntity.ok(tradeService.isValid(tradeService.combincationCheck(trade)));
            if(tradeService.validate(trade)) {
               Trade tradeStatus =tradeService.combincationCheck(trade);

                tradeService.persist( tradeStatus);
            }else{
                throw new InvalidTradeException(trade.getTradeId()+"  rade Id combination is not correct");
            }
        }catch(Exception e) {
            log.error("Exception Occured in isValid {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.ERROR);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(Constants.TRADE_ALL)
    public List<Trade> findAllTrades(){
        return tradeService.findAll();
    }


    @PostMapping(Constants.TRADE_STATUS)
    public ResponseEntity<Object> tradeStatus(@Valid @RequestBody Trade trade){
        Trade  tradeObj = (Trade) tradeService.combincationCheck(trade);
        Object strObj = tradeObj.getStatus();

        return ResponseEntity.ok(strObj);

    }


    @PostMapping(Constants.MATURITYDATE)
    public ResponseEntity<Boolean> validateMaturityDate(@Valid @RequestBody Trade trade){
        return ResponseEntity.ok(tradeService.validateMaturityDate(trade));
    }


    private ResponseEntity<VndErrors> error(
            final Exception exception, final HttpStatus httpStatus, final String logRef) {
        final String message =
                Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
        return new ResponseEntity<>(new VndErrors(logRef, message), httpStatus);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<VndErrors> assertionException(final IllegalArgumentException e) {
        return error(e, HttpStatus.NOT_ACCEPTABLE, e.getLocalizedMessage());
    }


}
