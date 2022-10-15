package com.example.springboot_shixun.utils;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {
    /**
     * 发送短信
     * @param signName 签名
     * @param templateCode 模板
     * @param phoneNumbers 手机号
     * @param param 参数
     */
    public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){

        //后面两个分别是accesskeyid和accesskeysecret，就是申请accesskey后会给的
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tP8zHpaaN1srjC8heet", "s6g9x7WXE8kbQzQowWiWcwc02Trukr");
        //client客户端对象
        IAcsClient client = new DefaultAcsClient(profile);
        //请求对象
        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId("cn-hangzhou");
        //给每个手机号发送短信，手机号写在下方位置
        request.setPhoneNumbers(phoneNumbers);
        //签名，签名管理申请的，得审核通过的
        request.setSignName(signName);
        //模板管理中的模板code
        request.setTemplateCode(templateCode);
        //param动态参数，就验证码替换code
        request.setTemplateParam("{\"code\":\""+param+"\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功");
        }catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
