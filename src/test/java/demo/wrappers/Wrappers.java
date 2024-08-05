package demo.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;



public class Wrappers {



    //method to click any page with its text title
   public static void clickPage(ChromeDriver driver, String pageTitle){
        List<WebElement> pageLinks = driver.findElements(By.xpath("//div[@class='page']/h3"));
        
        for(WebElement pageLink : pageLinks){
            try{
                if(pageLink.getText().equals(pageTitle)){
                    pageLink.click();
                    break;//exit the loop after clicking the desired page link
                }
            }catch(StaleElementReferenceException e){
                //re-locate the elements and retry if a StaleElementReferenceException occurs
                pageLinks = driver.findElements(By.xpath("//div[@class='page']/h3"));
            }catch(Exception e){
        
                e.printStackTrace();
            }
        }
    }


    //method to wait for an element 
    public static void waitFor(ChromeDriver driver, By locator){

         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
           wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    //method for javascript executor to scroll
    public static void scrollTo(By locator,ChromeDriver driver){

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);",driver.findElement(locator));
    }



    //method to fetch the details of the table with filters 
    public static List<String> fetchHockeyTable(ChromeDriver driver, int[] pageNo, String[] tableHeadtitles, String conditionOn, String condition, double targetValue){
        List<String> sortedList = new ArrayList<>();
        

        try{
            //click on the desired page numbers
            for(int i = 0; i <= pageNo.length - 1; i++){
                WebElement footerPageNo = driver.findElement(By.xpath("//a[@href='/pages/forms/?page_num="+pageNo[i]+"']"));
                        footerPageNo.click();
    
                //wait for the page to load and scroll to the end to ensure the table is loaded
                waitFor(driver, By.xpath("//table//tr[26]"));
                scrollTo(By.xpath("//table//tr[26]"), driver);
    
                //initialize column indices
                int[] columnIndices = new int[tableHeadtitles.length];
    
                //listing all headings  
                List<WebElement> headings = driver.findElements(By.xpath("//table//th"));
    
                for(int j = 0; j <= tableHeadtitles.length - 1; j++){
                    for(WebElement heading : headings){
                        if(heading.getText().equals(tableHeadtitles[j])){
                            columnIndices[j] = headings.indexOf(heading) + 1; //adding values to indices array
                        }
                    }
                }
    
                //getting the respective int element from indices for string tableHeadTitle
                int k = columnIndices[Arrays.asList(tableHeadtitles).indexOf(conditionOn)];
    
                //getting the webelements acc to table headings
                List<WebElement> elements = driver.findElements(By.xpath("//table//tr/td[" + k + "]"));
    
                List<WebElement> filteredElements = new ArrayList<>();
    
                for(WebElement filteredElement : elements){
                    //making it generic for each condition
                    try{
                        switch(condition){
                            case "<":
                                if(Double.parseDouble(filteredElement.getText().trim()) < targetValue){
                                    filteredElements.add(filteredElement);
                                }
                                break; //exit the loop of condition operators
                            case ">":
                                if(Double.parseDouble(filteredElement.getText().trim()) > targetValue){
                                    filteredElements.add(filteredElement);
                                }
                                break; //exit the loop of condition operators
                            case "=":
                                if(Double.parseDouble(filteredElement.getText().trim()) == targetValue){
                                    filteredElements.add(filteredElement);
                                }
                                break; //exit the loop of condition operators
                            default:
                                System.out.println("Check your condition");
                        }
                    }catch(NumberFormatException e){
                        System.out.println("Error parsing number: " + filteredElement.getText());
                    }
                }
    
                //initialize row indices
                int[] rowIndices = new int[filteredElements.size()];
    
                //add values into it
                for(int p = 0; p <= filteredElements.size() - 1; p++){
                    rowIndices[p] = elements.indexOf(filteredElements.get(p)) + 2;
                }
    
                //get the final table in arraylist
                for(int m = 0; m <= rowIndices.length - 1; m++){
                    for(int n = 0; n <= columnIndices.length - 1; n++){
                        try{
                            sortedList.add(driver.findElement(By.xpath("//table//tr[" + rowIndices[m] + "]/td[" + columnIndices[n] + "]")).getText().trim());
                            
                        }catch (Exception e){
                            System.out.println("Error retrieving table data at row " + rowIndices[m] + " and column " + columnIndices[n]);
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.println("An error occurred while fetching the table: " + e.getMessage());
        }
        return sortedList;
        
    }





    public static ArrayList<String> fetchFilmsTable(ChromeDriver driver, int[] years, int[] indicesOfFilms, String[] headingsList){
        ArrayList<String> sortedList = new ArrayList<>();
        
        for(int i = 0; i <= years.length - 1; i++){
            try{
                //to iterate over all years
                WebElement year = driver.findElement(By.xpath("//a[@id='" + years[i] + "']"));
                year.click();
    
                //to ensure the page is loaded properly with all displayed elements
                waitFor(driver, By.xpath("//table//tr[5]"));
                scrollTo(By.xpath("//table//tr[5]"), driver);
    
                //initializing a column array based on given 
                int[] columnIndices = new int[headingsList.length - 1];
                List<WebElement> headings = driver.findElements(By.xpath("//table//th"));
    
                //iterate for every string except the "Best Picture"
                for(int j = 0; j <= headingsList.length - 1; j++){
                    boolean execute = false;
                    for(WebElement heading : headings){
                        if(heading.getText().trim().equals("Best Picture")){
                            continue;
                        }
                        if(!execute && heading.getText().trim().equals(headingsList[j])){
                            columnIndices[j] = headings.indexOf(heading) + 1;
                            execute = true;
                        }
                    }
                }
    
                //listing the table data in string format
                for(int m = 0; m <= indicesOfFilms.length - 1; m++){
                    sortedList.add(years[i]+"");
                    for(int n = 0; n <= columnIndices.length - 1; n++){
                        try{
                            String detail = driver.findElement(By.xpath("//table/tbody/tr[" + (indicesOfFilms[m]) + "]/td[" + columnIndices[n] + "]")).getText().trim();
                            sortedList.add(detail);
                        }catch(Exception e){
                            System.out.println("Error retrieving detail for row " + indicesOfFilms[m] + " and column " + columnIndices[n] + ": " + e.getMessage());
                        }
                    }
                    sortedList.add("Not winner");
                }
    
                //adding winner for winner films
                List<WebElement> marks = driver.findElements(By.xpath("//table/tbody/tr/td/i"));
                for(WebElement mark : marks){
                    if(mark.isDisplayed()){
                        WebElement winnerRow = mark.findElement(By.xpath("./ancestor::tr"));
                        int winnerIndex = driver.findElements(By.xpath("//table/tbody/tr")).indexOf(winnerRow) + 2;
                        for(int x = 0; x < sortedList.size(); x += headingsList.length+1) {
                            if(sortedList.get(x+1).contains(winnerRow.findElement(By.xpath("td["+columnIndices[0]+"]")).getText().trim())){
                                sortedList.set(x+headingsList.length, "Winner");
                            }
                        }
                    }
                }
            }catch(Exception e){
                System.out.println("Error processing year " + years[i] + ": " + e.getMessage());
            }
        }
        return sortedList;
    }
    

}












