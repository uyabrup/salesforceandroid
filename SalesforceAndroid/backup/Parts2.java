package com.android.java.mypack;

/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.


class Parts2
{

    Parts2(String s)
    {
        int i = s.indexOf('#');
        ref = i >= 0 ? s.substring(i + 1) : null;
        s = i >= 0 ? s.substring(0, i) : s;
        int j = s.lastIndexOf('?');
        if(j != -1)
        {
            query = s.substring(j + 1);
            path = s.substring(0, j);
        } else
        {
            path = s;
        }
    }

    String getPath()
    {
        return path;
    }

    String getQuery()
    {
        return query;
    }

    String getRef()
    {
        return ref;
    }

    String path;
    String query;
    String ref;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Program Files\Java\jre1.6.0_04\lib\rt.jar
	Total time: 16 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/