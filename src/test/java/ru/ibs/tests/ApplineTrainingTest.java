package ru.ibs.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class ApplineTrainingTest {
    WebDriver driver;
    String baseUrl;
    private WebDriverWait wait;

    @Before
    public void beforeTest(){
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        baseUrl = "http://training.appline.ru/user/login";
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window(). maximize();
        wait = new WebDriverWait(driver, 10, 1000);
        driver.get(baseUrl);
    }

    @Test
    public void test(){
        //заполнить форму авторизации
        String fieldUserNameXPath = "//input[@name='_username']";
        String fieldPasswordXPath = "//input[@name='_password']";
        String comeButtonXPath = "//button[@type='submit']";
        WebElement comeButton = driver.findElement(By.xpath(comeButtonXPath));
        fillInputField(driver.findElement(By.xpath(fieldUserNameXPath)), "Taraskina Valeriya");
        fillInputField(driver.findElement(By.xpath(fieldPasswordXPath)), "testing");
        comeButton.click();

        //проверка открытия страницы "Панель быстрого запуска"
        String pageTitleXPath = "//h1[@class ='oro-subtitle']";
        waitUtilElementToBeVisible(By.xpath(pageTitleXPath));
        WebElement mainPageTitle = driver.findElement(By.xpath(pageTitleXPath));
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому","Панель быстрого запуска", mainPageTitle.getText());

        //выполнить переходна на страницу "Командировки"
        String titleExpensesXPath = "//a[@class='unclickable']/span[contains(text(), 'Расходы')]";
        WebElement titleExpenses = driver.findElement(By.xpath(titleExpensesXPath));
        titleExpenses.click();
        String  businessTripsXPath = "//li[@data-route='crm_business_trip_index']/a/span[contains(text(),'Командировки')]";
        WebElement businessTrips = driver.findElement(By.xpath(businessTripsXPath));
        businessTrips.click();

        // выполнить переходна на страницу "создать командировку
        String createBusinessTripButtonXPath = "//a[contains(@class,'btn back icons-holder-text')]";
        WebElement createBusinessTripButton = driver.findElement(By.xpath(createBusinessTripButtonXPath));
        createBusinessTripButton.click();
        String pageCreatebusinessTripsXPath = "//h1[@class='user-name']";
        WebElement pageCreatebusinessTrips = driver.findElement(By.xpath(pageCreatebusinessTripsXPath));
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому","Создать командировку", pageCreatebusinessTrips.getText());

        //выбрать подразделение
        WebElement subdivisionDropdown = driver.findElement(By.name("crm_business_trip[businessUnit]"));
        Select subdivisionSelect = new Select(subdivisionDropdown);
        subdivisionSelect.selectByVisibleText("Отдел внутренней разработки");

        // заполнить поле "Принимающая организация"
        WebElement openListButton = driver.findElement(By.id("company-selector-show"));
        openListButton.click();
        WebElement chooseOrganization = driver.findElement(By.xpath("//span[@class='select2-chosen']"));
        chooseOrganization.click();
        WebElement fieldcChooseOrganization = driver.findElement(By.xpath("//input[contains(@class,'select2-input')]"));
        fillInputField(fieldcChooseOrganization,"Аплана");
        fieldcChooseOrganization.clear();
        fieldcChooseOrganization.sendKeys("Аплана");
        String chooseOrganizationResultXPath = "//span[contains(text(),'Аплана')]";
        WebElement chooseOrganizationResult = driver.findElement(By.xpath(chooseOrganizationResultXPath));
        waitUtilElementToBeVisible(By.xpath(chooseOrganizationResultXPath));
        chooseOrganizationResult.click();

        // выбрать чекбокс
        WebElement checkboxTicket = driver.findElement(By.xpath("//input[@data-name='field__1']"));
        checkboxTicket.click();

        // заполнить поля выезда и заезда
        WebElement fieldDepartureCity = driver.findElement(By.xpath("//input[@data-name='field__departure-city']"));
        fillInputField(fieldDepartureCity,"Россия, Николаевск");

        WebElement fieldArrivalCity = driver.findElement(By.xpath("//input[@data-name='field__arrival-city']"));
        fillInputField(fieldArrivalCity, "Тайланд, Бангкок");

        //заполнить поля даты
        WebElement fieldDatesDeparture = driver.findElement(By.xpath("//input[contains(@name, 'date_selector_crm_business_trip_departureDatePlan')]"));
        fillInputField(fieldDatesDeparture, "22.04.2022");
        driver.findElement(By.xpath("//html")).click();

        WebElement fieldDatesReturn = driver.findElement(By.xpath("//input[contains(@name, 'date_selector_crm_business_trip_returnDatePlan')]"));
        fillInputField(fieldDatesReturn, "17.08.2023");
        driver.findElement(By.xpath("//html")).click();

        // нажать на кнопку "Сохранить и закрыть"
        WebElement buttonSaveClose = driver.findElement(By.xpath("//button[@class='btn btn-success action-button']"));
        buttonSaveClose.click();

        //проверка ошибки на странице
        WebElement validationError = driver.findElement(By.xpath("//span[@class='validation-failed']"));
        Assert.assertEquals("Проверка ошибки у поля не была пройдена",
                "Список командируемых сотрудников не может быть пустым", validationError.getText());



//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    @After
    public void afterTest(){
        driver.quit();
    }

    /**
     * Скрол до элемента на js коде
     *
     * @param element - веб элемент до которого нужно проскролить
     */
    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Явное ожидание того что элемент станет кликабельный
     *
     * @param element - веб элемент, который ждём когда он станет кликабельным
     */
    private void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Явное ожидание того что элемент станет видемым
     *
     * @param locator - локатор веб элемента, который мы ожидаем найти и который виден на странице
     */
    private void waitUtilElementToBeVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


    /**
     * Заполнение поля значением
     *
     * @param element - веб элемент - поле, которое заполняем
     * @param value - значение коотрым заполняем поле
     */
    private void fillInputField(WebElement element, String value){
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.click();
        element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }
}

