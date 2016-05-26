package com.couple.kristjanthor.appforcouple;

/**
 * Created by kristjanthorhedinsson on 04/05/16.
 */
public class BECouple {

    int m_id;
    String m_message;

    public BECouple(int id, String message)
    { m_id = id; m_message = message; }


    public String toString()
    { return "" + m_id + ": " + m_message; }

}