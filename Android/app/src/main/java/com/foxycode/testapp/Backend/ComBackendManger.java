package com.foxycode.testapp.Backend;

import android.content.Context;
import android.util.Log;

import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Exception.MyAppException;
import com.foxycode.testapp.Model.ImageContent;
import com.foxycode.testapp.Model.TextContent;
import com.foxycode.testapp.Util.UsUtil;
import com.foxycode.testapp.Util.XString;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gve on 30/09/2014.
 */
public class ComBackendManger {

    private static final String LOG = "ComBackendManger" ;
    private static final String USER_ID = "userId";
    private static final String USER_PASSWORD = "password";
    private static final String USER_JOIN_PWD = "join_pwd";

    private static final String REST_GET_IMAGE = "/image?pic_url={pic_url}&password={password}&userId={userId}";
    private static final String PICTURE_URL = "pic_url";
    private static final String USER_MESSAGE = "message" ;
    private static final String PICTURE_ID = "pic_id";
    private static final String TEXT_ID = "text_id";
    private static final String REST_GET_IMAGECONTENT = "/imagecontent?pic_id={pic_id}&password={password}&userId={userId}";
    private static final String REST_GET_TEXTCONTENT = "/textcontent?text_id={text_id}&password={password}&userId={userId}";
    private static final String USER_TEXT = "text";
    private static final String USER_ID_CONTENT = "idOnPhone" ;
    private static final String WHERE = "where";

    String mCookie;
   // ShindigApp application;
   SharedPreferenceManager c;

    public ComBackendManger(Context context) {
         c = (SharedPreferenceManager) context.getApplicationContext();
    }

    public DefaultHttpClient getSpecialClient() {
        MyHttpClient client = new MyHttpClient(
                c);
        client.createClientConnectionManager();
        return client;
    }

    /**
     * retrieve the cookie saved ine memory
     *
     * @return
     */
    public String getCookies() {
        return c.getCookie();
    }

    /**
     * set upt he cookie in memory, must remove the first part being Set-Cookie,
     * otherwise it crashs
     *
     * @param cookies
     */
    public void setCookies(String cookies) {
        mCookie = cookies;
        c.setCookie(mCookie);
    }


