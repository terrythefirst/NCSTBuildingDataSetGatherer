package terry.com.ncstbuildingdatasetgatherer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import terry.com.ncstbuildingdatasetgatherer.cameraContents.Camera2Config;
import terry.com.ncstbuildingdatasetgatherer.cameraContents.Camera2RecordActivity;
import terry.com.ncstbuildingdatasetgatherer.cameraContents.Camera2Util;
import terry.com.ncstbuildingdatasetgatherer.mapContents.MapBitmapsConstant;
import terry.com.ncstbuildingdatasetgatherer.mapContents.MapView;

public class SwitchView {
    MainActivity mainActivity;
    private Button chechBtn;
    private Button shareBtn;
    private ImageButton refreshBtn;
    private ListView listView;
    private View switchHeadView;
    private BaseAdapter adapter;

    private float mFirstY;
    private float mSecondY;
    private float mCurrentY;
    private int mTouchSlop;
    private int direction;

    public int[] imgsCount;

    public SwitchView(final MainActivity mainActivity){
        this.mainActivity = mainActivity;
        mainActivity.setContentView(R.layout.switch_layout);

        getImgsCount();

        initView();

    }
    public void initView(){
        listView = mainActivity.findViewById(R.id.switchMapList);
        //switchHeadView = View.inflate(mainActivity,R.layout.switch_head_view,null);
//        chechBtn = switchHeadView.findViewById(R.id.checkBtn);
//        shareBtn = switchHeadView.findViewById(R.id.shareBtn);
        //listView.addFooterView(switchHeadView);

        chechBtn = mainActivity.findViewById(R.id.checkBtn);
        shareBtn = mainActivity.findViewById(R.id.shareBtn);
        refreshBtn = mainActivity.findViewById(R.id.refresh_btn);

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.checkBtn:
                        checkOK();
                        break;
                    case R.id.shareBtn:
                        if(checkOK()){
                            saveAsZip();
                            File file = new File(Camera2Config.ZIP_SAVA_PATH+Config.studentID+".zip");
                            shareFile(mainActivity,file);
                        }
                        break;
                    case R.id.refresh_btn:
                        getImgsCount();
                        break;
                    case R.id.return_btn:
                        mainActivity.setContentView(R.layout.switch_layout);
                        initView();
                        break;
                }
            }
        };
        chechBtn.setOnClickListener(onClickListener);
        shareBtn.setOnClickListener(onClickListener);
        refreshBtn.setOnClickListener(onClickListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainActivity.switchToCamera(position);
            }
        });

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return Config.districtNames.length;
            }

            @Override
            public Object getItem(int position) {
                return Config.districtNames[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                //getImgsCount();
                View view= LayoutInflater.from(mainActivity).inflate(R.layout.switch_item, null);
                ImageButton btn = view.findViewById(R.id.item_map_btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            MapBitmapsConstant.load_bitmaps(mainActivity);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        mainActivity.setContentView(R.layout.map_view_layout);
                        MapView mapView = mainActivity.findViewById(R.id.map_view);
                        mapView.setDistrictID(position);
                        mainActivity.findViewById(R.id.return_btn).setOnClickListener(onClickListener);
                    }
                });
                TextView mTextView=(TextView)view.findViewById(R.id.item_text);
                int cnt = (4-imgsCount[position])>0?(4-imgsCount[position]):0;
                Log.e("getView",Config.districtNames[position]+":"+cnt);
                if(cnt==0)
                    mTextView.setText(Config.districtNames[position]);
                else
                    mTextView.setText(Config.districtNames[position]+"("+cnt+")");
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    public void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri data;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                data = FileProvider.getUriForFile(mainActivity, "terry.com.ncstbuildingdatasetgatherer.fileprovider", file);
                // 给目标应用一个临时授权
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                data = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, data);
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            Toast.makeText(mainActivity, "分享文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    public void saveAsZip(){
        FileOutputStream fos = null;
        Camera2Util.createSavePath(Camera2Config.ZIP_SAVA_PATH);//判断有没有这个文件夹，没有的话需要创建
        String zipSavePath = Camera2Config.ZIP_SAVA_PATH+Config.studentID+".zip";
        try {
            fos = new FileOutputStream(zipSavePath);

            String path = Camera2Config.PATH_SAVE_PIC;
            File file=new File(path);
            File[] tempList = file.listFiles();
            ArrayList<File> list = new ArrayList<>();
            int cnt[] = new int[Config.districtCodes.length];
            for(File f:tempList){
                if(f.isFile()&&f.getName().toUpperCase().endsWith(".JPG")){
                    String name = f.getName();

                    String[] splits = name.split("_");
                    if(splits.length<3)continue;

                    if(splits[0].length()!=12)continue;
                    if(!splits[0].equals(Config.studentID))continue;

                    //判断是否为数字
                    Pattern pattern = Pattern.compile("[0-9]*");
                    Matcher isNum = pattern.matcher(splits[0]);
                    if( !isNum.matches() ){
                        continue;
                    }

                    //判断区域码是否合法
                    boolean flag = Config.districtCodesMap.containsKey(splits[1]);
                    if(!flag)continue;

                    Log.e("saveZIP",name);
                    list.add(f);
                    for(int i=0;i<Config.districtCodes.length;i++)
                        if(splits[1].equals(Config.districtCodes[i])){
                            cnt[i]++;
                            break;
                        }
                }
            }
            for(int i=0;i<cnt.length;i++)
                if(cnt[i]<4){
                    Toast.makeText(mainActivity, "文件数量有误", Toast.LENGTH_SHORT).show();
                    return;
                }
            ZipUtil.generateZip(fos,list);
        } catch (Exception e) {
            Toast.makeText(mainActivity, "保存时出错", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(mainActivity, "保存成功，在："+zipSavePath, Toast.LENGTH_SHORT).show();
    }

    public boolean checkOK(){
        getImgsCount();
        boolean flag=false;
        for(int i=0;i<imgsCount.length;i++)
            if(imgsCount[i]<4)flag=true;
        if(flag){
            Toast.makeText(mainActivity, "还有未完成的拍照，注意括号内数字未位拍的张数", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mainActivity, "已经完成任务，可继续拍照，添加更多", Toast.LENGTH_SHORT).show();
        }
        return !flag;
    }

    public void getImgsCount(){
        String path = Camera2Config.PATH_SAVE_PIC;
        File file=new File(path);
        File[] tempList = file.listFiles();
        imgsCount = new int[Config.districtCodes.length];
        for(File f:tempList){
            if(f.isFile()&&f.getName().toUpperCase().endsWith(".JPG")){
                String name = f.getName();

                String[] splits = name.split("_");
                if(splits.length<3)continue;

                if(splits[0].length()!=12)continue;
                //Log.e("studentID","stID:"+Config.studentID+" spl:"+splits[0]);
                if(!splits[0].equals(Config.studentID))continue;

                //判断是否为数字
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(splits[0]);
                if( !isNum.matches() ){
                    continue;
                }
                //判断区域码是否合法
                boolean flag = Config.districtCodesMap.containsKey(splits[1]);
                if(!flag)continue;

                Log.e("getImgsCount",name);
                imgsCount[Config.districtCodesMap.get(splits[1])]++;
            }
        }
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}
