package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Driver {

    private Driver() {
    }

    private static WebDriver firefoxDriver;
    private static WebDriver chromeDriver;

    public static WebDriver getChromeDriver() {
        if (chromeDriver == null) {

            ChromeOptions options = new ChromeOptions();
            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("profile.default_content_setting_values.geolocation", 1);
            prefs.put("intl.accept_languages", "en-GB");
            options.setExperimentalOption("prefs", prefs);

            WebDriverManager.chromedriver().setup();
            options.setHeadless(true);
            chromeDriver = new ChromeDriver(options);
            chromeDriver.manage().window().maximize();
            chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return chromeDriver;
    }

    public static WebDriver getFirefoxDriver() {
        if (firefoxDriver == null) {
            WebDriverManager.firefoxdriver().setup();
            //firefoxDriver = new FirefoxDriver(new FirefoxOptions().setHeadless(true));
            firefoxDriver = new FirefoxDriver();
            firefoxDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return firefoxDriver;
    }

    public static void closeChromeDriver(){
        if (chromeDriver != null) {
            chromeDriver.quit();
            chromeDriver = null;
        }
    }

    public static void closeFirefoxDriver(){
        if (firefoxDriver != null) {
            firefoxDriver.close();
            firefoxDriver = null;
        }
    }



}
