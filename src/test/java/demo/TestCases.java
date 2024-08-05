package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    static ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */

     
    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
       
        System.out.println("Start Automation: Create a Scraper");
    }

    @AfterTest
    public void endTest()
    {  
        System.out.println("End Automation: Create a Scraper");
        driver.close();
        driver.quit();
    }

    @Test
    public static void testCase01(){
        System.out.println("Start Test Case: testCase01");

        //launch the url 
        driver.get("https://www.scrapethissite.com/pages/");

        //apply an assert whether the correct url is launched or not
        Assert.assertTrue(driver.getCurrentUrl().contains("scrape") && driver.getCurrentUrl().contains("pages"),"The current url is not same as the url given");

        //click on the "Hockey Teams: Forms, Searching and Pagination" link
        Wrappers.clickPage(driver, "Hockey Teams: Forms, Searching and Pagination");
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("forms"),"Did not click to correct url");

        //wait for the page to load completely  
        Wrappers.waitFor(driver, By.xpath("//table//tr[26]"));

        //scroll to the end of the table to load all elements
        Wrappers.scrollTo(By.xpath("//table//tr[26]"), driver);

        //fetch the details and collect the Team Name, Year and Win % for the teams with Win % less than 40% (0.40) for page 1,2,3,4 
        List<String> sortedList = Wrappers.fetchHockeyTable(driver, new int[] {1,2,3,4} , new String[] {"Team Name", "Year", "Win %"},"Win %","<", 0.4);
    

        //populating a map from sortedList list and making a list 
        List<Map<String, Object>> finalList = new ArrayList<>();

        for(int i=0;i<=sortedList.size()-3;i+=3){
        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.put("Epoch Time of Scrape",System.currentTimeMillis()/1000);
        inputMap.put("Team Name",sortedList.get(i));
        inputMap.put("Year",sortedList.get(i+1));
        inputMap.put("Win %",sortedList.get(i+2));
        finalList.add(inputMap);
        }
        
        ObjectMapper mapper = new ObjectMapper();
         //converting list to a JSON as string
        try {
            String hockeyFile = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalList);
            System.out.println(hockeyFile);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String userDir = System.getProperty("user.dir");
        //writing JSON on a file
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(userDir + "\\src\\test\\resources\\hockey-team-data.json"), finalList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("End Test Case: testCase01");
    }



    @Test
    public static void testCase02(){
        System.out.println("Start Test Case: testCase02");

        //launch the url 
        driver.get("https://www.scrapethissite.com/pages/");

        //apply an assert whether the correct url is launched or not
        Assert.assertTrue(driver.getCurrentUrl().contains("scrape") && driver.getCurrentUrl().contains("pages"),"The current url is not same as the url given");

        //click on the "Oscar Winning Films: AJAX and Javascript" link
        Wrappers.clickPage(driver, "Oscar Winning Films: AJAX and Javascript");

        //click on any year . let 2015
        ArrayList<String> sortedList = Wrappers.fetchFilmsTable(driver, new int[] {2015,2014,2013,2012,2011,2010}, new int[] {1,2,3,4,5}, new String[]{"Title","Nominations","Awards","Best Picture"});


        List<Map<String, Object>> finalList = new ArrayList<>();

        for(int i=0;i<=sortedList.size()-5;i+=5){
        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.put("Epoch Time of Scrape",System.currentTimeMillis()/1000);
        inputMap.put("Year",sortedList.get(i));
        inputMap.put("Title",sortedList.get(i+1));
        inputMap.put("Nominations",sortedList.get(i+2));
        inputMap.put("Awards",sortedList.get(i+3));
        inputMap.put("Best Pictures",sortedList.get(i+4));
        finalList.add(inputMap);
        }

        ObjectMapper mapper = new ObjectMapper();
        //converting list to a JSON as string
       try {
           String oscarFile = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalList);
           System.out.println(oscarFile);
       } catch (JsonProcessingException e) {
           e.printStackTrace();
       }
       String userDir = System.getProperty("user.dir");
       //writing JSON on a file
       try {
           mapper.writerWithDefaultPrettyPrinter().writeValue(new File(userDir + "\\src\\test\\resources\\oscar-winner-data.json"), finalList);
       } catch (IOException e) {
           e.printStackTrace();
       }

        System.out.println("End Test Case: testCase02");
    }
}