package indi.shinado.piping.pipes.impl;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

/**
 * Created by shinado on 16/5/7.
 */
public class ShellPipe extends DefaultInputActionPipe{

    private static final String NAME = "$shell";

    public ShellPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"shell"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
//        runAsRoot(new String[]{"mpstat -P ALL"});
//        runShell("mpstat");
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe rs, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

        ShellUtil.CommandResult result = ShellUtil.execCommand(input, false);
        callback.onOutput(result.successMsg +", "+ result.errorMsg);
    }

    private void runAsRoot(String[] cmds){
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            InputStream is = p.getInputStream();
            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd + "\n");
                os.flush();
                int readed = 0;
                byte[] buff = new byte[4096];
                int available = is.available();
                Log.d("shell-ava", "" + available);
//                boolean cmdRequiresAnOutput = true;
//                if (cmdRequiresAnOutput) {
//                    while( is.available() <= 0) {
//                        try { Thread.sleep(5000); } catch(Exception ex) {}
//                    }

                    while( is.available() > 0) {
                        readed = is.read(buff);
                        if ( readed <= 0 ) break;
                        String seg = new String(buff,0,readed);
                        Log.d("shell output:", seg);
                    }
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void runShell(String cmd){
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }
            Log.d("shell-get", log.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
