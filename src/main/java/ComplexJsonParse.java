import io.restassured.path.json.JsonPath;
import pl.files.Payload;

public class ComplexJsonParse {
    public static void main(String[] args) {


        JsonPath js = new JsonPath(Payload.coursePrice());

        //Print No of courses returned by API
        int count = js.getInt("courses.size()");

        System.out.println(count);

        //Print Purchase amount
        int totalAmount = js.getInt("dashboard.purchaseAmount");
        System.out.println(totalAmount);

        //Print title of the first course
        String titleFirstCourse = js.get("courses[0].title");
        System.out.println(titleFirstCourse);

        //Print all course titles and their respective Prices
        for (int i = 0; i < count; i++) {
            String courseTitles = js.get("courses[" + i + "].title");
            System.out.println(js.get("courses[" + i + "].price").toString());
            System.out.println(courseTitles);
        }

        //Print number of copies sold by RPA Course
        System.out.println("Print number of copies sold by RPA Course.");

        for (int i = 0; i < count; i++) {
            String courseTitles = js.get("courses[" + i + "].title");
            if (courseTitles.equalsIgnoreCase("RPA")) {
                int copies = js.get("courses[" + i + "].copies");
                System.out.println(copies);
                break;
            }
        }

    }
}
