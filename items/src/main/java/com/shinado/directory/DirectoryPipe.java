package com.shinado.directory;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

public class DirectoryPipe extends SearchablePipe {

    private static final String OPT_ADD = "add";
    private static final String OPT_REMOVE = "rm";
    private AbsTranslator mTranslator;
    private HashMap<String, Pipe> mPipeMap = new HashMap<>();

    public DirectoryPipe(int id) {
        super(id);
    }

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public Pipe getByValue(String value) {
        //not used
        return null;
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        callback.onOutput("Directory does not take any input");
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput(result.getExecutable());
    }

    @Override
    protected void putItemInMap(Pipe vo) {
        super.putItemInMap(vo);
        mPipeMap.put(vo.getExecutable(), vo);
    }

    @Override
    protected void removeItemInMap(Pipe vo) {
        super.removeItemInMap(vo);
        mPipeMap.remove(vo.getExecutable());
    }

    @Override
    protected void execute(Pipe rs) {
        Instruction instruction = rs.getInstruction();
        String params[] = instruction.params;
        if (params != null && params.length != 0) {
            if (params.length > 1) {

            }
            switch (params[0]) {
                case OPT_ADD:
                    add(rs);
                    break;
                case OPT_REMOVE:
                    remove(rs);
                    break;
            }
        } else {
            open(rs);
        }
    }

    private void remove(Pipe rs){
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            removeFiles(file);
        }
    }

    private void add(Pipe rs) {
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            addFiles(file);
        }
    }

    private void addFile(String path) {
        int index = path.lastIndexOf('/');
        String displayName = path.substring(index + 1, path.length());
        //TODO add Chinese Translator
        if (mTranslator == null){
            mTranslator = new EnglishTranslator(getLauncher());
        }
        Pipe pipe = new Pipe(DirectoryPipe.this.getId(), "/" + displayName, mTranslator.getName(displayName), path);
        pipe.setBasePipe(DirectoryPipe.this);
        putItemInMap(pipe);
    }

    private void addFiles(File dir) {
        File files[] = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                addFile(file.getPath());
            }else if (file.isDirectory()){
                addFiles(file);
            }
        }
    }

    private void removeFiles(File dir) {
        File files[] = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                removeFile(file.getPath());
            }else if (file.isDirectory()){
                removeFiles(file);
            }
        }
    }


    private void removeFile(String path){
        Pipe pipe = mPipeMap.get(path);
        removeItemInMap(pipe);
    }

    private void open(Pipe rs) {

        File file = new File(rs.getExecutable());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        getLauncher().startActivity(intent);
    }

    private final String[][] MIME_MapTable = {
//{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    private String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
//获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
/* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
//在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        new Thread() {
            public void run() {
                while (!translator.ready()) ;
                ArrayList<String> all = getAllDirectoriesFromSDCard();
                for (String item : all) {
                    addFile(item);
                }
                listener.onItemsLoaded(DirectoryPipe.this.getId(), total);
            }
        }.start();
    }

    public ArrayList<String> getAllDirectoriesFromSDCard() {
        ArrayList<String> absolutePathOfImageList = new ArrayList<>();
        File file[] = Environment.getExternalStorageDirectory().listFiles();

        for (File f : file) {
            if (f.isDirectory()) {
                absolutePathOfImageList.add(f.getAbsolutePath());
            }
        }

        return absolutePathOfImageList;
    }
}
