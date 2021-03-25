package org.crypto.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.crypto.constants.CryptoConstants;
import org.crypto.domain.CandleStick;
import org.crypto.domain.Trade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.math.BigDecimal;


public class CandleStickTest {


    private CandleStick candleStick;
    private Trade trade;

    public CandleStickTest(){
        candleStick = new CandleStick();
        trade = new Trade();
    }


    @ParameterizedTest
    @CsvSource({"BTC_USDT,5m"})
    @DisplayName("Verify when get candlestick api call with POST method")
    public void verify_invalid_method_handled(String instrumentType,String interval) throws IOException {
        candleStick.invokeCandleStickWithInvalidMethod(instrumentType,interval);
        candleStick.validateStatusCode(405);
    }

    @ParameterizedTest
    @CsvSource({"BTC_USDT,2m,10004,Timeframe 2m is not supported."})
    @DisplayName("Verify when get Candle is called with invalid data")
    public void verify_when_called_with_invalidInput(String instrumentType,String interval,String errorCode,String errorMessage) throws IOException {
        candleStick.invokeCandleStick(instrumentType,interval);
        candleStick.validateStatusCode(200);
        candleStick.validateParameterValue(CryptoConstants.CODE,Integer.parseInt(errorCode));
        candleStick.validateParameterValue(CryptoConstants.MESSAGE,errorMessage);
    }

    @ParameterizedTest
    @CsvSource({"BTCT,1m,10004,Timeframe 2m is not supported."})
    @DisplayName("Verify when get Candle is called with invalid instrumentType")
    public void verify_response_when_getCandle_called_with_invalid_instrumentType(String instrumentType,String interval,String errorCode,String errorMessage) throws IOException {
        candleStick.invokeCandleStick(instrumentType,interval);
        candleStick.validateStatusCode(200);
        candleStick.validateDataIsNull();
    }


    @Test
    @DisplayName("Verify when get Candle is called without query parameter")
    public void verify_response_when_getCandle_called_withoutQueryParam() {
        candleStick.invokeCandleStick();
        candleStick.validateStatusCode(400);
    }



    @ParameterizedTest
    @CsvSource({"BTC_USDT,1m,1","BTC_USDT,1m,2","BTC_USDT,5m,1"})
    @DisplayName("Verify CandleStick against trades")
    public void verify_candleStickValue_against_Trade(String instrumentType,String interval,String noOfCandleToValidate) throws IOException {
        trade.invokeGetTrade(instrumentType);
        trade.validateStatusCode(200);
        trade.validateTradesResponse();
        JsonArray trades = trade.getData();
        candleStick.invokeCandleStick(instrumentType,interval);
        candleStick.validateStatusCode(200);
        candleStick.validateCandleResponse(trades,noOfCandleToValidate);

    }











}