    /**
     *
     * @param sUserId
     * @param sPassword
     * @return
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws com.foxycode.testapp.Exception.MyAppException
     */
    public JSONObject signUp(String sUserId, String sPassword)
            throws JSONException, IOException,
            MyAppException {
        String url = Settings.getUrl() + "/signIn";
        JSONObject jsonObject = new JSONObject();
        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        Log.d("State:", "Post to the server beginning");

        jsonObject.put(USER_ID, sUserId);
        jsonObject.put(USER_PASSWORD, sPassword);

        StringEntity se = new StringEntity(jsonObject.toString());
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(se);

        Log.d("Entity...", "Created !");
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext);
        return parseAnswer(httpResponse);
    }

    /**
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws com.foxycode.testapp.Exception.MyAppException
     *********************************************************************************/
    public JSONObject sendRegistration( String registrationId)
            throws JSONException, IOException,
            MyAppException {
        String url = Settings.getUrl() + "/registration";
        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Cookie", getCookies());
        Log.d("Entity...", "Created !");
        JSONObject registrationJson = new JSONObject();
        registrationJson.put("registration_id", registrationId);
        StringEntity se = new StringEntity(registrationJson.toString());
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext);
        return parseAnswer(httpResponse);
    }

    /**
     *
     * @param userId
     * @param password
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     * @throws com.foxycode.testapp.Exception.MyAppException
     */
    public JSONObject login(String userId, String password)
            throws IllegalStateException, IOException, JSONException,
            MyAppException {

        String urlserver = Settings.getUrl() + "/loginUser";

        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(urlserver);
        Log.d("Entity...", "Created !");

        JSONObject registrationJson = new JSONObject();
        registrationJson.put(USER_ID, userId);
        registrationJson.put(USER_PASSWORD, password);
        StringEntity se = new StringEntity(registrationJson.toString());
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext); // Execute

        setCookies(httpResponse.getFirstHeader("Set-Cookie") == null ? ""
                : httpResponse.getFirstHeader("Set-Cookie").getValue());

        return parseAnswer(httpResponse);
    }

    /**
     *
     * @param join_pwd
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     * @throws com.foxycode.testapp.Exception.MyAppException
     */
    public JSONObject postPairingPwd(String join_pwd)
            throws IllegalStateException, IOException, JSONException,
            MyAppException {

        String urlserver = Settings.getUrl() + "/postPairingPwd";

        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(urlserver);
        Log.d("Entity...", "Created !");

        JSONObject registrationJson = new JSONObject();
        registrationJson.put(USER_JOIN_PWD, join_pwd);
        registrationJson.put(USER_ID, c.getUserId());
        registrationJson.put(USER_PASSWORD, c.getPwd_ID());
        Log.d(LOG, "pairing : /" +join_pwd+"/ length"+join_pwd.length());
        StringEntity se = new StringEntity(registrationJson.toString());
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Cookie", getCookies());

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext); // Execute

        setCookies(httpResponse.getFirstHeader("Set-Cookie") == null ? ""
                : httpResponse.getFirstHeader("Set-Cookie").getValue());

        return parseAnswer(httpResponse);
    }

    public JSONObject postGcmRegistrationId(String gcmID)
            throws IllegalStateException, IOException, JSONException,
            MyAppException {

        String urlserver = Settings.getUrl() + "/postGcmId";

        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(urlserver);

        JSONObject registrationJson = new JSONObject();
        registrationJson.put("GCM_ID", gcmID);
        httpPost.setHeader("Cookie", getCookies());
        StringEntity se = new StringEntity(registrationJson.toString());
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext); // Execute

        setCookies(httpResponse.getFirstHeader("Set-Cookie") == null ? ""
                : httpResponse.getFirstHeader("Set-Cookie").getValue());

        return parseAnswer(httpResponse);
    }


    /**
     *
     * @param userId
     * @param password
     * @param image
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     * @throws com.foxycode.testapp.Exception.MyAppException
     */
    public JSONObject postImage(String userId,
                                String password,ImageContent image)
            throws IllegalStateException, IOException, JSONException,
            MyAppException {

        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        String url = Settings.getUrl() + "/image";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Cookie", getCookies());
        Log.d("State:", "Post to the server beginning");
        Log.d("Connection to:", url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Content-type",
                "multipart/form-data"));

        MultipartEntity entity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);
        File file = new File(image.getPath());
        if (file != null)
            entity.addPart(image.getValue(),
                    new FileBody(file, "image/jpg"/*
														 * +getFileExtension(
														 * imageAbsolutePath)
														 */));



        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext); // Execute
        // the
        // post
        // request
        return parseAnswer(httpResponse);
    }

    public JSONObject postContentImage(String userId,
                                 String password, String urlImage, long idOnPhone)
            throws IllegalStateException, IOException, JSONException,
            MyAppException {

        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        String url = Settings.getUrl() + "/image";

        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Content-type",
                "multipart/form-data"));
        if (urlImage != null)
            nameValuePairs.add(new BasicNameValuePair("image", urlImage));
        nameValuePairs.add(new BasicNameValuePair(USER_ID, userId));
        nameValuePairs.add(new BasicNameValuePair(USER_PASSWORD, password));
        nameValuePairs.add(new BasicNameValuePair(USER_ID_CONTENT, ""+idOnPhone));

        MultipartEntity entity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int index = 0; index < nameValuePairs.size(); index++) {
            if (nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
                // If the key equals to "image", we use FileBody to transfer the
                // data
                Log.d("Path from wich its saved on the server", nameValuePairs
                        .get(index).getValue());

                // ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // bmp.compress(CompressFormat.JPEG, 70, bos);
                // InputStream in = new ByteArrayInputStream(bos.toByteArray());
                // ContentBody foto = new InputStreamBody(in, "image/jpeg",
                // "filename");
                File file = new File(urlImage);
                if (file != null)
                    entity.addPart(nameValuePairs.get(index).getName(),
                            new FileBody(file, "image/jpg"/*
														 * +getFileExtension(
														 * imageAbsolutePath)
														 */));

            } else {
                // Normal string data
                entity.addPart(nameValuePairs.get(index).getName(),
                        new StringBody(nameValuePairs.get(index).getValue()));
            }
        }
        Log.v(LOG,entity.toString());
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext); // Execute
        setCookies(httpResponse.getFirstHeader("Set-Cookie") == null ? ""
                : httpResponse.getFirstHeader("Set-Cookie").getValue());

        Log.v(LOG, "cookie:"+getCookies());

        return parseAnswer(httpResponse);
    }

    /**
     *
     * @param userId
     * @param password
     * @param urlImage
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     * @throws com.foxycode.testapp.Exception.MyAppException
     */
    public JSONObject postSignIn(String userId,
                                String password, String urlImage)
            throws IllegalStateException, IOException, JSONException,
            MyAppException {

        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        String url = Settings.getUrl() + "/loginWithImage";

        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Content-type",
                "multipart/form-data"));
        if (urlImage != null)
            nameValuePairs.add(new BasicNameValuePair("image", urlImage));
        nameValuePairs.add(new BasicNameValuePair(USER_ID, userId));
        nameValuePairs.add(new BasicNameValuePair(USER_PASSWORD, password));

        MultipartEntity entity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int index = 0; index < nameValuePairs.size(); index++) {
            if (nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
                // If the key equals to "image", we use FileBody to transfer the
                // data
                Log.d("Path from wich its saved on the server", nameValuePairs
                        .get(index).getValue());

                // ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // bmp.compress(CompressFormat.JPEG, 70, bos);
                // InputStream in = new ByteArrayInputStream(bos.toByteArray());
                // ContentBody foto = new InputStreamBody(in, "image/jpeg",
                // "filename");
                File file = new File(urlImage);
                if (file != null)
                    entity.addPart(nameValuePairs.get(index).getName(),
                            new FileBody(file, "image/jpg"/*
														 * +getFileExtension(
														 * imageAbsolutePath)
														 */));

            } else {
                // Normal string data
                entity.addPart(nameValuePairs.get(index).getName(),
                        new StringBody(nameValuePairs.get(index).getValue()));
            }
        }
        Log.v(LOG,entity.toString());
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext); // Execute
        setCookies(httpResponse.getFirstHeader("Set-Cookie") == null ? ""
                : httpResponse.getFirstHeader("Set-Cookie").getValue());

        Log.v(LOG, "cookie:"+getCookies());

        return parseAnswer(httpResponse);
    }

    /**
     *
     * @param sUserId
     * @param sPassword
     * @param message
     * @return
     * @throws JSONException
     * @throws IOException
     * @throws com.foxycode.testapp.Exception.MyAppException
     */
    public JSONObject postMessage(String sUserId, String sPassword, TextContent message)
            throws JSONException, IOException,
            MyAppException {
        String url = Settings.getUrl() + "/Message";

        JSONObject jsonObject = new JSONObject();
        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        Log.d("State:", "Post to the server beginning");

        jsonObject.put(USER_ID, sUserId);
        jsonObject.put(USER_PASSWORD, sPassword);
        jsonObject.put(USER_MESSAGE, message.getValue());

        StringEntity se = new StringEntity(jsonObject.toString());
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(se);

        Log.d("Entity...", "Created !");
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext);
        return parseAnswer(httpResponse);
    }

    private JSONObject parseAnswer(HttpResponse httpResponse)
            throws IllegalStateException, IOException, MyAppException {
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        Log.d("Server response:", Integer.toString(statusCode));
        JSONObject response = null;
        StringBuilder stringBuilder = new StringBuilder();
        HttpEntity httpentity = httpResponse.getEntity();
        InputStream inputStream = httpentity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        String s = stringBuilder.toString();

        if (statusCode == 200) {
            try {

                Log.d(LOG, "200 String: " + s);
                JSONObject jj = new JSONObject(stringBuilder.toString());
                // Log.d(LOG_TAG,"String: "+jj.getString("message"));
                response = jj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (statusCode == 400) {
            throw new MyAppException(MyAppException.ERROR_SERVER_INT,
                    "error 400");

        } else {
            String messageError = MyAppException.ERROR_SERVER;
            try {

                Log.d(LOG, "String: " + s);
                JSONObject jj = new JSONObject(stringBuilder.toString());
                messageError = jj.getString("messageError");
                Log.e(LOG, "String: " + messageError);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            throw new MyAppException(MyAppException.ERROR_SERVER_INT,
                    messageError);
        }
        return response;

    }

    public boolean downloadFileData(String save_path, String pic_url) {

        Map<String, String> list = new HashMap<String, String>();
        list.put(USER_PASSWORD, c.getPwd_ID());
        list.put(USER_ID, c.getUserId());
        list.put(PICTURE_URL, pic_url);

        String parseParam = XString.parseString(REST_GET_IMAGE, list);
        String urlserver = Settings.getUrl() + parseParam;

        Log.v(LOG, "url: "+urlserver);

        //   GZIPInputStream zin = null;
        BufferedInputStream in = null;
        FileOutputStream fileOutput = null;
        try {

            Log.v(LOG, "dwl 1");
            HttpClient httpClient = getSpecialClient();

            HttpGet getRequest = new HttpGet(urlserver);
            HttpResponse response = httpClient.execute(getRequest);
            HttpEntity entity = response.getEntity();

            Log.v(LOG, "dwl 2");

            // zin = new GZIPInputStream(new BufferedInputStream(entity.getContent()));
            in = new BufferedInputStream(entity.getContent());

            fileOutput = new FileOutputStream(save_path);
            byte[] buffer = new byte[1024];
            int numByte;
            int percent1 = 0;
            int percent2 = 0;
            int totalByte = 0;

            Log.v(LOG, "dwl 3");
            while ((numByte = in.read(buffer, 0, 1024)) > 0) {
                fileOutput.write(buffer, 0, numByte);
                totalByte += numByte;
                //  percent1 = (int) ((double) numByte/(double)totalByte*DownloadFile.TRANSFER_WEIGHT);
                // if( (percent1-percent2)>1){
                //     mCallBack.fileStatus(percent1,mPath,RequestManagerServiceEasy.DOWNLOAD);
                //     percent2=percent1;
                // }
            }

            in.close();


            //SimpleReplyMessage reply = XMLMessageParser.parseSimpleReplyMessage(response);
            return true;
        } catch (Exception e) {

            Log.v(LOG, "dwl 4 "+e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (fileOutput != null)
                    fileOutput.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    public JSONObject downloadImageContent(long id) {

        Map<String, String> list = new HashMap<String, String>();
        list.put(USER_PASSWORD, c.getPwd_ID());
        list.put(USER_ID, c.getUserId());
        list.put(PICTURE_ID, ""+id);

        String parseParam = XString.parseString(REST_GET_IMAGECONTENT, list);
        String urlserver = Settings.getUrl() + parseParam;

        Log.v(LOG, "url: "+urlserver);

        //   GZIPInputStream zin = null;
        BufferedInputStream in = null;
        FileOutputStream fileOutput = null;
        try {

            Log.v(LOG, "dwl 1");
            HttpClient httpClient = getSpecialClient();

            HttpGet getRequest = new HttpGet(urlserver);
            HttpResponse response = httpClient.execute(getRequest);
            HttpEntity entity = response.getEntity();

            Date date = new Date();
            String save_path =UsUtil.getDirectoryImPath()+"/img_"+date.getTime();
            JSONObject json = new JSONObject();
            json.put("path", save_path);
            json.put("id", id);
            Log.v(LOG, "dwl 2");

            // zin = new GZIPInputStream(new BufferedInputStream(entity.getContent()));
            in = new BufferedInputStream(entity.getContent());


            fileOutput = new FileOutputStream(save_path);
            byte[] buffer = new byte[1024];
            int numByte;
            int percent1 = 0;
            int percent2 = 0;
            int totalByte = 0;

            Log.v(LOG, "dwl 3");
            while ((numByte = in.read(buffer, 0, 1024)) > 0) {
                fileOutput.write(buffer, 0, numByte);
                totalByte += numByte;
                //  percent1 = (int) ((double) numByte/(double)totalByte*DownloadFile.TRANSFER_WEIGHT);
                // if( (percent1-percent2)>1){
                //     mCallBack.fileStatus(percent1,mPath,RequestManagerServiceEasy.DOWNLOAD);
                //     percent2=percent1;
                // }
            }
            fileOutput.flush();

          //  in.close();


            //SimpleReplyMessage reply = XMLMessageParser.parseSimpleReplyMessage(response);
            return json;
        } catch (Exception e) {

            Log.v(LOG, "dwl 4 "+e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (fileOutput != null)
                    fileOutput.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public JSONObject postContentText(String userId, String password, String messEncrypt, long idOnPhone) throws IOException, MyAppException {

        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        String url = Settings.getUrl() + "/text";

        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Content-type",
                "multipart/form-data"));
        nameValuePairs.add(new BasicNameValuePair(USER_TEXT, messEncrypt));
        nameValuePairs.add(new BasicNameValuePair(USER_ID, userId));
        nameValuePairs.add(new BasicNameValuePair(USER_PASSWORD, password));
        nameValuePairs.add(new BasicNameValuePair(USER_ID_CONTENT, ""+idOnPhone));


        MultipartEntity entity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int index = 0; index < nameValuePairs.size(); index++) {
                entity.addPart(nameValuePairs.get(index).getName(),
                        new StringBody(nameValuePairs.get(index).getValue()));
        }

        Log.v(LOG,entity.toString());
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext); // Execute
        setCookies(httpResponse.getFirstHeader("Set-Cookie") == null ? ""
                : httpResponse.getFirstHeader("Set-Cookie").getValue());

        Log.v(LOG, "cookie:"+getCookies());

        return parseAnswer(httpResponse);

    }


    public JSONObject downloadTextContent(long id) {

        Map<String, String> list = new HashMap<String, String>();
        list.put(USER_PASSWORD, c.getPwd_ID());
        list.put(USER_ID, c.getUserId());
        list.put(TEXT_ID, ""+id);

        String parseParam = XString.parseString(REST_GET_TEXTCONTENT, list);
        String urlserver = Settings.getUrl() + parseParam;

        Log.v(LOG, "url: "+urlserver);

        try {

            Log.v(LOG, "dwl 1");
            HttpClient httpClient = getSpecialClient();

            HttpGet getRequest = new HttpGet(urlserver);
            HttpResponse response = httpClient.execute(getRequest);
            JSONObject json = parseAnswer(response);
            return json;
        }
        catch (Exception e) {

            Log.v(LOG, "dwl 4 "+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject postDeleteIds(String where) throws JSONException, IOException, MyAppException {
        String url = Settings.getUrl() + "/deleteContent";

        JSONObject jsonObject = new JSONObject();
        HttpClient httpClient = getSpecialClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        Log.d("State:", "Post to the server beginning");

        jsonObject.put(USER_ID, c.getUserId());
        jsonObject.put(USER_PASSWORD, c.getPwd_ID());
        //JSONArray ja = new JSONArray();
       // for(int i : mids)
        //    ja.put(i);
        jsonObject.put(WHERE, where );

        StringEntity se = new StringEntity(jsonObject.toString());
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(se);

        Log.d("Entity...", "Created !");
        HttpResponse httpResponse = httpClient.execute(httpPost, localContext);
        return parseAnswer(httpResponse);

    }
}
