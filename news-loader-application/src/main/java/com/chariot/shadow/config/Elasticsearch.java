package com.chariot.shadow.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by Trung Vu on 2017/06/20.
 */
public class Elasticsearch {

    private static final String NEWS_INDEX;
    private static final TransportClient CLIENT;

    static {
        String newsIndex = "news";
        Properties properties = new Properties();
        String nodes = "es-cluster01,es-cluster02";

        try {
            properties.load(new FileInputStream("/data/shadow/config/elasticsearch.properties"));
            newsIndex = properties.getProperty("news.index", newsIndex);
            nodes = properties.getProperty("nodes", nodes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        try {
            for (String node : nodes.split(",")) {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(node), 9300));
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        NEWS_INDEX = newsIndex;
        CLIENT = client;
    }

    public static Client getInstance() {
        return CLIENT;
    }

    public static void dispose() {
        if (CLIENT != null) {
            CLIENT.close();
        }
    }
}