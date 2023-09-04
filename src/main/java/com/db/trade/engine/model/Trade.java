package com.db.trade.engine.model;

import com.db.trade.engine.constants.Constants;
import com.db.trade.engine.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Document(collection = Constants.TRADE_COLLECTION)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class Trade implements Event{
    @JsonIgnore
    private String _id;

    @NotBlank
    @NotNull(message="Please provide Trade Id")
    private String tradeId;

    @NotNull(message="Please provide Version Number")
    private int version;
    @NotBlank
    @NotNull(message="Please provide Counter Party ID")
    private String counterParty;
    @NotBlank
    @NotNull(message="Please provide booking ID")
    private String bookId;

    @NotNull(message="Please provide futuristic Maturity date")
    private LocalDate maturityDate;
    private LocalDate createdDate;
    private String expiredFlag;
    private String status;

    private String statusCode;
    @JsonIgnore
    private String exception;



}
