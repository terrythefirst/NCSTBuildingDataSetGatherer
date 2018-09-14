package terry.com.ncstbuildingdatasetgatherer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import terry.com.ncstbuildingdatasetgatherer.cameraContents.Camera2RecordActivity;

public class MainActivity extends AppCompatActivity {
    String[] PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
    };
    EditText editText;
    Button confirmBtn;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏模式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //透明导航栏
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        for(String per:PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this,
                    per)!= PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(this, PERMISSIONS,1);
            }
        }


        sharedPreferences=getSharedPreferences("NCSTBuildingSP", Context.MODE_PRIVATE);
        String studentID=sharedPreferences.getString(Config.studentID_SP_KEY,"");
        Log.e("studentID from sp",studentID);
        if(studentID==""){
            setContentView(R.layout.activity_main);

            editText = findViewById(R.id.studentID);
            confirmBtn = findViewById(R.id.confirmBtn);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(v.getId()==R.id.confirmBtn){
                        String ID = editText.getText().toString();
                        Log.e("edit",ID);
                        if(ID.length()!=12){
                            Toast.makeText(MainActivity.this,getString(R.string.studentID_error), Toast.LENGTH_SHORT);
                            return;
                        }
                        editor.putString(Config.studentID_SP_KEY,ID);
                        editor.commit();
                        Config.studentID = ID;
                        new SwitchView(MainActivity.this);
                    }
                }
            });
        }else{
            Config.studentID =studentID;
            new SwitchView(MainActivity.this);
        }
    }



    public void switchToCamera(int district){
        //新建一个Intent对象
        Intent intent =new Intent();
        //指定intent要启动的类
        intent.setClass(MainActivity.this,Camera2RecordActivity.class);
        intent.putExtra("district",district);
        startActivity(intent);
        //关闭当前Activity
        //finish();
    }
}
