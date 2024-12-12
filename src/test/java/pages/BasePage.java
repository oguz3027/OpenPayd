package pages;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BasePage {

    WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@role='searchbox']")
    public WebElement searchBox;

    @FindBy(id = "nav-global-location-popover-link")
    public WebElement deliverLocation;

    @FindBy(className = "a-popover-wrapper")
    public WebElement chooseYourLocationPopup;

    @FindBy(name = "glowDoneButton")
    public WebElement doneButton;

    @FindBy(id = "nav-search-submit-button")
    public WebElement searchButton;

    @FindBy(id = "nav-cart")
    public WebElement cartButton;

    @FindBy(xpath = "//span[@data-component-type='s-search-results']")
    public WebElement results;

    @FindBy(id = "nav-hamburger-menu")
    public WebElement allDropdownMenu;

    @FindBy(xpath = "(//div[.='Shop by Department']/../..//a[@aria-label='See all'])[1]")
    public WebElement seeAllButtonForShopByDepartment;

}
