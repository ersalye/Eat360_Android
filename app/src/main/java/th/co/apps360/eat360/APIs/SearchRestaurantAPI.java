package th.co.apps360.eat360.APIs;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 11/28/16.
 */

public class SearchRestaurantAPI extends AsyncTask<String, Void, String> {

    private Context mContext;
    private ResultCallback resultCallback;
    private String searchText;
    private String searchLang;
    private String latitude;
    private String longitude;
    private String radius;
    private String categoryIDs;
    private String mealIDs;
    private String cuisineIDs;
    private String onlyIDs;
//    private String foodCurrency;

    public interface ResultCallback {
        void searchResultCallback(String jsonStringResult);
    }

    // SearchText, SearchLang, Latitude, Longitude, Radius, CuisineIDs, MealIDs, CategoryIDs, FoodCurrency
    public SearchRestaurantAPI(Context context, String searchText, String searchLang,
                         String latitude, String longitude, String radius,
                         String categoryIDs, String mealIDs, String cuisineIDs, String onlyIDs) {
        mContext = context;
        resultCallback = (ResultCallback)mContext;
        this.searchText = searchText;
        this.searchLang = searchLang;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.categoryIDs = categoryIDs;
        this.mealIDs = mealIDs;
        this.cuisineIDs = cuisineIDs;
        this.onlyIDs = onlyIDs;
//        this.foodCurrency = foodCurrency;
    }

    @Override
    protected String doInBackground(String... url) {
        StringBuilder stringResult = new StringBuilder();

        try {
            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

            String apiUrl = "";
            if (url != null && url.length > 0) {
                if(url[0] == null)
                    apiUrl = new ConfigURL(mContext).searchRestaurantURL;
                else
                    apiUrl = url[0];
            }
            else
                apiUrl = new ConfigURL(mContext).searchRestaurantURL;

            HttpParams httpParams = new BasicHttpParams();
            int socketTimeoutConnection = 10000;
            HttpConnectionParams.setSoTimeout(httpParams,
                    socketTimeoutConnection);
            HttpPost httppost = new HttpPost(apiUrl);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("SearchText", searchText));
            nameValuePairs.add(new BasicNameValuePair("SearchLang", searchLang));
            nameValuePairs.add(new BasicNameValuePair("Latitude", latitude));
            nameValuePairs.add(new BasicNameValuePair("Longitude", longitude));
            nameValuePairs.add(new BasicNameValuePair("Radius", radius));
            nameValuePairs.add(new BasicNameValuePair("CategoryIDs", categoryIDs));
            nameValuePairs.add(new BasicNameValuePair("MealIDs", mealIDs));
            nameValuePairs.add(new BasicNameValuePair("CuisineIDs", cuisineIDs));
            nameValuePairs.add(new BasicNameValuePair("OnlyIDs", onlyIDs));
            nameValuePairs.add(new BasicNameValuePair("PageID", "1"));
//            nameValuePairs.add(new BasicNameValuePair("FoodCurrency", foodCurrency));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httppost);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line;
            while ((line = buffer.readLine()) != null) {
                stringResult.append(line);
            }

        } catch (SocketTimeoutException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
            stringResult.append(Utils.TIMEOUT_ERROR);
        } catch (IOException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
            stringResult.append(Utils.CONNECTION_ERROR);
        }

        return stringResult.toString();
    }

    @Override
    protected void onPostExecute(String jsonString) {
        resultCallback.searchResultCallback(jsonString);
        Utils.setDebug("json SearchRestaurantAPI", jsonString);
    }
}
