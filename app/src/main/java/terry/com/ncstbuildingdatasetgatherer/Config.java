package terry.com.ncstbuildingdatasetgatherer;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public static String studentID_SP_KEY = "studentID";
    public static String studentID;

    public static String[] districtCodes = {
            "LIB","SCB","ADB","MB",
            "CTA",
                "CTAA","CTAB",
                "CTAC","CTAD",
                "CTAE","CTAL",
                "CTAK","CTAF",
            "JT",
            "MCT","LCT","ZCT","JCT",
            "SWB","WL","PEPE","PEB",
            "ACC","SG","SWG","WSSC","ESSC"
    };
    public static Map<String,Integer> districtCodesMap;
    public static String[] districtNames = {
            "图书馆","科技楼","行政楼","大会堂",
            "核心教学区",
                "核心教学区A座","核心教学区B座",
                "核心教学区C座","核心教学区D座",
                "核心教学区E座","核心教学区L座",
                "核心教学区K座","核心教学区F座",
            "冀唐学院",
            "梅园食堂","兰园食堂","竹园食堂","菊园食堂",
            "游泳馆","校园湿地","东区体育场入口","体育馆",
            "学术交流中心","南门","西南门",
            "西大学生服务中心","东大学生服务中心"
    };

    static {
        districtCodesMap = new HashMap<String, Integer>();
        for(int i=0;i<districtCodes.length;i++)
            districtCodesMap.put(districtCodes[i],i);
    }
}
