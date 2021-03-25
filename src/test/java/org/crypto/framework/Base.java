package org.crypto.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.crypto.constants.Common;
import org.crypto.constants.CryptoConstants;
import org.crypto.util.Helper;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class Base {

    private RequestSpecBuilder rsb;
    private Map<String,String> queryParamMap;
    private Response responseString;
    private JsonPath path;
    public static  Properties property;
    private JsonObject body;


    public Base(){
        rsb = new RequestSpecBuilder();
        property = Helper.loadProperties();
        //body = new JsonObject();

    }


    public void setBody(JsonObject body){
        this.body = body;
    }
    //for any specific method
    public void service_is_invoked(String url,String methodName){
        post(this.body,url,methodName);
    }

    //for post method
    public void service_is_invoked(String url){
        post(this.body,url,Common.POST);
    }


    public void post(Object body,String url,String method){
        rsb = new RequestSpecBuilder();
        if(queryParamMap!=null && queryParamMap.size()>0){
            rsb.addQueryParams(queryParamMap);
        }
        if(body!=null && body.toString()!=""){
            rsb.setBody(body);
        }

        final RequestSpecification requestConfiguration = rsb.build();
        System.out.println(requestConfiguration.log().uri().toString());
        responseString = RestAssured.given()
                .spec(requestConfiguration).relaxedHTTPSValidation()
                .when().request(method,url)
                .then().extract().response();

        System.out.println(responseString.getBody().prettyPrint());
        path = responseString.jsonPath();
    }

    public String getUrl(String msName,String serviceName){
        return property.getProperty(msName+ Common.DELIMITER+property.get(Common.ENV))+property.getProperty(serviceName);

    }

    public void addQueryParams(String params) throws IOException {
        ObjectMapper om = new ObjectMapper();
        queryParamMap = om.readValue(params,new TypeReference<Map<String,Object>>(){});
    }


    public JsonElement getJSONResponseString(){
        String temp = getResponseString().asString();
        return JsonParser.parseString(temp);
    }

    public Response getResponseString(){
        return responseString;
    }

    public void validateStatusCode(int statusCode){
        Assertions.assertEquals(statusCode,getStatus());
    }

    private int getStatus(){
        return responseString.getStatusCode();
    }

    public void validateParameterValue(String parameter,String expectedValue){
        Assertions.assertEquals(expectedValue,path.get(parameter).toString());
    }

    public void validateParameterValue(String parameter,int expectedValue){
        Assertions.assertEquals(expectedValue,path.getInt(parameter));
    }

    public JsonObject getResult(){
        return getJSONResponseString().getAsJsonObject().get(CryptoConstants.RESULT).getAsJsonObject();
    }

    public JsonArray getData(){
        return getResult().get(CryptoConstants.DATA).getAsJsonArray();
    }

    public String getQuery(String queryName){
        return property.getProperty(queryName);
    }

    public ArrayList parseJson(String jsonString, String jsonPath){
        return com.jayway.jsonpath.JsonPath.parse(jsonString).read(jsonPath,ArrayList.class);

    }

    public void validateDataIsNull(){
        Assertions.assertTrue(getResult().get(CryptoConstants.DATA).isJsonNull());
    }

}
