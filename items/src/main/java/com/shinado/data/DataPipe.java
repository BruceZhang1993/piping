package com.shinado.data;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.Method;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class DataPipe extends DefaultInputActionPipe{

    public DataPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$data";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"da", "ta"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        roll(callback);

    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        roll(callback);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        roll(callback);
    }

    private void roll(OutputCallback callback){
        ConnectivityManager mCM = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass   = mCM.getClass();
        Class[] argClasses  = null;
        Object[] argObject  = null;
        Boolean isEnable = true;
        try{
            Method method = cmClass.getMethod("getMobileDataEnabled", argClasses);
            isEnable = (Boolean) method.invoke(mCM, argObject);
        }catch (Exception e){
            e.printStackTrace();
        }

        String output;
        if (isEnable){
            output = "Data traffic is on.";
        }else{
            output = "Data traffic is off.";
        }
        if (callback == getConsoleCallback()){
            if (isEnable){
                output += " Turning off...";
            }else{
                output += " Turning on...";
            }

            Class[] arg  = new Class[1];
            argClasses[0]   = boolean.class;

            try{
                Method method = cmClass.getMethod("setMobileDataEnabled", arg);
                method.invoke(mCM, !isEnable);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        callback.onOutput(output);
    }

}
