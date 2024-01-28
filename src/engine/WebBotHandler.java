package engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;	
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxOptions;



// siirretään pääasialliset toiminnot ja driverin luonti omaan luokkaansa
public class WebBotHandler {
    public WebDriver driver = null;
    private boolean isSetHeadless;
    private FirefoxOptions options;

    public WebBotHandler(boolean isHeadless){
        options = new FirefoxOptions();
        options.setHeadless(isHeadless);
        driver = new FirefoxDriver(options);
    }
    

    public void login() throws InterruptedException {
        log("TRYING TO LOG IN TO REPORTRONIC.");
        Thread.sleep(2000);
        Alert alert = driver.switchTo().alert();
        driver.switchTo().alert().sendKeys("jusju" + Keys.TAB + "Turku2020");
        driver.switchTo().alert().accept();
    }

    public void selectTyoajat() throws InterruptedException {
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        log("WAITING FOR ELEMENT WITH ID OF uiMenuIcon uiMenuWork");
        odotaEttaOnKlikattavissa("uiMenuIcon uiMenuWork");
        log("TRYING TO FIND ELEMENT WITH ID OF CtlMenu1_CtlNavBarMain1_ctlNavBarWorkT1_lnkSelaaTyoaika");
        klikkaaTata("uiMenuIcon uiMenuWork");
    }

    public void selectNaytaTyoaikailmoituksia() throws InterruptedException {
        log("CHECKING IF MONTHLY SUBMISSION IS ALREADY DONE FOR THIS MONTH.");
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        log("WAITING FOR ELEMENT WITH ID OF CtlMenu1_CtlNavBarMain1_ctlNavBarWorkT1_lnkSelaaTyoaika");
        odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTimetd2");

        /*
         * try { Thread.sleep(2000); } catch (InterruptedException e) {
         * e.printStackTrace(); } WebElement element = driver.findElement(By.id(
         * "CtlMenu1_CtlNavBarMain1_ctlNavBarWorkT1_lnkSelaaTyoaika")); Actions
         * action = new Actions(driver);
         * action.moveToElement(element).build().perform();
         */
        log("TRYING TO FIND ELEMENT WITH ID OF prlWTEP_uwtWorkTimetd2");
        driver.findElement(By.id("prlWTEP_uwtWorkTimetd2")).click();
    }

    public void clickTyoaikailmoituksetSelectKaikki() {
        odotaEttaOnKlikattavissa("prlWorkTimeAnnouncementPage_uwtWorkTime__ctl2_cboFilter");
        Select dropdown = new Select(driver.findElement(By
                .id("prlWorkTimeAnnouncementPage_uwtWorkTime__ctl2_cboFilter")));
        dropdown.selectByValue("0");

    }

    public void clickHae() {
        odotaEttaOnKlikattavissa("prlWorkTimeAnnouncementPage_uwtWorkTime__ctl2_rlbSearch");
        klikkaaTata("prlWorkTimeAnnouncementPage_uwtWorkTime__ctl2_rlbSearch");

        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
    }

