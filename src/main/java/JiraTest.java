import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import java.io.File;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class JiraTest {
    public static void main(String[] args) {

        baseURI = "http://localhost:8090/";
//login

        SessionFilter sesion = new SessionFilter();
        String response = given().relaxedHTTPSValidation().header("Content-Type", "application/json")
                .body("{ \"username\": \"knapix\", \"password\": \"KoloroweKredki30!\" }")
                .log().all()
                .filter(sesion)
                .when()
                .post("http://localhost:8090/rest/auth/1/session")
                .then().log().all().extract().response().asString();

String expectedMessage = "Hi! How are you?";

        //Add comment
        String addCommentResponse = given().pathParam("key", "10005")
                .log().all()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"body\": \""+expectedMessage+"\",\n" +
                        "    \"visibility\": {\n" +
                        "        \"type\": \"role\",\n" +
                        "        \"value\": \"Administrators\"\n" +
                        "    }\n" +
                        "}")
                .filter(sesion)
                .when().post("/rest/api/2/issue/{key}/comment")
                .then().log().all()
                .assertThat().statusCode(201).extract().response().asString();

        JsonPath js = new JsonPath(addCommentResponse);
        String commentId = js.getString("id");

        //Add Attachment

        given().header("X-Atlassian-Token", "no-check")
                .filter(sesion)
                .pathParam("key", "10005")
                .header("Content-Type", "multipart/form-data")
                .multiPart("file", new File("jira.txt"))
                .when()
                .post("/rest/api/2/issue/{key}/attachments")
                .then().log().all()
                .assertThat().statusCode(200);

        //Get issue

        String issueDetails = given().filter(sesion)
                .queryParam("fields", "comment")
                .pathParam("key", "10005")
                .when()
                .get("/rest/api/2/issue/{key}")
                .then().log().all()
                .extract().response().asString();
        System.out.println(issueDetails);

        JsonPath js1 = new JsonPath(issueDetails);
        int commentsCount = js1.get("fields.comment.comments.size()");
        for (int i = 0; i < commentsCount; i++) {
            String commentIdIssue = js1.get("fields.comment.comments[" + i + "].id").toString();
            if (commentIdIssue.equalsIgnoreCase(commentId)) {
                String message = js1.get("fields.comment.comments["+i+"].body").toString();
                System.out.println(message);
                Assert.assertEquals(message, expectedMessage);

            }

        }

    }

}
