package com.example.skoparov.bastardmaps;

import android.location.Location;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

public class BastardFileManager
{
    private enum DetailField
    {
        DETAILS_NAME,
        DETAILS_DETAILS,
        DETAILS_SIZE;

        public static int toInt(DetailField x)
        {
            switch (x)
            {
                case DETAILS_NAME:
                    return 0;
                case DETAILS_DETAILS:
                    return 1;
                case DETAILS_SIZE:
                    return 2;
            }

            return 3;
        }
    }

    private enum PathField
    {
        PATH_LAT,
        PATH_LNG,
        PATH_TIME,

        PATH_SIZE;

        public static int toInt(PathField x)
        {
            switch (x)
            {
                case PATH_LAT:
                    return 0;
                case PATH_LNG:
                    return 1;
                case PATH_TIME:
                    return 2;
                case PATH_SIZE:
                    return 3;
            }

            return 4;
        }
    }


    public void savePath(BastardPath p) throws IOException
    {
        String filename = p.getName();
        FileOutputStream out = new FileOutputStream(getFilePath(filename));

        Iterator<Location> it = p.getPoints().iterator();
        while(it.hasNext())
        {
            Location  l = it.next();
            out.write((Double.toString(l.getLatitude()) + ";").getBytes());
            out.write((Double.toString(l.getLongitude()) + ";").getBytes());
            out.write((Long.toString(l.getTime()) + ";").getBytes());
        }

        out.flush();
        out.close();
    }

    public BastardPath loadPath(String fileName) throws IOException, ClassNotFoundException
    {
        BastardPath p = null;
        FileInputStream in = new FileInputStream(getFilePath(fileName));
        InputStreamReader isr = new InputStreamReader ( in ) ;
        BufferedReader buffreader = new BufferedReader ( isr ) ;

        String readString = buffreader.readLine ( ) ;
        String[] parts = readString.split(";");

        if( parts.length %  PathField.toInt(PathField.PATH_SIZE) == 0)
        {
            p = new BastardPath();
            for(int loc = 0; loc < parts.length; loc+=PathField.toInt(PathField.PATH_SIZE))
            {
                Double lat = Double.parseDouble(parts[loc + PathField.toInt(PathField.PATH_LAT)]);
                Double lng = Double.parseDouble(parts[loc + PathField.toInt(PathField.PATH_LNG)]);
                Long time = Long.parseLong(parts[loc + PathField.toInt(PathField.PATH_TIME)]);

                Location l = new Location("");
                l.setLatitude(lat);
                l.setLongitude(lng);
                l.setTime(time);
                p.addPosition(l);
            }
        }

        return p;
    }

    public void savePathDetails(BastardPathDetails d) throws IOException
    {
        String filename = "details_"+ d.pathName;
        FileOutputStream out = new FileOutputStream(getFilePath(filename));
        String detailsSub = d.details.replace("\n", "|");

        out.write( (d.pathName + ";").getBytes() );
        out.write( detailsSub.getBytes() );

        out.flush();
        out.close();
    }

    public BastardPathDetails loadPathDetails( String fileName ) throws IOException
    {
        FileInputStream in = new FileInputStream(getFilePath(fileName));
        InputStreamReader isr = new InputStreamReader ( in ) ;
        BufferedReader buffReader = new BufferedReader ( isr ) ;

        String readString = buffReader.readLine ( ) ;
        String[] parts = readString.split(";");

        if( parts.length  ==  DetailField.toInt(DetailField.DETAILS_SIZE))
        {
            String name = parts[DetailField.toInt(DetailField.DETAILS_NAME)];
            String details = parts[DetailField.
                    toInt(DetailField.DETAILS_DETAILS)].replace("|", "\n");

            return new BastardPathDetails(name, details);
        }

        return null;
    }

    public HashMap<String, BastardPathDetails > loadPathsList() throws IOException
    {
        File f = new File(homeFolder());
        File files[] = f.listFiles();
        HashMap<String, BastardPathDetails > result = new HashMap<>();

        for (int file = 0; file < files.length; ++file)
        {
            String name = files[file].getName();
            if( name.contains("details_") )
            {
                BastardPathDetails d = loadPathDetails(name);
                result.put(d.pathName, d);
            }
        }

        return result;
    }

    public boolean deleteFile(String fileName)
    {
        File file = new File(getFilePath(fileName));
        return file.delete();
    }

    public boolean deleteDetailsFile(String fileName)
    {
        String name = "details_"+fileName;
        File file = new File(getFilePath(name));
        return file.delete();
    }

    public String homeFolder()
    {
        String home = new String();

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            home = Environment.getExternalStorageDirectory().toString();
            home += "/Android/data/com.example.skoparov.bastardmaps/";
        }

        return home;
    }

    private String getFilePath(String pathName)
    {
        return homeFolder()+pathName;
    }
}
