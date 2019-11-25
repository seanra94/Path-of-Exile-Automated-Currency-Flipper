package backend.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import backend.Global;
import backend.Utility;
import backend.dataStructures.Currency;
import backend.dataStructures.Exchange;
import backend.dataStructures.ExchangeData;
import backend.dataStructures.Timer;

public class PushOffersModel2 {
	
//--------------------------------------------------------------Fields--------------------------------------------------------------//

	private static List<ExchangeData> _finalOfferList;
	private static HtmlDivision _emptyOffer;
	
	private static final String ACCOUNT_NAME = Global.getAccountName();
	private static final String EMAIL_ADDRESS = Global.getEmailAddress();
	private static final String EMAIL_PASSWORD = Global.getEmailPassword();
	private static final String LEAGUE = Global.getLeague();
	private static final String FORUM_THREAD_ID = Global.getForumThreadId();
	private static Map<String, Currency> _nameToCurrencyMap = Global.get_nameToCurrencyMap();
	private static String _forumContent;
	//private static final String FILLER = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
	
//--------------------------------------------------------------Methods--------------------------------------------------------------//

	//--------------------
	public static void pushExchanges() throws FailingHttpStatusCodeException, MalformedURLException, IOException, ParseException {

		//Global setup
		_finalOfferList = Global.get_finalOfferList();

		//Webclient setup
		WebClient webClient = new WebClient();
		webClient.getOptions().setCssEnabled(false);//if you don't need CSS
		webClient.getOptions().setJavaScriptEnabled(false);//if you don't need JS

		//Connect to log in page
		System.out.println("Loading login page...");
		HtmlPage loginPage = webClient.getPage("https://www.pathofexile.com/login");
		System.out.println("Login page loaded");

		//Log in
		HtmlTextInput emailAddressField = loginPage.getHtmlElementById("login_email");
		emailAddressField.setValueAttribute(EMAIL_ADDRESS);

		HtmlPasswordInput emailPasswordField = loginPage.getElementByName("login_password");
		emailPasswordField.setValueAttribute(EMAIL_PASSWORD);

		HtmlSubmitInput logInButton = loginPage.getHtmlElementById("login_submit");

		System.out.println("Logging in...");
		logInButton.click();
		System.out.println("Successfully logged in");

		//Get JSON and parse it
		UnexpectedPage stashPage = webClient.getPage("https://www.pathofexile.com/character-window/get-stash-items?accountName=" + ACCOUNT_NAME + "&tabIndex=0&league=" + LEAGUE + "&tabs=0");


		setJsonInfo(stashPage.getInputStream());
		setStashInfo();

		
		// TEST TEST TEST TEST TEST
		Map<Integer, Currency> _currMapWithC = Global.get_currMapNoC();
		Currency _chaosCurrency = Global.get_chaosCurrency();
		Map<Integer, Exchange> chaosMap = _chaosCurrency.get_webOffersMap();
		
		for (Entry<Integer, Currency> currencyEntry : _currMapWithC.entrySet()) {
			System.out.println();
			System.out.println("Selling: " + currencyEntry.getValue().get_name());
			for (Entry<Integer, Exchange> exchangeEntry : currencyEntry.getValue().get_webOffersMap().entrySet()) {
				System.out.println(generateContentStringPart((exchangeEntry.getValue())));
			}
		}
		for (Entry<Integer, Exchange> exchangeEntry : chaosMap.entrySet()) {
			System.out.println(generateContentStringPart((exchangeEntry.getValue())));
		}
		
		
		
		//Post to thread 
		System.out.println("Loading forum page...");   
		HtmlPage forumPage = webClient.getPage("https://www.pathofexile.com/forum/edit-thread/" + FORUM_THREAD_ID + "?history=1");
		System.out.println("Forum page loaded");

		HtmlTextArea contentArea = forumPage.getHtmlElementById("content");
		contentArea.setText("[linkItem location=\"Stash1\" league=\"Delve\" x=\"8\" y=\"0\"]~b/o 800/2 chaos");

		
		
		HtmlSubmitInput submitForumPost = forumPage.getElementByName("post_submit");
		//HtmlSubmitInput submitForumPost = forumPage.getHtmlElementById("submit");

		System.out.println("Submitting forum post...");
		submitForumPost.click();
		System.out.println("Successfully posted to forums");

		webClient.close();
		//Timer.printTimes();
	}


	//--------------------
	private static String generateContentStringPart(String stashName, int stashXPos, int stashYPos, int buyAmount, int sellAmount, String buyCurrency) {
		String content = "[linkItem location=\"" + stashName + "\" league=\"" + LEAGUE + "\" x=\"" + stashXPos + "\" y=\"" + stashYPos + "\"]" +
				System.lineSeparator() + "~b/o " + buyAmount + "/" + sellAmount + " " + buyCurrency;
				
		return content;
	}
	
	//--------------------
	private static String generateContentStringPart(Exchange aExchange) {
		ExchangeData lFinalOffer = aExchange.get_finalOffer();
		if (lFinalOffer == null) {
			//System.out.println("No final exchance for sellID = " + aExchange.get_sellId() + " and buyID = " + aExchange.get_buyId());
			return "No final exchance for sellID = " + aExchange.get_sellId() + " and buyID = " + aExchange.get_buyId();
		}
		Currency lSellCurr = aExchange.get_sellCurrency();
		
		String lContent = "[linkItem location=\"" + "Stash1" + "\" league=\"" + LEAGUE 
				+ "\" x=\"" + lSellCurr.getStashXPos() + "\" y=\"" + lSellCurr.getStashYPos() + "\"]" + System.lineSeparator() +
				 "~b/o " + lFinalOffer.get_buyValue() + "/" + lFinalOffer.get_sellValue() + " " + aExchange.get_buyCurrency().get_currTag();
		
		return lContent;
	}
	
