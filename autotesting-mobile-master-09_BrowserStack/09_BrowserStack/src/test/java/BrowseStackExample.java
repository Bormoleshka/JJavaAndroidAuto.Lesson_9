import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import config.Config;
import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

public class BrowseStackExample {
    public IOSDriver<IOSElement> driver;
    
    @BeforeMethod
    public void beforeTest() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        
        // Set your access credentials
        caps.setCapability("browserstack.user", Config.getValue("loginBrowserStack"));
        caps.setCapability("browserstack.key", Config.getValue("keyBrowserStack"));
        
        // Set URL of the application under test
        caps.setCapability("app", "bs://444bd0308813ae0dc236f8cd461c02d3afa7901d");
        
        // Specify device and os_version for testing
        caps.setCapability("device", "iPhone 11 Pro");
        caps.setCapability("os_version", "13");
        
        // Set other BrowserStack capabilities
        caps.setCapability("project", "First Java Project");
        caps.setCapability("build", "Java iOS");
        caps.setCapability("name", "first_test");
        
        driver = new IOSDriver<>(new URL("http://hub-cloud.browserstack.com/wd/hub"), caps);
    }
    
    @AfterMethod
    public void afterTest(ITestResult result) {
        updateTestBrowserStack(result);
        driver.quit();
    }
    
    @Test
    public void sampleAppTest() {
        IOSElement textButton = (IOSElement) new WebDriverWait(driver, 10).until(
                ExpectedConditions.elementToBeClickable(MobileBy.AccessibilityId("Text Button")));
        textButton.click();
        
        IOSElement textInput = (IOSElement) new WebDriverWait(driver, 10).until(
                ExpectedConditions.elementToBeClickable(MobileBy.AccessibilityId("Text Input")));
        textInput.sendKeys("hello@browserstack.com");
        
        IOSElement textOutput = (IOSElement) new WebDriverWait(driver, 10).until(
                ExpectedConditions.elementToBeClickable(MobileBy.AccessibilityId("Text Output")));
        
        Assert.assertEquals(textOutput.getText(), "hello@browserstack.com");
    }
    
    public void updateTestBrowserStack(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        driver.executeScript("browserstack_executor: {\"action\": \"setSessionName\", \"arguments\": {\"name\":\"" + testName + "\" }}");
        
        if (!result.isSuccess()) {
            driver.executeScript("browserstack_executor: " +
                    "{\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"" + result.getThrowable().getLocalizedMessage() + "\"}}");
        } else {
            driver.executeScript("browserstack_executor:" +
                    " {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"\"}}");
        }
    }
}
