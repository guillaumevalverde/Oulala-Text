package com.foxycode.testapp.Backend;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import android.content.Context;
import android.content.res.AssetManager;

public class MyHttpClient extends DefaultHttpClient {
	 
    final Context mContext;
 
    public MyHttpClient(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }
 
    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // Register for port 443 our SSLSocketFactory with our keystore
        // to the ConnectionManager
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        
      //this = new DefaultHttpClient(new ThreadSafeClientConnManager(getParams(), registry), getParams());
        return new ThreadSafeClientConnManager(getParams(), registry);
    }
 
    private SSLSocketFactory newSslSocketFactory() {
    	

     try {
          AssetManager assetManager       = mContext.getAssets();
      	  InputStream keyStoreInputStream = assetManager.open("ca.store");
      	  KeyStore trustStore             = KeyStore.getInstance("BKS");

      	  trustStore.load(keyStoreInputStream, "innovation".toCharArray());
          SSLSocketFactory sf = new SSLSocketFactory(trustStore);
          sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}

