package com.hiddenshrineoffline;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileManager {

    public String readFile(String filename, Context context){
        FileInputStream inputStream;
        int c;
        String temp = "";

        try{
            inputStream = context.openFileInput(filename);
            while ((c=inputStream.read()) != -1){
                temp = temp + Character.toString((char) c);
            }
            inputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return temp;
    }


    public void saveFile(String filename, String fileContents, Context context) {
        FileOutputStream outputStream;

        try{
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
