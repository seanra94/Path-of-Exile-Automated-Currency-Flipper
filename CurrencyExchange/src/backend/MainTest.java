package backend;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import backend.models.PushOffersModel;
import backend.models.PushOffersModel2;
import net.sourceforge.htmlunit.corejs.javascript.json.JsonParser;

public class MainTest {
	private static String LEAGUE = "Incursion";
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, ParseException {
		//PushOffersModel2.pushExchanges();
		
		System.out.println(generateContentString("Stash1", 8, 0, 200, 2, "chaos"));

	
	}
	
	private static String generateContentString(String stashName, int stashXPos, int stashYPos, int buyAmount, int sellAmount, String buyCurrency) {
		
		String content = "[linkItem location=\"" + stashName + "\" league=\"" + LEAGUE + "\" x=\"" + stashXPos + "\" y=\"" + stashYPos 
				+ "\"]~b/o " + buyAmount + "/" + sellAmount + " " + buyCurrency;
		return content;

		
	}

}
