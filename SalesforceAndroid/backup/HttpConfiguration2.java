package com.android.java.mypack;

/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

import java.net.*;
import org.apache.harmony.luni.util.Msg;

import android.net.Uri;

import com.android.java.mypack.proxy.Proxy2;

public class HttpConfiguration2
{

    public HttpConfiguration2(Uri uri)
    {
        this.uri = uri;
        hostName = uri.getHost();
        hostPort = uri.getPort();
        if(hostPort == -1)
            if(uri.getScheme().equals("https"))
                hostPort = 443;
            else
                hostPort = 80;
    }

    public HttpConfiguration2(Uri uri, Proxy2 proxy)
    {
        this.uri = uri;
        this.proxy = proxy;
        java.net.SocketAddress proxyAddr;
        if(proxy.type() == Proxy2.Type.HTTP)
        {
            proxyAddr = proxy.address();
            if(!(proxyAddr instanceof InetSocketAddress))
                throw new IllegalArgumentException(Msg.getString("K0316", proxyAddr.getClass()));
            InetSocketAddress iProxyAddr = (InetSocketAddress)proxyAddr;
            hostName = iProxyAddr.getHostName();
            hostPort = iProxyAddr.getPort();
        } else
        {
            hostName = uri.getHost();
            hostPort = uri.getPort();
            if(hostPort == -1)
                if(uri.getScheme().equals("https"))
                    hostPort = 443;
                else
                    hostPort = 80;
        }
        this.uri = uri;
        proxyAddr = proxy.address();
        if(!(proxyAddr instanceof InetSocketAddress))
        {
            throw new IllegalArgumentException(Msg.getString("K0316", proxyAddr.getClass()));
        } else
        {
            InetSocketAddress iProxyAddr = (InetSocketAddress)proxyAddr;
            hostName = iProxyAddr.getHostName();
            hostPort = iProxyAddr.getPort();
            return;
        }
    }

    public boolean usesProxy()
    {
        return proxy != null;
    }

    public Proxy2 getProxy()
    {
        return proxy;
    }

    public String getHostName()
    {
        return hostName;
    }

    public int getHostPort()
    {
        return hostPort;
    }

    public boolean equals(Object arg0)
    {
        if(!(arg0 instanceof HttpConfiguration2))
            return false;
        HttpConfiguration2 config = (HttpConfiguration2)arg0;
        if(config.proxy != null && proxy != null)
            return config.proxy.equals(proxy) && uri.equals(config.uri);
        else
            return uri.equals(config.uri);
    }

    public int hashCode()
    {
        return uri.hashCode();
    }

    private Proxy2 proxy;
    private int hostPort;
    private String hostName;
    private Uri uri;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Application\Android\sdkrc14\android.jar
	Total time: 31 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/