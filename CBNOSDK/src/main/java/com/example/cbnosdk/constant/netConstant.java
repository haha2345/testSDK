package com.example.cbnosdk.constant;

public class netConstant {
    public static String getURL() {
        return URL;
    }

    private static String URL="http://172.16.10.11:8080/notarize";

    private static String getVcodeURL=URL+"/getSMSCode";
    private static String loginURL=URL+"/mobile/login";
    private static String registerURL=URL+"/mobile/register";
    private static String resetPwdURL=URL+"/mobile/resetPwd";
    private static String noticeURL=URL+"/system/notice/list";
    private static String bankURL=URL+"/fqgz/coList/partList";
    private static String applyURL=URL+"/fqgz/ca/apply";
    private static String uploadEContractURL=URL+"/fqgz/userFile/uploadEContract";
    private static String checkSMSCodeURL=URL+"/checkSMSCode";
    private static String model0x12URL=URL+"/fqgz/ca/mode0x12";
    private static String personalListURL=URL+"/fqgz/caseList/personalList";
    private static String userInfoandStateURL=URL+"/fqgz/ca/getUserInfoAndState";
    private static String addTrustUserV2URL=URL+"/fqgz/ca/addTrustUserV2";
    private static String cloudSealUploadDocWithKeyIDURL=URL+"/fqgz/ca/cloudSealUploadDocWithKeyID";
    private static String cloudSealCommitSignURL=URL+"/fqgz/ca/cloudSealCommitSign";
    private static String uploadNotifyLetterURL=URL+"/fqgz/userFile/uploadNotifyLetter";
    private static String uploadNotarizeVideo=URL+"/fqgz/userFile/uploadNotarizeVideo";
    private static String getCaseFilePathURL=URL+"/fqgz/userFile/getCaseFilePath";
    private static String downloadCaseFile=URL+"/fqgz/userFile/downloadCaseFile";


    public static String getUploadNotarizeVideo() {
        return uploadNotarizeVideo;
    }




    public static String getGetCaseFilePathURL() {
        return getCaseFilePathURL;
    }


    public static String getDownloadCaseFile() {
        return downloadCaseFile;
    }


    public static String getUploadNotifyLetterURL() {
        return uploadNotifyLetterURL;
    }



    public static String getCloudSealCommitSignURL() {
        return cloudSealCommitSignURL;
    }



    public static String getCloudSealUploadDocWithKeyIDURL() {
        return cloudSealUploadDocWithKeyIDURL;
    }






    public static String getUserInfoandStateURL() {
        return userInfoandStateURL;
    }
    public static String getAddTrustUserV2URL() {
        return addTrustUserV2URL;
    }
    public static String getPersonalListURL() {
        return personalListURL;
    }
    public static String getCheckSMSCodeURL() {
        return checkSMSCodeURL;
    }
    public static String getModel0x12URL() {
        return model0x12URL;
    }
    public static String getApplyURL() {
        return applyURL;
    }
    public static String getUploadEContractURL() {
        return uploadEContractURL;
    }
    public static String getBankURL() {
        return bankURL;
    }
    public static String getGetVcodeURL() {
        return getVcodeURL;
    }
    public static String getLoginURL() {
        return loginURL;
    }
    public static String getRegisterURL() {
        return registerURL;
    }
    public static String getResetPwdURL() {
        return resetPwdURL;
    }
    public static String getNoticeURL() {
        return noticeURL;
    }
}
