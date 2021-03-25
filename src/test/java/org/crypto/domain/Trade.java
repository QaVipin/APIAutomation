package org.crypto.domain;

import org.crypto.constants.Common;
import org.crypto.constants.CryptoConstants;
import org.crypto.framework.Base;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.ArrayList;

public class Trade extends Base {
    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    private String instrumentName;


    public void invokeGetTrade(String instrumentName) throws IOException {
        addQueryParams(instrumentName);
        getTrades_service_is_invoked();
    }


    @Override
    public void addQueryParams(String instrumentName) throws IOException {
        setInstrumentName(instrumentName);
        super.addQueryParams("{\"instrument_name\":\"" + instrumentName +"\"}");
    }

    private void getTrades_service_is_invoked(){
        service_is_invoked(getTradesUrl(), Common.GET);
    }
    private String getTradesUrl(){
        return getUrl(CryptoConstants.CRYPTO_MS,CryptoConstants.GET_TRADES_API);
    }

    public void invokeGetTradesWithInvalidMethod(String instrumentName) throws IOException {
        addQueryParams(instrumentName);
        service_is_invoked(getTradesUrl(),Common.POST);
    }

    public void validateTradesResponse(){
        Assertions.assertEquals(CryptoConstants.GET_TRADES_METHOD,getJSONResponseString().getAsJsonObject().get("method").getAsString()
                ,"Validate Method is valid");
        Assertions.assertEquals(getInstrumentName(),getResult().get(CryptoConstants.INSTRUMENT_NAME).getAsString()
                ,"Instrument Name returned is invalid");

        ArrayList instructionType = parseJson(getData().toString(),"$.[*].i");
        Assertions.assertFalse(instructionType.stream().filter(instr-> !instr.toString().equals(getInstrumentName())).count()>0,"Get Trades returned trades other than " + getInstrumentName());

    }

}
