package step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;
import utilities.ConfigurationReader;
import utilities.Driver;
import java.util.ArrayList;
import java.util.List;

public class Step_Definition {
    WebDriver driver = Driver.getChromeDriver();
    BasePage basePage = new BasePage(driver);
    WebDriverWait wait = new WebDriverWait(driver, 20);

    static List<WebElement> products_in_stock = new ArrayList<>();
    static List<String> products_in_stock_name = new ArrayList<>();
    static List<WebElement> discounted_products = new ArrayList<>();
    static List<String> discounted_products_name = new ArrayList<>();
    static List<WebElement> not_discounted_products = new ArrayList<>();
    static List<String> not_discounted_products_name = new ArrayList<>();

    @Given("Navigate to amazon web site")
    public void navigate_to_amazon_web_site() {
        driver.get(ConfigurationReader.getProperty("web-page-url"));
    }

    @Then("Check the homepage")
    public void check_the_homepage() {
        Assert.assertEquals("https://www.amazon.com/", driver.getCurrentUrl());
        Assert.assertTrue(driver.getTitle().toLowerCase().contains("amazon"));
    }

    @When("Search by word {string}")
    public void search_by_word(String string) {
        wait.until(ExpectedConditions.visibilityOf(basePage.deliverLocation));
        //Selected "UK" as delivery address
        basePage.deliverLocation.click();
        Assert.assertTrue(basePage.chooseYourLocationPopup.isDisplayed());
        Select dropdown = new Select(driver.findElement(By.xpath("//select[@id='GLUXCountryList']")));
        List<WebElement> optGroups = dropdown.getWrappedElement().findElements(By.tagName("optgroup"));
        List<WebElement> optionsInFirstOptgroup = optGroups.get(0).findElements(By.tagName("option"));
        optionsInFirstOptgroup.get(6).click();
        basePage.doneButton.click();
        wait.until(ExpectedConditions.invisibilityOf(basePage.chooseYourLocationPopup));

        basePage.searchBox.sendKeys(string + Keys.ESCAPE);
        basePage.searchButton.click();

        wait.until(ExpectedConditions.visibilityOf(basePage.results));
    }

    @When("Add the non-discounted products in the stock on the first page of the search results to the cart")
    public void add_the_non_discounted_products_in_the_stock_on_the_first_page_of_the_search_results_to_the_cart() {
        products_in_stock = driver.findElements(By.xpath(
                "//span[@data-component-type='s-search-results']//button[.='Add to cart']/../../../../../../../../../../../../../../.."
        ));

        for (int i = 0; i < products_in_stock.size(); i++) {
            products_in_stock_name.add(products_in_stock.get(i).findElement(By.xpath(".//div[@data-cy='title-recipe']//h2/span")).getText());
        }

        discounted_products = driver.findElements(By.xpath("//span[@class='a-price']//following-sibling::div[1]"));
        for (int i = 0; i < discounted_products.size(); i++) {
            discounted_products_name.add(discounted_products.get(i).findElement(By.xpath("./../../../../../../../../../../..//div[@data-cy='title-recipe']//h2/span")).getText());
        }

        for (int i = 0; i < products_in_stock.size(); i++) {
            if (!discounted_products_name.contains(products_in_stock_name.get(i))) {
                not_discounted_products_name.add(products_in_stock_name.get(i).substring(0, 80));
                // Getting substring(0,80) to compare its name on the cart
                not_discounted_products.add(products_in_stock.get(i));
            }
        }

        for (int i = 0; i < not_discounted_products.size(); i++) {
            not_discounted_products.get(i).findElement(By.xpath(".//button[.='Add to cart']")).click();
            wait.until(ExpectedConditions.attributeToBe(not_discounted_products.get(i).findElement(By.xpath(".//div[@aria-live='polite']")), "style", ""));
            wait.until(ExpectedConditions.attributeToBe(not_discounted_products.get(i).findElement(By.xpath(".//div[@aria-live='polite']")), "style", "display: none;"));
        }
    }

    @When("Go to cart")
    public void go_to_cart() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", basePage.cartButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", basePage.cartButton);
        //basePage.cartButton.click();
    }

    @Then("Verify products added correctly")
    public void verify_products_added_correctly() {
        List<WebElement> products_on_cart = driver.findElements(By.xpath("//div[@data-name='Active Items']//span[@class='a-truncate-cut']"));
        List<String> products_names_on_cart = new ArrayList<>();

        for (int i = 0; i < products_on_cart.size(); i++) {
            products_names_on_cart.add(products_on_cart.get(i).getText().substring(0, 100));
            /*Using substring(0,100) is to compare with not_discounted_products_name list
            Normally need to use same substring(0,100) but there is some different situation.
            Explained in line 123
             */
        }

        System.out.println("products_names_on_cart.size() = " + products_names_on_cart.size());
        System.out.println("not_discounted_products_name.size() = " + not_discounted_products_name.size());

        System.out.println("products_names_on_cart = " + products_names_on_cart);
        System.out.println("not_discounted_products_name = " + not_discounted_products_name);

        //Assert.assertTrue(products_names_on_cart.containsAll(not_discounted_products_name));
        /*Normally containsAll method should work, but there is some different products
        One of the products starts with "Laptop"
        Same product's name starts with "URAO Laptop" on the cart
        "URAO" is its brand
        */
        int count;
        for (int i = 0; i < not_discounted_products_name.size(); i++) {
            count=0;
            for (int j = 0; j < products_names_on_cart.size(); j++) {
                if(products_names_on_cart.get(j).contains(not_discounted_products_name.get(i))){
                    count++;
                }
            }
            Assert.assertEquals(1,count);
        }
    }


}
