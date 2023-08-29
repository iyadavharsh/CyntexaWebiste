package newTrailhead.newTrailhead;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class jsExe {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		 WebDriverManager.chromedriver().setup();

	        ChromeOptions options = new ChromeOptions();
	        WebDriver driver = new ChromeDriver(options);
	        driver.manage().window().maximize();

	        
	        
	        driver.get("https://trailblazer.me/id/iyadavharsh");

	        Thread.sleep(4000);

	        
	        JavascriptExecutor jse=(JavascriptExecutor) driver;
	        WebElement accprivate= (WebElement) jse.executeScript("return document.querySelector(\"#profile-sections-container > tbui-private-profile-banner\").shadowRoot.querySelector(\"div > span\")");
	        		
	        
	        String text= accprivate.getText();
	        System.out.println(text);
		
		
	}

}
