package com.hiddenshrineoffline;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileManager {

    /* Always use readObjectFile unless is small string
     * @param filename
     * @param context
     * @return
     */

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


    public Object readObjectFile(String filename, Context context){
        Object outputObject;
        outputObject = new Object();

        try{
            File file;
            file = new File(context.getFilesDir(), filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            outputObject = objectInputStream.readObject();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return outputObject;
    }

    public void saveObjectFile(String filename, Context context, Object object){
        try {
            File file;
            file = new File(context.getFilesDir(), filename);
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(object);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
