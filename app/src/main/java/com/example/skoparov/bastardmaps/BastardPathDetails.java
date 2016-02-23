package com.example.skoparov.bastardmaps;

public class BastardPathDetails
{
    public String pathName;
    public String details;

    public BastardPathDetails( String name, String details )
    {
        pathName = name;
        this.details = details;
    }

    public static String getPathDetails( BastardPath p )
    {
        String details = new String();
        details += "Duration: " + String.format("%.2f", p.getDuration()/1000) + " sec\n";
        details += "Distance: " + String.format("%.2f", p.getDistance() ) + " m\n";
        details += "Avr. spd: " + String.format("%.2f", p.getAverageSpeed() ) + " m/sec\n";

        return details;
    }

    public static  String getPathDetails( String duration, String distance, String aveSpeed )
    {
        String details = new String();
        details += "Duration: " + duration + " sec\n";
        details += "Distance: " + distance + " m\n";
        details += "Avr. spd: " + aveSpeed + " m/sec\n";

        return details;
    }
}
