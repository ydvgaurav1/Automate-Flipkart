package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;
    Wrappers wrapper;
    WebDriverWait wait;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */

     
    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest
    public void startBrowser() {
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
        wrapper = new Wrappers(driver);
        wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));

        driver.manage().window().maximize();
    }

    @Test
    public void testCase01() throws InterruptedException {

        driver.get("https://www.flipkart.com");

        wrapper.searchForItem("Washing Machine");

        By sortByPopularityLocator = By.xpath("//div[text()='Popularity']");
        wrapper.clickElement(sortByPopularityLocator);
        Thread.sleep(4000);

        By ratingLocator = By.xpath("//div[contains(@class, '_5OesEi')]//div[contains(@class, 'XQDdHH')]");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(ratingLocator));
        List<WebElement> ratingElements = wrapper.findElements(ratingLocator);
        int count = 0;
        for (WebElement ratingElement : ratingElements) {
            double rating = Double.parseDouble(ratingElement.getText());
            if (rating <= 4.0) {
                count++;
            }
        }
        System.out.println("Number of items with rating <= 4 stars: " + count);
        System.out.println("Navigated to the Flipkart");
        System.out.println("Typed Washing Machine into search box");
        System.out.println("Got 4 star and lower rated products");

    }

    @Test
    public void testCase02() {
        driver.get("https://www.flipkart.com");

        wrapper.searchForItem("iPhone");

        By productTitleLocator = By.xpath("//div[@class='col col-7-12']//div[@class='KzDlHZ']");
        By discountLocator = By.xpath("//div[@class='hl05eU']//div[@class='UkUFwK']");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(productTitleLocator));

        List<WebElement> productTitles = wrapper.findElements(productTitleLocator);
        List<WebElement> discounts = wrapper.findElements(discountLocator);

        System.out.println("Products with more than 17% discount:");
        boolean foundDiscount = false;
        for (int i = 0; i < productTitles.size() && i < discounts.size(); i++) {
            String discountText = discounts.get(i).getText();
            if (discountText.contains("%")) {
                int discountValue = Integer.parseInt(discountText.replaceAll("% off", "").trim());
                if (discountValue > 17) {
                    System.out
                            .println("Title: " + productTitles.get(i).getText() + ", Discount: " + discountValue + "%");

                }

            }
        }
        if (!foundDiscount) {
            System.out.println(
                    "Successfully searched for \"iPhone\" but no products met the discount criteria of more than 17%.");
        }
    }

    @Test
    public void testCase03() throws InterruptedException {
        driver.get("https://www.flipkart.com");

        wrapper.searchForItem( "Coffee Mug");

        By fourStarFilterLocator = By.xpath("//div[contains(text(), '4') and contains(text(), 'above')]");
        wrapper.clickElement(fourStarFilterLocator);
        Thread.sleep(5000);

        By productDetailLocator = By.xpath("//div[@class='slAVV4']");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(productDetailLocator));

        List<WebElement> productElements = wrapper.findElements(productDetailLocator);
        List<String[]> products = new ArrayList<>();

        for (WebElement productElement : productElements) {
            String title = productElement.findElement(By.xpath(".//a[@class='wjcEIp']")).getText();
            String reviewText = productElement.findElement(By.xpath(".//div[@class='_5OesEi afFzxY']")).getText();
            String rating = reviewText.split("\\(")[0];
            String reviewCount = reviewText.split("\\(")[1].replace(")", "");
            int reviews = Integer.parseInt(reviewCount.replace(",", ""));
            String imageUrl = productElement.findElement(By.xpath(".//img[@loading='eager']"))
                    .getAttribute("src");

            products.add(new String[] { title, String.valueOf(reviews), imageUrl, rating });
        }

        products.sort((p1, p2) -> Integer.compare(Integer.parseInt(p2[1]), Integer.parseInt(p1[1])));

        System.out.println("Top 5 Products with the Highest Number of Reviews:");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < Math.min(5, products.size()); i++) {
            String[] product = products.get(i);
            System.out.println("Product " + (i + 1) + ":");
            System.out.println("  Title      : " + product[0]);
            System.out.println("  Rating     : " + product[3]);
            System.out.println("  Reviews    : " + product[1]);
            System.out.println("  Image URL  : " + product[2]);
            System.out.println("--------------------------------------------------");
        }

    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();

    }
}