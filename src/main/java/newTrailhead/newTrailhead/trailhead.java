package newTrailhead.newTrailhead;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class trailhead {

    public static void main(String[] args) throws InterruptedException, IOException {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

       
        //harshyadav
        
        //below file should have urls in A column starting from 1
       
        List<String> urls = readUrlsFromCsv("C:\\Users\\DESKTOP\\Desktop\\Trailhead Account Details View.csv");

     
        
        //below file should have Name, Count, Certification, Date as individual column 
        
        String filePath = "C:\\Users\\DESKTOP\\Desktop\\BetaOne.xlsx";
        String sheetName = "Sheet1";

        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
        } catch (FileNotFoundException e) {
            // Handle the exception (e.g., create a new workbook if the file doesn't exist)
            workbook = new XSSFWorkbook();
        }

        Sheet sheet = workbook.getSheet(sheetName);
        int lastRowNum = (sheet != null) ? sheet.getLastRowNum() : -1;

        for (String url : urls) {
            try {
                driver.get(url);

                // Get name of the person
                String name = driver.getTitle();
                String[] titleParts = name.split(" - ");
                String harsh = titleParts[0];
                System.out.println(harsh);

                Thread.sleep(4000);

                JavascriptExecutor j = (JavascriptExecutor) driver;
                j.executeScript("window.scrollBy(0,450)");

                WebElement element = null;

                // Check if the profile is public
                boolean isPublic = true;
                try {
                    element = (WebElement) j.executeScript("return document.querySelector(\"#profile-sections-container > tbui-private-profile-banner\").shadowRoot.querySelector(\"div > span\")");
                    if (element != null && element.getText().contains("private")) {
                        isPublic = false;
                    }
                } catch (Exception e) {
                    // Ignore any exceptions
                }

                System.out.println("Is Public: " + isPublic);

                String certCount;

                if (isPublic) {
                    // Get certificate count
                    List<WebElement> certCountElement = driver.findElements(By.xpath("//div[@class='slds-card__header-title']//h2[@class='slds-text-heading_medium slds-p-bottom_xx-small']"));
                    if (certCountElement.isEmpty()) {
                        // No certificate count element found, set "NA" for certificate count
                        certCount = "NA";
                    } else {
                        certCount = certCountElement.get(0).getText();
                        System.out.println("Cert Count: " + certCount);
                    }

                    Thread.sleep(4000);

                    // If more than three certificates, click on "Show More" button
                    List<WebElement> showMoreButtons = driver.findElements(By.xpath("//button[contains(text(),'Show More')]"));
                    if (!showMoreButtons.isEmpty()) {
                        showMoreButtons.get(0).click();
                    }

                    Thread.sleep(2000);

                    // Get certificates and descriptions
                    List<WebElement> elements = driver.findElements(By.xpath("//div[@class='slds-media__body']//a[@class='tds-text_bold slds-m-right_x-small']"));
                    List<WebElement> descriptionElements = driver.findElements(By.xpath("//div[@class='slds-media__body']//p[@class='slds-text-body_small tds-text_bold slds-m-bottom_xx-small']"));

                    Row dataRow = sheet.createRow(lastRowNum + 1);
                    int cellIndex = 0;
                    Cell dataCell = dataRow.createCell(cellIndex++);
                    dataCell.setCellValue(harsh);
                    dataCell = dataRow.createCell(cellIndex++);
                    dataCell.setCellValue(certCount);

                    if (!certCount.equals("NA")) {
                        // Process certificates only if the count is not "NA"
                        for (int i = 0; i < elements.size(); i++) {
                            WebElement webElement = elements.get(i);
                            String text = webElement.getText();
                            dataCell = dataRow.createCell(cellIndex++);
                            dataCell.setCellValue(text);
                        }

                        for (int i = 0; i < descriptionElements.size(); i++) {
                            WebElement descriptionElement = descriptionElements.get(i);
                            String description = descriptionElement.getText();
                            dataCell = dataRow.createCell(cellIndex++);
                            dataCell.setCellValue(description);
                        }
                    } else {
                        // No certificates found, set "NA" for other details
                        dataCell = dataRow.createCell(cellIndex++);
                        dataCell.setCellValue("NA");
                        dataCell = dataRow.createCell(cellIndex++);
                        dataCell.setCellValue("No Certificates");
                    }

                    lastRowNum++;
                } else {
                    // Profile is private, set "Private" status
                    Row dataRow = sheet.createRow(lastRowNum + 1);
                    int cellIndex = 0;
                    Cell dataCell = dataRow.createCell(cellIndex++);
                    dataCell.setCellValue(harsh);
                    dataCell = dataRow.createCell(cellIndex++);
                    dataCell.setCellValue("Private");

                    lastRowNum++;
                }
            } catch (Exception e) {
                System.out.println("Page Not Found: " + url);
                continue; // Move to the next URL
            }
        }

        if (fis != null) {
            fis.close();
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        workbook.close();
        driver.quit();
    }

    private static List<String> readUrlsFromCsv(String filePath) throws FileNotFoundException {
        List<String> urls = new ArrayList<>();
        Scanner scanner = new Scanner(new FileInputStream(filePath));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            urls.add(line);
        }
        scanner.close();
        return urls;
    
    
    
    }
}