	private static String generateEntireContentString() { //DO LATER
		return _forumContent;
		
	}

	//--------------------
	private static void setJsonInfo(InputStream jsonStream) {

		StringBuilder sb = new StringBuilder(10000);

		//Convert URL content to StringBuilder
		try {

			int data = jsonStream.read();
			while(data != -1){
				sb.append((char) data);
				data = jsonStream.read();
			}
			jsonStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			//Convert StringBuilder content to JSON
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonFile = (JSONObject) jsonParser.parse(sb.toString());
			JSONArray jsonCurrencies = (JSONArray) jsonFile.get("items");

			@SuppressWarnings("unchecked")
			ListIterator<JSONObject> currencyIterator =  jsonCurrencies.listIterator();

			JSONObject jsonCurrency;
			Currency currency;
			String currencyName;
			Long currencyCount;

			//Add JSON data to currencies
			while (currencyIterator.hasNext()) {
				jsonCurrency = currencyIterator.next();

				currencyName =  (String) jsonCurrency.get("typeLine");
				currencyCount =  (Long) jsonCurrency.get("stackSize");

				currency = _nameToCurrencyMap.get(currencyName);
				if (currency != null) {
					currency.set_stock(currencyCount);
				} else {
					System.out.println("WARNING: " + currencyName + " was found in JSON but was not found in currency list");
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	//--------------------
	private static void setStashInfo() {
		//Get stash info

		try (BufferedReader br = new BufferedReader(new FileReader(Utility.stashInfo()))) {
			String regex = "\\((.+),\\s+(.+),\\s+(.+),\\s+(.+),\\s+(.+)\\)";
			String line;
			String currencyName;
			Currency currency;
			String[] info;

			//Format: Name, Tag, StashType, XPos, YPos
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				info = new String[5];
				info = Utility.regex(regex, line, new String[5]);

				currencyName = info[0];
				currency = _nameToCurrencyMap.get(info[0]);

				if (currency != null) {
					currency.setStashInfo(info[1], info[2], Integer.valueOf(info[3]),  Integer.valueOf(info[4]));
				} else {
					System.out.println("WARNING: " + currencyName + " was found in stash text file but was not found in currency list");
				}
			}
			br.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	
	//Form
			/*
			HtmlForm exchangeForm = exchangePage.getFirstByXPath("//*[@id=\"content\"]/form");
			HtmlDivision offersDiv = exchangeForm.getFirstByXPath("//div[contains(@id, 'offers')]");
			
			//----Delete----
			System.out.println("Deleting old offers...");
			List<HtmlAnchor> deleteButtons = exchangeForm.getByXPath("//*[@id=\"offers\"]//a[contains(@class, 'button prefix secondary expand')]");
			for (HtmlAnchor button : deleteButtons) {
				try {
					button.click();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Finished deleting old offers");
			
			
			if (!Global.isDeleteOnly()) {
				//----Create 2 offers via button----
				System.out.println("Creating new empty offers...");
				HtmlAnchor _newOfferButton = exchangeForm.getFirstByXPath("//*[@id=\"content\"]//a[contains(@class, 'button small right secondary expand')]");
				for (int i = 0; i < 2; i++) {
					try {
						_newOfferButton.click();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				//----Put original 2 buttons in delete list----
				deleteButtons = exchangeForm.getByXPath("//*[@id=\"offers\"]//a[contains(@class, 'button prefix secondary expand')]");
				
				//----Create via clones----
				newOfferClonerTimer.start();
				_emptyOffer = exchangeForm.getFirstByXPath("//*[@id=\"offers\"]/div[2]");

				int size = _finalOfferList.size();
				for (int i = 0; i < size; i++) {
					HtmlDivision newEmptyOffer = (HtmlDivision) _emptyOffer.cloneNode(true);
					ExchangeData finalOffer = _finalOfferList.get(i);	
					
					_sellComboBox = newEmptyOffer.getFirstByXPath("//select[contains(@name, 'sell_currency')]");
					_sellTextFields = newEmptyOffer.getFirstByXPath("//input[contains(@name, 'sell_value')]");
					_buyComboBoxList = newEmptyOffer.getFirstByXPath("//select[contains(@name, 'buy_currency')]");
					_buyTextFields = newEmptyOffer.getFirstByXPath("//input[contains(@name, 'buy_value')]");
					
					
					_sellComboBox.setSelectedIndex(finalOffer.get_sellId());
					_sellTextFields.setValueAttribute(String.valueOf(finalOffer.get_sellValue()));
					_buyComboBoxList.setSelectedIndex(finalOffer.get_buyId());
					_buyTextFields.setValueAttribute(String.valueOf(finalOffer.get_buyValue()));
					
					offersDiv.appendChild(newEmptyOffer);	
					System.out.println("(" + i + "/" + size + ") Added!");
				}
				newOfferClonerTimer.stop();

				for (HtmlAnchor button : deleteButtons) {
					try {
						button.click();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Finished creating new empty offers");

				//----Submit offers----
				System.out.println("Submitting offers...");
				HtmlSubmitInput submitOffersButton = exchangeForm.getFirstByXPath("//*[@id=\"content\"]//input[contains(@class, 'search button')]");
				HtmlPage submitPage = submitOffersButton.click();
				System.out.println("Finished Submitting offers");
				
				//----Load submit page----
				System.out.println("Loading submit page...");
				File file = File.createTempFile("HtmlUnit", ".html");
				file.delete(); // Delete is needed, because page.save can't overwrite it
				submitPage.save(file);

				Runtime.getRuntime().exec("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe " + file);
				System.out.println("Finished loading submit page");
			}
			
			*/
	
	
}
