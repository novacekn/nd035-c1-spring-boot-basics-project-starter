package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

/*
	Was having an issue with the H2 in memory database not clearing data after each test. Found the decorator
	solutions below at this link https://stackoverflow.com/questions/34617152/how-to-re-create-database-before-each-test-in-spring
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
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

	public void createNote() throws InterruptedException {
		createNewUser();
		loginUser();
		Thread.sleep(1000);
		WebElement navNotesTab = driver.findElement(By.id("nav-notes-tab"));
		navNotesTab.click();
		Thread.sleep(1000);
		WebElement addNoteButton = driver.findElement(By.id("add-note-button"));
		addNoteButton.click();
		Thread.sleep(1000);
		WebElement noteTitle = driver.findElement(By.name("noteTitle"));
		WebElement noteDescription = driver.findElement(By.name("noteDescription"));
		WebElement noteSubmit = driver.findElement(By.id("noteSubmit"));
		noteTitle.sendKeys("Title 1");
		noteDescription.sendKeys("Description 1");
		noteSubmit.submit();
	}

	public void createCredential() throws InterruptedException {
		createNewUser();
		loginUser();
		Thread.sleep(1000);
		WebElement navCredsTab = driver.findElement(By.id("nav-credentials-tab"));
		navCredsTab.click();
		WebElement addCredentialButton = driver.findElement(By.id("add-credential-button"));
		Thread.sleep(1000);
		addCredentialButton.click();
		Thread.sleep(1000);
		WebElement urlField = driver.findElement(By.name("url"));
		WebElement usernameField = driver.findElement(By.name("username"));
		WebElement passwordField = driver.findElement(By.name("password"));
		WebElement credentialSubmitButton = driver.findElement(By.id("credentialSubmit"));
		urlField.sendKeys("url1");
		usernameField.sendKeys("username1");
		passwordField.sendKeys("password1");
		credentialSubmitButton.submit();
	}

	@Test
	public void testCredentialCreation() throws InterruptedException {
		createCredential();
		Assertions.assertEquals("Result", driver.getTitle());
		Assertions.assertEquals("The credential has been successfully created.", driver.findElement(By.id("success-msg")).getText());
	}

	@Test
	public void testCredentialsAreDisplayed() throws InterruptedException {
		createCredential();
		driver.get("http://localhost:" + this.port + "/home");
		driver.findElement(By.id("nav-credentials-tab")).click();
		Thread.sleep(1000);
		WebElement credentialUrl1 = driver.findElement(By.id("credentialUrl1"));
		WebElement credentialUsername1 = driver.findElement(By.id("credentialUsername1"));
		WebElement credentialPassword1 = driver.findElement(By.id("credentialPassword1"));
		Assertions.assertEquals("url1", credentialUrl1.getText());
		Assertions.assertEquals("username1", credentialUsername1.getText());
		Assertions.assertNotEquals("password1", credentialPassword1.getText());
		Assertions.assertFalse(credentialPassword1.getText().isEmpty());
	}

	@Test
	public void testEditCredential() throws InterruptedException {
		createCredential();
		driver.get("http://localhost:" + this.port + "/home");
		driver.findElement(By.id("nav-credentials-tab")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("editCredentialButton1")).click();
		Thread.sleep(1000);
		WebElement credUrl = driver.findElement(By.name("url"));
		WebElement credUsername = driver.findElement(By.name("username"));
		WebElement credPassword = driver.findElement(By.name("password"));
		credUrl.clear();
		credUrl.sendKeys("url2");
		credUsername.clear();
		credUsername.sendKeys("username2");
		credPassword.clear();
		credPassword.sendKeys("password2");
		driver.findElement(By.id("credentialSubmit")).submit();
		Assertions.assertEquals("Result", driver.getTitle());
		Assertions.assertEquals("The credential has been successfully edited.", driver.findElement(By.id("success-msg")).getText());
		driver.get("http://localhost:" + this.port + "/home");
		driver.findElement(By.id("nav-credentials-tab")).click();
		Thread.sleep(1000);
		WebElement credentialUrl1 = driver.findElement(By.id("credentialUrl1"));
		WebElement credentialUsername1 = driver.findElement(By.id("credentialUsername1"));
		WebElement credentialPassword1 = driver.findElement(By.id("credentialPassword1"));
		Assertions.assertEquals("url2", credentialUrl1.getText());
		Assertions.assertEquals("username2", credentialUsername1.getText());
		Assertions.assertNotEquals("password2", credentialPassword1.getText());
		Assertions.assertFalse(credentialPassword1.getText().isEmpty());
	}

	@Test
	public void testDeleteCredential() throws InterruptedException {
		createCredential();
		driver.get("http://localhost:" + this.port + "/home");
		driver.findElement(By.id("nav-credentials-tab")).click();
		Thread.sleep(1000);
		driver.findElement(By.linkText("Delete")).click();
		Assertions.assertEquals("Result", driver.getTitle());
		Assertions.assertEquals("The credential has been deleted successfully.", driver.findElement(By.id("success-msg")).getText());
		driver.get("http://localhost:" + this.port + "/home");
		driver.findElement(By.id("nav-credentials-tab")).click();
		Thread.sleep(1000);
		int rows = driver.findElements(By.xpath("//table[@id='credentialTable']/tbody/tr")).size();
		Assertions.assertEquals(0, rows);
	}

	@Test
	public void testNoteCreation() throws InterruptedException {
		createNote();
		Assertions.assertEquals("Result", driver.getTitle());
		WebElement successMessage = driver.findElement(By.id("success-msg"));
		Assertions.assertEquals("Your note has been successfully created.", successMessage.getText());
	}

	@Test
	public void testCreatedNoteIsDisplayed() throws InterruptedException {
		createNote();
		driver.get("http://localhost:" + this.port + "/home");
		WebElement navNotesTab = driver.findElement(By.id("nav-notes-tab"));
		navNotesTab.click();
		Thread.sleep(1000);
		WebElement noteTitle1 = driver.findElement(By.id("noteTitle1"));
		WebElement noteDescription1 = driver.findElement(By.id("noteDescription1"));
		Assertions.assertEquals("Title 1", noteTitle1.getText());
		Assertions.assertEquals("Description 1", noteDescription1.getText());
	}

	@Test
	public void testEditNote() throws InterruptedException {
		createNote();
		driver.get("http://localhost:" + this.port + "/home");
		WebElement navNotesTab = driver.findElement(By.id("nav-notes-tab"));
		navNotesTab.click();
		Thread.sleep(1000);
		WebElement editNoteButton1 = driver.findElement(By.id("editNoteButton1"));
		editNoteButton1.click();
		Thread.sleep(1000);
		WebElement noteTitle = driver.findElement(By.name("noteTitle"));
		WebElement noteDescription = driver.findElement(By.name("noteDescription"));
		WebElement noteSubmit = driver.findElement(By.id("noteSubmit"));
		noteTitle.clear();
		noteDescription.clear();
		noteTitle.sendKeys("Title 2");
		noteDescription.sendKeys("Description 2");
		noteSubmit.submit();
		Assertions.assertEquals("Result", driver.getTitle());
		WebElement successMessage = driver.findElement(By.id("success-msg"));
		Assertions.assertEquals("Your note has been successfully edited.", successMessage.getText());
		driver.get("http://localhost:" + this.port + "/home");
		WebElement navNotesTab2 = driver.findElement(By.id("nav-notes-tab"));
		navNotesTab2.click();
		Thread.sleep(1000);
		WebElement noteTitle1 = driver.findElement(By.id("noteTitle1"));
		WebElement noteDescription1 = driver.findElement(By.id("noteDescription1"));
		Assertions.assertEquals("Title 2", noteTitle1.getText());
		Assertions.assertEquals("Description 2", noteDescription1.getText());
	}

	@Test
	public void testDeleteNote() throws InterruptedException {
		createNote();
		driver.get("http://localhost:" + this.port + "/home");
		WebElement navNotesTab = driver.findElement(By.id("nav-notes-tab"));
		navNotesTab.click();
		Thread.sleep(1000);
		driver.findElement(By.linkText("Delete")).click();
		Assertions.assertEquals("Result", driver.getTitle());
		Assertions.assertEquals("The note has been deleted successfully.", driver.findElement(By.id("success-msg")).getText());
		driver.get("http://localhost:" + this.port + "/home");
		WebElement navNotesTab2 = driver.findElement(By.id("nav-notes-tab"));
		navNotesTab2.click();
		Thread.sleep(1000);
		int noteTableBody = driver.findElements(By.xpath("//table[@id='userTable']/tbody/tr")).size();
		Assertions.assertEquals(0, noteTableBody);
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
