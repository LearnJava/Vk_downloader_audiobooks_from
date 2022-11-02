import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selectors.byId
import com.codeborne.selenide.Selenide.*
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.WebDriverRunner.getWebDriver
import com.codeborne.selenide.WebDriverRunner.setWebDriver
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.interactions.Actions
import java.io.File
import java.time.Duration


fun main() {
    println("Hello World!")

    startHere()
}

fun startHere() {
    firstConfiguration()
    open("https://www.vk.com/")

    checkAuthenticationAndLoginIfNeed()

    open("https://vk.com/best_audiobooks")

    //Ищем иконку поиска с лупой и кликаем по ней

    for (title in File("titles.txt").readLines()) {
        activateFindField()
        sendSearchQuery(title)

        val downloadButtons: List<SelenideElement>
        try {
            downloadButtons = `$$`(Selectors.byXpath("//div[@id='page_search_posts']//div[@class=\"vkd_download_all_btn\"]")).shouldBe(
                CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(4)
            )
        } catch (e: com.codeborne.selenide.ex.ElementNotFound) {
            continue
        }


        for (button in downloadButtons) {
            val actions = Actions(getWebDriver())
            actions.moveToElement(button).build().perform()

            val ex: JavascriptExecutor = getWebDriver() as JavascriptExecutor
            ex.executeScript("arguments[0].click()", button)
        }

        while (true) {
            try {
                `$`(Selectors.byXpath("//div[@class=\"vkd_download_all_btn vkd_downloading\"]"))
                    .should(
                        Condition.exist, Duration.ofSeconds(4)
                    )

            } catch (e: ElementNotFound) {
                break
            }
        }
    }

    println()
}

private fun sendSearchQuery(queryString: String) {
    //Вводим поисковый текст в поле и жмём Enter
    val searchElem =
        `$`(byId("wall_search")).shouldBe(Condition.appear, Duration.ofSeconds(5))
    searchElem.setValue(queryString).shouldBe(Condition.appear)
    `$`(Selectors.byXpath("//div[@class='ui_search_reset_close']")).shouldBe(
        Condition.appear,
        Duration.ofSeconds(5)
    )
    searchElem.pressEnter()
}

private fun activateFindField() {
    `$`(Selectors.byXpath("//*[@id=\"wall_tabs\"]/li[@class='ui_tab_search_wrap']/a"))
        .shouldBe(Condition.appear, Duration.ofSeconds(5))
        .pressEnter()
}

private fun firstConfiguration() {
    System.setProperty(
        "webdriver.chrome.driver",
        "chromedriver.exe"
    )
//    Configuration.browser = "chrome"
//    Configuration.browserBinary = "C:\\Users\\User\\IdeaProjects\\Vk_downloader_audiobooks_from\\utils\\chrlauncher-win64-stable-ungoogled\\bin\\chrome.exe"
    val chromeOptions = ChromeOptions()
    chromeOptions.addArguments("--user-data-dir=C:\\Users\\konstantin\\AppData\\Local\\Google\\Chrome\\User Data")
    chromeOptions.addArguments("--profile-directory=Profile 1")
    chromeOptions.addExtensions(File("vkdownloader.crx"))
    val webDriver: WebDriver = ChromeDriver(chromeOptions)
    setWebDriver(webDriver)
}

fun checkAuthenticationAndLoginIfNeed() {
    if (!`$`(byId("top_profile_link")).exists()) {
        //login
        `$`(byId("index_email")).setValue("79184509096").pressEnter()
        `$`(Selectors.byXpath("//input[@name='password']")).shouldBe(
            Condition.appear,
            Duration.ofSeconds(10)
        ).setValue("Itisagooddaytofun232323").pressEnter()
    }
}