package restassuredApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class OrangeHRMTest {
	
	private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/";
    private WebDriver driver;


	@BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

        // Set the path for ChromeDriver (update with your local path)
        String chrome = System.getProperty("user.dir") + "\\Chrome_Driver\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chrome);

        // Initialize WebDriver
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Log in before running the tests
        login(driver, "admin", "admin123");
    }

    @AfterClass
    public void tearDown() {
        // Close the WebDriver instance
        driver.quit();
    }

    @Test(priority = 1)
    public void testAddEmployee() throws InterruptedException {
    	
        // Step 1: Add new Employee using API
        Response addEmployeeResponse = addEmployee();

        String empNumber = "";
        String employeeId = "";
        try {
            String jsonResponse = addEmployeeResponse.getBody().asString();
            System.out.println("JSON Response: " + jsonResponse);

            // Parse JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(addEmployeeResponse.getBody().asString());

            // Extract empNumber and employeeId from the JSON node
            empNumber = jsonNode.path("data").path("empNumber").asText();
            employeeId = jsonNode.path("data").path("employeeId").asText();
        } catch (IOException e) {
            System.out.println("Failed to parse JSON response for addEmployee: " + e.getMessage());
        }

        // Step 2: Add personal-details using API
        addPersonalDetails(empNumber, employeeId);

        // Step 3: Add Contact Details using GUI
        addContactDetails(driver, empNumber);
    }

	private static void login(WebDriver driver, String username, String password) {
		driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
		driver.findElement(By.xpath("//input[@name='username']")).sendKeys(username);
		driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
		driver.findElement(By.xpath("//button[contains(@type,'submit')]")).click();
	}

	private static Response addEmployee() {
		// Create a JSON object with employee data
		String requestBody = "{\"firstName\":\"tamer8\",\"middleName\":\"mohamed8\",\"lastName\":\"hassan8\",\"empPicture\":null,\"employeeId\":\"0378\"}";

	  

	    // Additional headers
	    Map<String, String> headers = new HashMap<>();
	    headers.put("Accept", "application/json");
	    headers.put("Accept-Language", "en-US,en;q=0.9");
	    headers.put("Connection", "keep-alive");
	    headers.put("Content-Type", "application/json");
	    headers.put("Cookie", "orangehrm=486544069e25866c7652fd7cd2a77ed5");
	    headers.put("Origin", "https://opensource-demo.orangehrmlive.com");
	    headers.put("Referer", "https://opensource-demo.orangehrmlive.com/web/index.php/pim/addEmployee");
	    headers.put("Sec-Fetch-Dest", "empty");
	    headers.put("Sec-Fetch-Mode", "cors");
	    headers.put("Sec-Fetch-Site", "same-origin");
	    headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
	    headers.put("sec-ch-ua", "\"Google Chrome\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"");
	    headers.put("sec-ch-ua-mobile", "?0");
	    headers.put("sec-ch-ua-platform", "\"Windows\"");

	    // Use RestAssured to send the request
	    return RestAssured.given()
	            .baseUri(BASE_URL)
	            .basePath("pim/employees")
	            .headers(headers) // Add additional headers
	            .contentType(ContentType.JSON)
	            .body(requestBody)
	            .post();
	}

	private static void addPersonalDetails(String empNumber, String employeeId) {
		JSONObject personalDetailsJson = new JSONObject().put("lastName", "hassan8").put("firstName", "tamer8")
				.put("middleName", "mohamed8").put("employeeId", employeeId).put("otherId", "5")
				.put("drivingLicenseNo", "5845").put("drivingLicenseExpiredDate", "2025-08-31").put("gender", "1")
				.put("maritalStatus", "Single").put("birthday", "1999-11-30").put("nationalityId", 55)
				.put("sinNumber", "01452144");

		RestAssured.given().contentType(ContentType.JSON).body(personalDetailsJson.toString())
				.put(BASE_URL + "addEmployee/" + empNumber + "/personal-details");

	}

	private static void addContactDetails(WebDriver driver, String empNumber) throws InterruptedException {
//        login(driver, "admin", "admin123");

//        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewPimModule");
//        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewEmployeeList");

		driver.findElement(By.xpath("//a[@class='oxd-main-menu-item'][contains(.,'PIM')]")).click();
		Thread.sleep(1000);
		WebElement searchBox = driver
				.findElement(By.xpath("(//input[contains(@class,'oxd-input oxd-input--active')])[2]"));
		searchBox.sendKeys(String.valueOf(empNumber));

		driver.findElement(By.xpath("//button[@type='submit'][contains(.,'Search')]")).click();

		// Click on the edit button
		WebElement editButton = driver.findElement(By.xpath("(//i[contains(@class,'oxd-icon bi-pencil-fill')])[1]"));
		editButton.click();

		// Click on Contact Details tab
		WebElement contactDetailsTab = driver
				.findElement(By.xpath("//a[@class='orangehrm-tabs-item'][contains(.,'Contact Details')]"));
		contactDetailsTab.click();

//        // Fill Street 1 and Street 2 with random data
//        WebElement street1Field = driver.findElement(By.xpath("//yourStreet1XPath"));
//        WebElement street2Field = driver.findElement(By.xpath("//yourStreet2XPath"));
//
//        String randomStreet1 = "RandomStreet1"; // replace with your logic to generate random data
//        String randomStreet2 = "RandomStreet2"; // replace with your logic to generate random data
//
//        street1Field.sendKeys(randomStreet1);
//        street2Field.sendKeys(randomStreet2);
//
//        // Fill other contact details fields from a JSON file
//        // Use your logic to read data from the JSON file and populate the fields
//
//        // Verify the success message after filling contact details
//        WebElement successMessageContactDetails = driver.findElement(By.xpath("//yourSuccessMessageContactDetailsXPath"));
//        // Add verification logic here
//
//        // Add Attachments (assuming you have a button to add attachments)
//        WebElement addAttachmentsButton = driver.findElement(By.xpath("//yourAddAttachmentsButtonXPath"));
//        addAttachmentsButton.click();
//
//        // Verify the success message after adding attachments
//        WebElement successMessageAttachments = driver.findElement(By.xpath("//yourSuccessMessageAttachmentsXPath"));
//        // Add verification logic here
//
//        // Verify that one record is added in the grid
//        WebElement gridRecord = driver.findElement(By.xpath("//yourGridRecordXPath"));
//        // Add verification logic here
//
//        // Click on Job Details tab
//        WebElement jobDetailsTab = driver.findElement(By.xpath("//yourJobDetailsTabXPath"));
//        jobDetailsTab.click();
//
//        // Fill Job Details fields
//        // ... (similar logic as above)
//
//        // Verify the success message after filling Job Details
//        WebElement successMessageJobDetails = driver.findElement(By.xpath("//yourSuccessMessageJobDetailsXPath"));
//

	}

}
