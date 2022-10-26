import com.codeborne.selenide.Condition
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selectors
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$$`
import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.WebDriverRunner.setWebDriver
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.io.File
import java.time.Duration


fun main() {
    println("Hello World!")

    startHere()
}

fun startHere() {
    System.setProperty(
        "webdriver.chrome.driver",
        "C:\\Users\\konstantin\\IdeaProjects\\Vk_downloader_audiobooks_from\\chromedriver_win32\\chromedriver.exe"
    )

//    Configuration.browser = "chrome"
//    Configuration.browserBinary = "C:\\Users\\User\\IdeaProjects\\Vk_downloader_audiobooks_from\\utils\\chrlauncher-win64-stable-ungoogled\\bin\\chrome.exe"
    val options = ChromeOptions()
    options.addExtensions(File("vkdownloader.crx"))
    val webDriver: WebDriver = ChromeDriver(options)
    setWebDriver(webDriver)
    open("https://www.vk.com/")

//    Selenide.`$`(Selectors.byXpath("//*[@id="index_email"]"))
//        .shouldBe(Condition.visible, Duration.ofSeconds(10))
//        .click()

//    //*[@id="index_login"]/div/form/button[1]
    println("Press any key.")
    readLine()
    println("Thank`s")

    var downloadButtons = `$$`(Selectors.byXpath("//div[@class=\"vkd_download_all_btn\"]"))

    while (true) {
        try {
            if (downloadButtons.isNotEmpty()) {
//                downloadButtons.last().click()
                for (button in downloadButtons.chunked(5).first()) {
                    button.click()
                }

                while (true) {
                    try {
                        Selenide.`$`(Selectors.byXpath("//div[@class=\"vkd_download_all_btn vkd_downloading\"]"))
                            .should(
                                Condition.exist, Duration.ofSeconds(4)
                            )
                    } catch (e: ElementNotFound) {
                        break
                    }
                }

                downloadButtons = `$$`(Selectors.byXpath("//div[@class=\"vkd_download_all_btn\"]"))
                println("Качаем новую книгу.")
            }
        } catch (e: Exception) {

        }
//        else {
//            break
//        }
    }



    println()
}