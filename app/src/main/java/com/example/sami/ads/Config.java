package com.example.sami.ads;

/**
 * Created by sami on 1/21/2017.
 */

public class Config {
    public static String DOMAIN = "http://adstest.96.lt/";
    //public static String DOMAIN = "http://192.168.56.1/CodeIgniter/";
    public static String HOME_SCREEN_URL = DOMAIN + "index.php/category";
    public static String CITY_URL = DOMAIN + "index.php/city";
    public static String CATEGORY_IMAGE_PATH = DOMAIN + "images/category/";
    public static String REGISTER_URL = DOMAIN + "index.php/user/register";
    public static String TEST = DOMAIN + "index.php/welcome/test";
    public static String IMAGE_UPLOAD = DOMAIN + "index.php/ad/upload_image";
    public static String ADD_AD = DOMAIN + "index.php/ad/add";
    public static String AD_BY_CATEGORY = DOMAIN + "index.php/ad/by_category";
    public static String AD_VIEW = DOMAIN + "index.php/ad/view";
    public static int LIMIT_ROW_COUNT = 5;
    public static String IMAGE_PATH = DOMAIN + "images/ads/";
    public static String IMAGES_AD = DOMAIN + "index.php/ad/get_images/";
    public static String SIGN_IN = DOMAIN + "index.php/user/signin/";
    public static String USER_ADS = DOMAIN + "index.php/ad/by_user/";
    public static String Ad_DEACTIVE = DOMAIN + "index.php/ad/deactive/";
}
