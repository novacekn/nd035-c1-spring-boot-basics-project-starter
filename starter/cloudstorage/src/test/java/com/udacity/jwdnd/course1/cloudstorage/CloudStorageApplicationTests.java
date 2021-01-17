package com.udacity.jwdnd.course1.cloudstorage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	public void createNewUser() {
		driver.get("http://localhost:" + this.port + "/signup");
		WebElement firstName = driver.findElement(By.name("firstName"));
		WebElement lastName = driver.findElement(By.name("lastName"));
		WebElement username = driver.findElement(By.name("username"));
		WebElement password = driver.findElement(By.name("password"));
		WebElement submitButton = driver.findElement(By.id("submit-button"));

		firstName.sendKeys("John");
		lastName.sendKeys("Doe");
		username.sendKeys("jdoe");
		password.sendKeys("password");
		submitButton.submit();
	}

	public void createNote() {

	}

	public void loginUser() {
		driver.get("http://localhost:" + this.port + "/login");
		WebElement username = driver.findElement(By.name("username"));
		WebElement password = driver.findElement(By.name("password"));
		WebElement submitButton = driver.findElement(By.id("submit-button"));
		username.sendKeys("jdoe");
		password.sendKeys("password");
		submitButton.submit();
	}

	@Test
	public void testUnauthorizedUserCannotAccessHomePage() {
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	public void testUnauthorizedUserCanAccessLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	public void testUnauthorizedUserCanAccessSignupPage() {
		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}

	@Test
	public void testAuthorizedUserCanAccessHomePage() {
		createNewUser();
		loginUser();
		Assertions.assertEquals("Home", driver.getTitle());
	}

	@Test
	public void testLogoutUser() {
		createNewUser();
		loginUser();
		WebElement logoutButton = driver.findElement(By.id("logout-button"));
		logoutButton.submit();
		Assertions.assertEquals("Login", driver.getTitle());
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}
}
