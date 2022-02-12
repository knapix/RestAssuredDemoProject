import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import pl.files.Payload;
import pl.files.ReUsableMethods;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;


public class Basics {
    public static void main(String[] args) throws IOException {
        //TODO Auto-generated method stub
        //validate if Add place API is working as expected
        //Add place -> Update place with New Address -> Get Place to validate if New address is present in response

        //given - all input detail
        //when - Submit the API - resource, http method
        //then - validate the response
        //content of the file to String -> content of file can convert into Byte -> Byte data to String

        RestAssured.baseURI="https://www.rahulshettyacademy.com";
        String response = given().log().all().queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(new String(Files.readAllBytes(Paths.get("C:\\TEMP\\addPlace.json"))))
                //.body(Payload.addPlace())
                .when().post("maps/api/place/add/json")
                .then().assertThat().statusCode(200)
                .body("scope", equalTo("APP"))
                .header("Server", "Apache/2.4.18 (Ubuntu)").extract().response().asString();

        System.out.println(response);
        JsonPath js=new JsonPath(response); //for parsing json
        String placeId = js.getString("place_id");
        System.out.println(placeId);

        //Update place

        String newAddress = "Kraków";

        given().log().all().queryParam("key","qaclick123").header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"place_id\": \""+ placeId + "\",\n" +
                        "    \"address\": \""+ newAddress + "\",\n" +
                        "    \"key\": \"qaclick123\"\n" +
                        "}")
                .when().put("maps/api/place/update/json")
                .then().assertThat().log().all().statusCode(200).body("msg", equalTo("Address successfully updated"));

        //Get place


        String getPlaceResponse = given().log().all().queryParam("key", "qaclick123")
                .queryParam("place_id", placeId)
                .when().get("maps/api/place/get/json")
                .then().assertThat().log().all().statusCode(200).extract().response().asString();

        JsonPath js1 = ReUsableMethods.rawToJson(getPlaceResponse);
        String actualAddress = js1.getString("address");

        System.out.println(actualAddress);

        //Cucumber Junit, TestNG

        Assert.assertEquals(actualAddress, newAddress);


        //Add place -> Update Place with new Address -> Get Place to validate if New address is present in response
    }
}
