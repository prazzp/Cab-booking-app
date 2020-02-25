package cabdriver.max.com.cabdriver;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by root on 1/5/17.
 */

public class SignUpActivity extends AppCompatActivity{
    public EditText etname, etemail, etmobileno, etpassword;
    public String cname, email, mobileno, password;
    public String result2;
    public ArrayList<String> errors=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etname = (EditText) findViewById(R.id.name);
        etemail = (EditText) findViewById(R.id.email);
        etmobileno = (EditText) findViewById(R.id.mobileno);
        etpassword = (EditText) findViewById(R.id.password);

    }

    public void onClicks(View v) {
        email = etemail.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";

        if (etname.length()<=0) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        }  else if (etpassword.length()<=0) {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
        } else if (etmobileno.length()<=0) {
            Toast.makeText(this, "Please enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
        }  else if (email.matches(emailPattern) || email.matches(emailPattern2)) {

            new MakeNetworkRequestAsyncTask().execute();

        }
        else {
            Toast.makeText(this, "Please enter Valid Email", Toast.LENGTH_SHORT).show();
        }


    }

    private class MakeNetworkRequestAsyncTask extends AsyncTask<Object, Object, Void> {
        // The system calls this to perform work in a worker thread and
        // delivers it the parameters given to AsyncTask.execute()
        public Void doInBackground(Object... params) {
            //In background these both functions will be executed
            try {
                addUserDetailsInDB();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // The system calls this to perform work in the UI thread and
        // delivers the result from doInBackground() method defined above
        @Override
        protected void onPostExecute(Void result) {

            if(result2.equals("created")){
                Toast.makeText(SignUpActivity.this,result2,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(i);
            }else {
                for(String error: errors)
                    Toast.makeText(SignUpActivity.this,error,Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void addUserDetailsInDB() throws JSONException, IOException {
        email = etemail.getText().toString().trim();
        cname = etname.getText().toString();
        mobileno = etmobileno.getText().toString();
        password = etpassword.getText().toString();

        OkHttpClient client2 = new OkHttpClient();
        RequestBody body2 = new FormBody.Builder()
                .add("name", cname)
                .add("email", email)
                .add("contact", mobileno)
                .add("password", password)
                .add("type", "driver")
                .build();
        // Log.d(TAG,"In try "+body2);// Log is used to Display some text in Android Monitor-Logcat below in Android Studio(like printf for Checking)
        Request request2 = new Request.Builder()
                .url("http://192.168.1.13:8000/api/user/create")
                .post(body2)
                .build();
        Response responses = null;

        try {

            responses=client2.newCall(request2).execute();
            //Log.d(TAG,"In try");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert responses != null;
        int resp_code = responses.code();
        Log.d("response code ->", String.valueOf(resp_code));
        String jsonData = responses.body().string();
        Log.d("JSON data -> ", jsonData);
        JSONObject Jobject = new JSONObject(jsonData);
        Log.d("Json Object -> ", String.valueOf(Jobject));
        if(resp_code == 201){
            result2 = "created";
        }else{
            result2 = "failed";
            Iterator<String> keyiter = Jobject.keys();
            errors.clear();
            Log.d("Jobject size ->", String.valueOf(Jobject.length()));
            for(int i=0; i<Jobject.length(); i++ ){
                String key = keyiter.next();
                Log.d(key, Jobject.getString(key));
                errors.add(Jobject.getString(key).replaceAll("\\[|\\]|\"", ""));
            }
        }

    }

}
