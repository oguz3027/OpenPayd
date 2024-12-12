package crawlerTest;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                            List<String> titleAndStatus = checkLinkStatus(driver, url);
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

    private static List<String> checkLinkStatus(WebDriver driver, String url) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<String> checkLink = new ArrayList<>();
        String status = "Dead link";
        String pageTitle = null;
        try {
            js.executeScript("window.open(arguments[0], '_blank');", url);

            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(tabs.size() - 1));

            URL link = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) link.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println("responseCode = " + responseCode);
            pageTitle = driver.getTitle();
            connection.disconnect();

            if (responseCode == 200) {
                status = "OK";
            } else {
                status = "Dead link";
            }
            driver.close();
            driver.switchTo().window(tabs.get(0));
        } catch (Exception e) {
            status = "Dead link";
            pageTitle = "Error fetching title";
        }
        checkLink.add(pageTitle);
        checkLink.add(status);
        return checkLink;
    }
}

