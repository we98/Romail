package com.romail.romail.util;

import com.romail.romail.base64.BASE64Decoder;
import com.romail.romail.global.GlobalVariables;
import com.romail.romail.entity.Account;
import com.romail.romail.entity.Email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class Pop3Manager {
    //不同的邮件服务器域名
    private final static HashMap<String, String> EMAIL_TO_DOMAIN = new HashMap<>();
    static {
        EMAIL_TO_DOMAIN.put("@qq.com", "pop.qq.com");
        EMAIL_TO_DOMAIN.put("@163.com", "pop3.163.com");
    }
    //POP3端口
    private final static int PORT = 110;
    //当连接POP3服务器失败后重新连接的次数
    private final static int RETRY=3;
    //当连接POP3服务器失败后重新连接的时间间隔
    private final static int INTERVAL=1000;
    public static int verifyLogin() throws IOException, InterruptedException{
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        int times = 0;
        String server = EMAIL_TO_DOMAIN.get(Account.getCurrentAccount().getCurrentAccountEmailType());
        for(; times < RETRY; ++times){
            socket = new Socket(server, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new PrintWriter(outputStream, true);
            if("+OK".equals(getResponse(reader))){
                break;
            }
            else{
                Thread.sleep(INTERVAL);
            }
        }
        if(times == RETRY){
            closeResource(writer, reader, outputStream, inputStream, socket);
            //代表无网络连接
            return GlobalVariables.NO_NETWORK_CONNECTION;
        }
        sendCommand(writer, "user " + Account.getCurrentAccount().getCurrentAccountEmail());
        getResponse(reader);
        sendCommand(writer, "pass " + Account.getCurrentAccount().getCurrentAccountPassword());
        boolean succeed = "+OK".equals(getResponse(reader));
        if(!succeed){
            return GlobalVariables.LOGIN_TRANSACTION_FAIL;
        }
        sendCommand(writer, "stat");
        String[] statResult = reader.readLine().split(" ");
        int emailCount = Integer.parseInt(statResult[1]);
        ArrayList<Email> emails = new ArrayList<>(emailCount);
        for(int i = 0; i < emailCount; ++i){
            Email email = new Email(i + 1);
            sendCommand(writer, "retr " + (i + 1));
            String line = "";
            StringBuffer Base64Content = new StringBuffer();
            boolean isReadingContent = false;
            boolean isReadingCompleted = false;
            String codingFormat = "GBK";
            while (!line.contains("CST")) {
                line = reader.readLine();
            }
            email.setDate((line.substring(line.indexOf(";") + 1)).trim());
            while (!line.equals(".")) {
                if (isReadingCompleted) {
                    line = reader.readLine();
                    continue;
                }
                line = reader.readLine();
//            System.out.println(line);
                if (isReadingContent) {
                    if (line.contains("------=")) {
                        try{
                            email.setContent(decode(Base64Content.toString(), codingFormat));
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        isReadingCompleted = true;
                    }
                    else{
                        Base64Content.append(line);
                    }
                }
                else if (line.length() > 5 && line.substring(0, 5).equalsIgnoreCase("From:")) {
                    try{
                        String[] elements = line.split("\\?|<|>");
                        codingFormat = elements[1];
                        email.setSender(decode(elements[3], codingFormat) + " " + elements[5]);
                    }
                    catch(Exception e) {
                        email.setSender(line.substring(6));
                    }
                }
                else if (line.length() > 8 && line.substring(0, 8).equals("Subject:")) {
                    try {
                        String[] elements = line.split("\\?");
                        codingFormat = elements[1];
                        email.setSubject(decode(elements[3], codingFormat));
                    }
                    catch(Exception e) {
                        email.setSubject(line.substring(9));
                    }
                }
                else if (line.length() > 3 && line.substring(0, 3).equals("To:")) {
                    try {
                        email.setReceiver(line.substring(line.indexOf("<") + 1, line.indexOf(">")));
                    }
                    catch(Exception e){
                        email.setReceiver(line.substring(4));
                    }
                }
                else if (line.contains("base64")) {
                    isReadingContent = true;
                }
            }
            emails.add(email);
        }
        Account.getCurrentAccount().setEmails(emails);
        closeResource(writer, reader, outputStream, inputStream, socket);
        //0登录成功 2账号或密码错误
        return GlobalVariables.LOGIN_TRANSACTION_SUCCESS;
    }
    public static int delete(int index) throws IOException{
        if(index < 1 || index > Account.getCurrentAccount().getEmails().size() + 1){
            return GlobalVariables.DELETE_TRANSACTION_FAIL;
        }
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        String server = EMAIL_TO_DOMAIN.get(Account.getCurrentAccount().getCurrentAccountEmailType());
        socket = new Socket(server, PORT);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        writer = new PrintWriter(outputStream, true);
        if(!"+OK".equals(getResponse(reader))){
            return GlobalVariables.DELETE_TRANSACTION_FAIL;
        }
        sendCommand(writer, "user " + Account.getCurrentAccount().getCurrentAccountEmail());
        getResponse(reader);
        sendCommand(writer, "pass " + Account.getCurrentAccount().getCurrentAccountPassword());
        if(!"+OK".equals(getResponse(reader))){
            return GlobalVariables.DELETE_TRANSACTION_FAIL;
        }
        sendCommand(writer, "dele " + index);
        boolean delSuccess = "+OK".equals(getResponse(reader));
        closeResource(writer, reader, outputStream, inputStream, socket);
        return delSuccess ? GlobalVariables.DELETE_TRANSACTION_SUCCESS :
                GlobalVariables.DELETE_TRANSACTION_FAIL;
    }
    private static String decode(String content, String format) throws Exception {
        BASE64Decoder util = new BASE64Decoder();
        byte[] byteConent = util.decodeBuffer(content);
        String result = new String(byteConent, format);
        return result;
    }
    private static void sendCommand(PrintWriter writer, String command){
        writer.println(command);
    }
    private static String getResponse(BufferedReader reader){
        String response = null;
        try {
            response = reader.readLine();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(response);
        return response.substring(0, 3);
    }
    private static void closeResource(PrintWriter writer, BufferedReader reader, OutputStream outputStream,
                               InputStream inputStream, Socket socket) throws IOException{
        sendCommand(writer, "quit");
        getResponse(reader);
        writer.close();
        reader.close();
        outputStream.close();
        inputStream.close();
        socket.close();
    }

}
