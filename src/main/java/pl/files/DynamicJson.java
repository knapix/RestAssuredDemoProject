package pl.files;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class DynamicJson {

    @Test(dataProvider = "booksData")
    public void addBook(String isbn, String aisle) {

        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://rahulshettyacademy.com";
        String response = given().header("Content-Type", "application/json")
                .body(Payload.addBook(isbn, aisle))
                .when()
                .post("/Library/Addbook.php")
                .then().assertThat().statusCode(200)
                .extract().response().asString();
        JsonPath js = ReUsableMethods.rawToJson(response);
        String id = js.get("ID");
        System.out.println(id);
    }

    @DataProvider(name="booksData")
    public Object[][] getData() {
        //array = collection of elements
        //multidimensional array = collection of arrays
        return new Object[][]{{"ajajaj","6456"}, {"ojoj", "5832"}, {"orty", "6798"} };
    }

}
