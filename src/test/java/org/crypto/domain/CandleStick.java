package org.crypto.domain;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.crypto.constants.Common;
import org.crypto.constants.CryptoConstants;
import org.crypto.framework.Base;
import org.crypto.util.Helper;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class CandleStick extends Base {
    private String instrumentName;
    private String interval;



    public void setInstrumentName(String instrumentName){ this.instrumentName = instrumentName;}
    public void setInterval(String interval){this.interval=interval;}
    public String getInstrumentName(){ return this.instrumentName;}
    public String getInterval(){ return this.interval;}

    public void invokeCandleStick(String instrumentName,String interval) throws IOException {
        addQueryParams(instrumentName,interval);
        candleStick_service_is_invoked();
    }

    public void invokeCandleStick(){
        candleStick_service_is_invoked();
    }


    private void addQueryParams(String instrumentName,String interval) throws IOException {
        setInstrumentName(instrumentName);
        setInterval(interval);
        addQueryParams("{\"instrument_name\":\"" + instrumentName +"\",\"timeframe\":\"" + interval + "\"}");
    }

    private void candleStick_service_is_invoked(){
        service_is_invoked(getCandleStickUrl(), Common.GET);
    }
    private String getCandleStickUrl(){
        return getUrl(CryptoConstants.CRYPTO_MS,CryptoConstants.GET_CANDLESTICK_API);
    }

    public void invokeCandleStickWithInvalidMethod(String instrumentName,String interval) throws IOException {
        addQueryParams(instrumentName,interval);
        service_is_invoked(getCandleStickUrl(),Common.POST);
    }

    public void validateCandleResponse(JsonArray trades,String candleToValidate){

        Assertions.assertEquals(CryptoConstants.GET_CANDLE_METHOD,getJSONResponseString().getAsJsonObject().get("method").getAsString()
                ,"Validate Method is valid");
        Assertions.assertEquals(getInstrumentName(),getResult().get(CryptoConstants.INSTRUMENT_NAME).getAsString()
                ,"Instrument Name returned is invalid");
        Assertions.assertEquals(getInterval(),getResult().get(CryptoConstants.INTERVAL).getAsString()
                , "Interval returned is invalid");
          validateCandleResponseAgainstTrades(trades,candleToValidate);

    }

    private void validateCandleResponseAgainstTrades(JsonArray trades,String candleToValidate){

       //get the candles data and pick top candleToValidate
        int candles = Integer.parseInt(candleToValidate);
        for(int index = getData().size()-1;index>getData().size()-candles-1;index--){
            JsonObject candle = getData().get(index).getAsJsonObject();
            Instant candleEndTime = Instant.ofEpochMilli(candle.get(CryptoConstants.END_TIME).getAsLong());
            Instant candleBandTime = candleEndTime.plus(Helper.getTime(getInterval()), Helper.getUnit(getInterval()));
            JsonArray expectedTrades = new JsonArray();

            trades.forEach(trade->
                    {
                        Instant tradeTime = Instant.ofEpochMilli(trade.getAsJsonObject().get(CryptoConstants.TRADE_TIME).getAsLong());
                        if(tradeTime.isAfter(candleEndTime) && tradeTime.isBefore(candleBandTime)){
                            expectedTrades.add(trade);
                        }
                    }
                    );

            ArrayList price = parseJson(expectedTrades.toString(),"$.[*].p");
            ArrayList volume = parseJson(expectedTrades.toString(),"$.[*].q");

            //validate High from the filtered trades with highest trade price
           Assertions.assertEquals(Helper.max(price).doubleValue(),candle.get(CryptoConstants.HIGH).getAsJsonPrimitive().getAsDouble(),
                   "High is not matching with trades");

           //validate Low from the filtered trades with Lowest trade price
            Assertions.assertEquals(Helper.min(price).doubleValue(),candle.get(CryptoConstants.LOW).getAsJsonPrimitive().getAsDouble(),
                   "Low is not matching with trades");

            //validate open from the filtered trades the first trade price
            Assertions.assertEquals(price.get(price.size()-1),candle.get(CryptoConstants.OPEN).getAsJsonPrimitive().getAsDouble(),
                   "Open is not matching with trades");

            //validate last from the filtered trades the last trade price
            Assertions.assertEquals(price.get(0),candle.get(CryptoConstants.CLOSE).getAsJsonPrimitive().getAsDouble(),
                   "Close is not matching with trades");

            //validate traded volume with the sum of trades of all filtered trades
            Assertions.assertEquals(Helper.sum(volume).doubleValue(),candle.get(CryptoConstants.VOLUME).getAsJsonPrimitive().getAsDouble(),
                    "Traded volume is not matching");



        }

    }







}
