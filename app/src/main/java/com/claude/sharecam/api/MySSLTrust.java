package com.claude.sharecam.api;

import android.content.Context;

import com.claude.sharecam.R;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MySSLTrust {

        @SuppressWarnings("null")
        public static OkHttpClient configureClient(final OkHttpClient client) {
            final TrustManager[] certs = new TrustManager[] { new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }
            } };

            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(null, certs, new SecureRandom());
            } catch (final java.security.GeneralSecurityException ex) {
            }

            try {
                final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(final String hostname,
                                          final SSLSession session) {
                        return true;
                    }
                };
                client.setHostnameVerifier(hostnameVerifier);
                client.setSslSocketFactory(ctx.getSocketFactory());
            } catch (final Exception e) {
            }

            return client;
        }

        public static OkHttpClient trustcert(Context context) {
            final OkHttpClient client = new OkHttpClient();
            return configureClient(client);
        }

    /*
    public static OkHttpClient trustcert(Context context){
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            KeyStore ksTrust = KeyStore.getInstance("BKS");
            InputStream instream = context.getResources().openRawResource(R.raw.mykeystore);
            ksTrust.load(instream, "secret".toCharArray());
            // TrustManager decides which certificate authorities to use.
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ksTrust);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            e.printStackTrace();
        }
        return okHttpClient;
    }*/
}