    public boolean onkoTamaKuukausiLahettamatta() {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            String dateFromCalendar = cal.get(Calendar.DATE) + "."
                    + (cal.get(Calendar.MONTH) + 1) + "."
                    + cal.get(Calendar.YEAR);
            log("LUULLAAN TIETOKONEEN KELLOSTA, ETTÄ TÄNÄÄN ON: " + dateFromCalendar);
            //dateFromCalendar = "30.11.2018";
            String foundText = driver.findElement(
                    By.xpath(".//*[contains(text(), '" + dateFromCalendar
                            + "')]")).getText();
            log("LOYTYI SAMA PÄIVÄ JO WWW SIVULTA; ELI EI KANNATTAISI LÄHETTÄÄ UUDESTAAN: " + foundText);
            
            // if elsestä eroon tällä?
            return foundText.length() > 0 ? true : false;
            
            /* if (foundText.length() > 0) {
                return true;
            } else {
                return false;
            } */
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public void lahetaKuukausi() {
        String tyoajat = "CtlMenu1_CtlNavBarMain1_ctlNavBarWorkT1_lnkSelaaTyoaika";
        log("ODOTETAAN Ty�ajat N�KYVIIN: " + tyoajat);
        odotaEttaOnKlikattavissa(tyoajat);
        log("KLIKATAAN Ty�ajat LINKKI�: " + tyoajat);
        klikkaaTata(tyoajat);

        String lahetaHyvaksyttavaksi = "prlWTEP_uwtWorkTimetd2";
        log("ODOTETAAN L�het� hyv�ksytt�v�ksi VALIKOSTA: "
                + lahetaHyvaksyttavaksi);
        odotaEttaOnKlikattavissa(lahetaHyvaksyttavaksi);
        log("KLIKATAAN L�het� hyv�ksytt�v�ksi VALIKKOLINKKI�: "
                + lahetaHyvaksyttavaksi);
        klikkaaTata(lahetaHyvaksyttavaksi);

        String teeIlmoitus = "prlWorkTimeAnnouncementPage_uwtWorkTime__ctl2_lnkTeeIlmoitus";
        log("ODOTETAAN Tee ilmoitus VALIKOSTA: " + lahetaHyvaksyttavaksi);
        odotaEttaOnKlikattavissa(teeIlmoitus);
        log("KLIKATAAN Tee ilmoitus VALIKKOLINKKI�: " + teeIlmoitus);
        klikkaaTata(teeIlmoitus);

        String seuraava = "prlWorkTimeAnnouncementPage_uwtWorkTime__ctl2_rlbNext";
        log("ODOTETAAN Seuraava: " + seuraava);
        odotaEttaOnKlikattavissa(seuraava);
        log("KLIKATAAN Seuraava PAINIKETTA: " + seuraava);
        klikkaaTata(seuraava);

        String laheta = "prlWorkTimeAnnouncementPage_uwtWorkTime__ctl2_rlbSave";
        log("ODOTETAAN Laheta: " + laheta);
        odotaEttaOnKlikattavissa(laheta);
        log("KLIKATAAN Seuraava PAINIKETTA: " + laheta);
        klikkaaTata(laheta);
    }

    public void klikkaaLisaaTyoaika() throws InterruptedException {
		log("ODOTETAAN ETT� Lis�� ty�aika TULEE KLIKATTAVAKSI.");
		// driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		log("WAITING FOR ELEMENT WITH ID OF CtlMenu1_CtlNavBarMain1_ctlNavBarWorkT1_lnkSelaaTyoaika");
		odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTimetd1");

		/*
		 * try { Thread.sleep(2000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } WebElement element = driver.findElement(By.id(
		 * "CtlMenu1_CtlNavBarMain1_ctlNavBarWorkT1_lnkSelaaTyoaika")); Actions
		 * action = new Actions(driver);
		 * action.moveToElement(element).build().perform();
		 */

		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		log("KLIKATAAN Lis�� ty�aika");
		driver.findElement(By.id("prlWTEP_uwtWorkTimetd1")).click();
    }
    
    // varmistetaan, että ollaan päivänäkymässä
	public void selectPaivaNakyma () throws InterruptedException {
		log("WAITING FOR ELEMENT WITH ID OF prlWTEPuwtWorkTimectl1CtlWorkTimeEditViewSelector1UWTS1_1");
		odotaEttaOnKlikattavissa("prlWTEPuwtWorkTimectl1CtlWorkTimeEditViewSelector1UWTS1_1");
		Thread.sleep(2000);
		log("TRYING TO FIND ELEMENT WITH ID OF prlWTEPuwtWorkTimectl1CtlWorkTimeEditViewSelector1UWTS1_1");
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		klikkaaTata("prlWTEPuwtWorkTimectl1CtlWorkTimeEditViewSelector1UWTS1_1");
		Thread.sleep(2000);
    }

    public void syotaTunnit1(String tunnitPaivalta) throws InterruptedException {
		log("KLIKATAAN Sy�tet��n ty�aika ensimm�iseen projektiin");
		WebElement tyonKestoKentta = driver.findElement(By.id("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask1_txtDuration"));
		for (int i = 0; i < 8; i++) {
			tyonKestoKentta.sendKeys(Keys.BACK_SPACE);
		}
		
		tyonKestoKentta.sendKeys(tunnitPaivalta);				
    }

    public void syotaTunnit2(String tunnitPaivalta) throws InterruptedException {
		log("KLIKATAAN Syotetaan tyoaika toiseen projektiin");
		odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask2_txtDuration");
		WebElement tyonKestoKentta = driver.findElement(By.id("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask2_txtDuration"));
		for (int i = 0; i < 8; i++) {
			tyonKestoKentta.sendKeys(Keys.BACK_SPACE);
		}
		
		tyonKestoKentta.sendKeys(tunnitPaivalta);		
		log("SYOTETAAN TYOTEHTAVAN KUVAUS");
		odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask2_txtDescription1");
		WebElement tyonKuvaus = driver.findElement(By.id("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask2_txtDescription1"));
		
		for (int i = 0; i < 50; i++) {
			tyonKuvaus.sendKeys(Keys.BACK_SPACE);			
		}
		
		tyonKuvaus.sendKeys("Toiden jarjestelya.");
		Thread.sleep(10000);
	}
    
    public void klikkaaKustannuspaikka1(String kustannusPaikkaValue) throws InterruptedException {
		log("ODOTETAAN CHECKBOXIA");
		odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTime__ctl1_chkRemoveBreakTime");		
		String check_remove_break_time_id = "prlWTEP_uwtWorkTime__ctl1_chkRemoveBreakTime";
        driver.findElement(By.id(check_remove_break_time_id)).click();	
		log("ODOTETAAN PROJEKTIEN ALASVETOLISTAA");
		odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask1_cboProject");
		log("KLIKATAAN PERUSKUSTANNUSPAIKKA");
		Select dropdown = new Select(driver.findElement(By
				.id("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask1_cboProject")));
		dropdown.selectByValue(kustannusPaikkaValue);
    }
    
    public void klikkaaKustannuspaikka2(String kustannusPaikkaValue) throws InterruptedException {
		log("ODOTETAAN PROJEKTIEN ALASVETOLISTAA");
		odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask2_cboProject");
		log("KLIKATAAN ALASVETOLISTASTA OSAAMISEN PELIMERKIT");
		Select dropdown = new Select(driver.findElement(By
				.id("prlWTEP_uwtWorkTime__ctl1_ctlWorkTimeTask2_cboProject")));
		dropdown.selectByValue(kustannusPaikkaValue);
    }

    public void klikkaaLaheta() {
		log("ODOTETAAN Ty�aikojen l�hetys n�kyviin");
		odotaEttaOnKlikattavissa("prlWTEP_uwtWorkTime__ctl1_rlbSave");
		log("KLIKATAAN TY�AIKOJEN L�HETYS");
		driver.findElement(By.id("prlWTEP_uwtWorkTime__ctl1_rlbSave")).click();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
	}

    public boolean tarkista0030() throws InterruptedException{
        scrollUpOnPage();
		selectTyoajat();
		
		try {
			String errorTime = "00:30";
			String foundText = driver.findElement(
					By.xpath(".//*[contains(text(), '" + errorTime
							+ "')]")).getText();
			log("LOYTYI ERROR AIKA: " + foundText);
			
			// if elsestä eroon
			return foundText.length() > 0 ? true : false;
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}

	
    }

    /* päästään tällä alertista eroon joka syntyy 'lisää työaika' napin
    painamisen jälkeen joskus.
     */
    public void clickCancelOnAlert() throws InterruptedException {
        try {
            klikkaaTata("prlWTEP_ctlWorkTimeDefaultProjectAllocationDialog1_rjqWindow1_rlbCancel");
            log("PÄÄSTIIN ALERTISTA EROON");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    /* tarkistetaan onko häiritsevä alertti löytynyt domista  */
    
    public boolean isAlertPresent() throws InterruptedException {
        try {
            driver.findElement(By.id("prlWTEP_ctlWorkTimeDefaultProjectAllocationDialog1_rjqWindow1_rlbCancel"));
            log("ALERTTI LÖYTYI");
            return true;
        } catch(NoSuchElementException e) {
            log("Ei alerttia, jatketaan");
            return false;
        }
    }

    public void scrollUpOnPage() {
        log("SCROLLING UP ON PAGE UNTIL ELEMENT IS FOUND");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 1000)");
    }

    public void odotaEttaOnKlikattavissa(String element) {
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(By.id(element)));
    }

    public void log(String message) {
        System.out.println(currentTimestamp() + ": " + message);
    }

    public void klikkaaTata(String element) {
        driver.findElement(By.id(element)).click();
    }

    public void otaScreenShot() {
        log("OTETAAN SCREENSHOT");
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere
        try {
            FileUtils.copyFile(scrFile, new File("c:\\tmp\\screenshot.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log("LAHETETAAN SCREENSHOT EMAILILLA");
        boolean shouldSendScreenshot = true;
 
    }

    public static String currentTimestamp() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM);
        return f.format(c.getTime());
    }

    class ProxyPrintStream extends PrintStream{    
        private PrintStream fileStream = null;
        private PrintStream originalPrintStream = null;
        public ProxyPrintStream(PrintStream out, String FilePath) {
            super(out);
            originalPrintStream = out;
             try {
                 FileOutputStream fout = new FileOutputStream(FilePath,true);
                 fileStream = new PrintStream(fout);
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            }
        }    
        public void print(final String str) {
            originalPrintStream.print(str);
            fileStream.print(str);
        }
        public void println(final String str) {
            originalPrintStream.println(str);
            fileStream.println(str);
        }        
    }
}