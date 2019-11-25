package backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import backend.Global;
import backend.dataStructures.ExchangeData;

public class PushOffersModel {
	
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String POST_URL_XYZ = "http://currency.poe.trade/shop?league=" + Global.getLeague();
	private static final String POST_BASE_XYZ = "league=" + Global.getLeague() + "&apikey=" + Global.getApikey();

	private static final String POST_URL_OFFICIAL = "https://www.pathofexile.com/forum/edit-thread/2196329?history=1";
	private static final String POST_BASE_OFFICIAL = 
			"forum_thread=d8a474d513d776adb413e045a9cc85e4445241f04cae86bae29764914c343a046becbccee626926994bd3fa588e2459b683932f0f6626810cf7b738f2cc11c4e&"
			+ "title=Test55&content=[linkItem+location=\"Stash1\"+league=\"Incursion\"+x=\"8\"+y=\"0\"]~b/o+400/2+chaos&notify_owner=0&submit=Submit";
	
	private static String _entirePost = "" + POST_BASE_XYZ;
	private static List<ExchangeData> _finalOfferList;
	private static Map<Integer, String> _currencyNameMap;
	
	
	public PushOffersModel(boolean deleteOffers) {
		if (deleteOffers) {
			deleteOffers();
		} else {
			pushOffers();
		}
	}
	
	
	public PushOffersModel() {}


	private void deleteOffers() {
		System.out.println("Clearing past trades...");
		try {
			sendPOST(POST_BASE_XYZ, POST_URL_XYZ);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR: Failed to delete past trades");
		}
		System.out.println("Finished clearing past trades");
	}
	
	
	public void pushOffersOfficial() {
		try {
			sendPOST(POST_BASE_OFFICIAL, POST_URL_OFFICIAL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void pushOffers() {
		deleteOffers();
		_finalOfferList = Global.get_finalOfferList();
		_currencyNameMap = Global.get_currencyNameMap();

		
		String sellName;
		String buyName;
		for (ExchangeData exchangeData : _finalOfferList) {
			sellName = _currencyNameMap.get(exchangeData.get_sellId());
			buyName = _currencyNameMap.get(exchangeData.get_buyId());
			_entirePost+= "&sell_currency=" + sellName + "&sell_value=" + exchangeData.get_sellValue() + "&buy_value=" + exchangeData.get_buyValue() + "&buy_currency=" + buyName;
		}
		
		System.out.println("Posting new trades...");
		System.out.println("Post info: " + _entirePost);
		try {
			sendPOST(_entirePost, POST_URL_XYZ);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR: Failed to post new trades");
		}
		System.out.println("Finished Posting new trades");
	}
	
	private void sendPOST(String postParams, String postUrl) throws IOException {
		URL url = new URL(postUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", USER_AGENT);

		// For POST only - START
		connection.setDoOutput(true);
		OutputStream os = connection.getOutputStream();
		os.write(postParams.getBytes());
		os.flush();
		os.close();
		// For POST only - END

		int responseCode = connection.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println(response.toString());
		} else {
			System.out.println("POST request failed");
		}
		
		connection.disconnect();
	}

}



