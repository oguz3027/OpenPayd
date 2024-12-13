package crawlerTest;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import pages.BasePage;
import utilities.BrowserUtils;
import utilities.ConfigurationReader;
import utilities.Driver;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import static io.restassured.RestAssured.*;

public class AmazonDepartmentCrawler {

    @Test
    void crawlerTest() {

        WebDriver driver = Driver.getChromeDriver();
        BasePage basePage = new BasePage(driver);
        Actions actions = new Actions(driver);

        try {

            driver.get(ConfigurationReader.getProperty("web-page-url"));

            basePage.allDropdownMenu.click();
            basePage.seeAllButtonForShopByDepartment.click();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String outputFileName = timestamp + "_results.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));

            BrowserUtils.sleep(1);
            List<WebElement> departmentsLinks = driver.findElements(By.cssSelector("ul.hmenu-visible a.hmenu-item"));
            BrowserUtils.sleep(1);
            for (WebElement department : departmentsLinks) {
                System.out.println("department : " + department.findElement(By.xpath(".//div")).getText());
                if (Integer.parseInt(department.getAttribute("data-menu-id")) > 4 && Integer.parseInt(department.getAttribute("data-menu-id")) < 27) {
                    BrowserUtils.sleep(2);
                    actions.click(department).perform();
                    BrowserUtils.sleep(2);
                    List<WebElement> departmentElementLinks = driver.findElements(By.cssSelector("ul.hmenu-visible a.hmenu-item"));
                    BrowserUtils.sleep(2);
                    for (WebElement linkElement : departmentElementLinks) {
                        String url = linkElement.getAttribute("href");
                        System.out.println("url : " + url);
                        String linkText = linkElement.getText();
                        System.out.println("linkText = " + linkText);

                        if (url != null && !url.isEmpty()) {
                            List<String> titleAndStatus = checkLinkStatus(driver, url, extractParameters(url));
                            String status = titleAndStatus.get(1);
                            System.out.println("status = " + status);
                            String title = titleAndStatus.get(0);
                            System.out.println("title = " + title);
                            String pageTitle = (status.equals("OK")) ? titleAndStatus.get(0) : "N/A";

                            writer.write(String.format("Link: %s\nTitle: %s\nStatus: %s\n\n", url, pageTitle, status));
                        }
                    }
                    BrowserUtils.sleep(1);
                    WebElement backButton = driver.findElement(By.cssSelector("ul.hmenu-visible a.hmenu-back-button"));
                    actions.moveToElement(backButton).perform();
                    actions.click(backButton).perform();
                    BrowserUtils.sleep(1);
                    if (backButton.isDisplayed()) {
                        backButton.click();
                    }
                } else if (Integer.parseInt(department.getAttribute("data-menu-id")) < 4 || Integer.parseInt(department.getAttribute("data-menu-id")) >= 27) {
                    continue;
                }
                BrowserUtils.sleep(1);
            }
            writer.close();
            System.out.println("Results saved in: " + outputFileName);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static class ExtractedParams {
        String baseUrl;
        Map<String, String> queryParams;

        public ExtractedParams(String baseUrl, Map<String, String> queryParams) {
            this.baseUrl = baseUrl;
            this.queryParams = queryParams;
        }
    }

    public static ExtractedParams extractParameters(String fullUrl) {
        try {
            String[] parts = fullUrl.split("\\?");
            String baseUrl = parts[0];

            Map<String, String> queryParams = new LinkedHashMap<>();
            if (parts.length > 1) {
                String queryString = parts[1];
                for (String param : queryString.split("&")) {
                    String[] keyValue = param.split("=", 2);
                    String key = keyValue[0];
                    String value = keyValue.length > 1 ? keyValue[1] : "";
                    queryParams.put(key, value);
                }
            }

            return new ExtractedParams(baseUrl, queryParams);
        } catch (Exception e) {
            e.printStackTrace();
            return new ExtractedParams(fullUrl, Collections.emptyMap());
        }
    }


    private static List<String> checkLinkStatus(WebDriver driver, String url, ExtractedParams extractedParams) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<String> checkLink = new ArrayList<>();
        String status;
        String pageTitle;

        js.executeScript("window.open(arguments[0], '_blank');", url);
        pageTitle = driver.getTitle();
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));

        Set<org.openqa.selenium.Cookie> seleniumCookies = driver.manage().getCookies();
        Map<String, String> cookies = new HashMap<>();
        for (org.openqa.selenium.Cookie cookie : seleniumCookies) {
            cookies.put(cookie.getName(), cookie.getValue());
        }

        Response response = given()
                .cookies(cookies)
                .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
                        "Accept-Language", "en-US,en;q=0.9",
                        "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .queryParams(extractedParams.queryParams)
                .when()
                .get(extractedParams.baseUrl);

        int responseCode = response.statusCode();

        System.out.println("responseCode = " + responseCode);

        if (responseCode == 200) {
            status = "OK";
        } else {
            status = "Dead link";
        }

        driver.close();
        driver.switchTo().window(tabs.get(0));

        checkLink.add(pageTitle);
        checkLink.add(status);
        return checkLink;
    }
}

