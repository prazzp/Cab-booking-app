package cabdriver.max.com.cabdriver;

import android.app.ProgressDialog;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    public EditText etemail, etpassword;
    public String email,password;
    public String result2;
    public String sname;
    public int nDriverId;
    public int nCarID;
    public long nContact;
    public String sCarModel;
    public String sCarRegNum;
    public String response_error;
    public ProgressDialog mProgressDialog;
    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle("Logging In");
        mProgressDialog.setMessage("Please Wait...");
        session = new UserSessionManager(getApplicationContext());
        LoginCheck();
        etemail = (EditText) findViewById(R.id.email);
        etpassword = (EditText) findViewById(R.id.password);

    }

    public void onClickL(View v) {

        switch (v.getId()) {

            case R.id.submit:

                email = etemail.getText().toString().trim();
//                Log.d(TAG, "email: " + email);
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";

                if (etpassword.length() <= 0) {
                    Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
                } else if (email.matches(emailPattern) || email.matches(emailPattern2)) {

                    email = etemail.getText().toString();
                    password = etpassword.getText().toString();
                    new MakeNetworkRequestAsyncTask().execute();

                } else {
//                    Log.d(TAG, "email:2 " + email);
                    Toast.makeText(this, "Please enter Valid Email", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.signup:
                Intent i2 = new Intent(this, SignUpActivity.class);
                startActivity(i2);

        }


    }

    private void LoginCheck()

    {
        if (session.isUserLoggedIn()) {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

            finish();

        }

    }

    private class MakeNetworkRequestAsyncTask extends AsyncTask<Object, Object, Void> {
        // The system calls this to perform work in a worker thread and
        // delivers it the parameters given to AsyncTask.execute()

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
            super.onPreExecute();
        }

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
            mProgressDialog.dismiss();
            try {
                if (result2.equals("loggedIn")) {
                    session.createUserLoginSession(nDriverId,sname, email, nContact, nCarID, sCarRegNum);
                    Toast.makeText(LoginActivity.this, result2, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, result2, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, response_error, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "something went wrong, please try again later", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void addUserDetailsInDB() throws JSONException, IOException {

        OkHttpClient client2 = new OkHttpClient();
        RequestBody body2 = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("type", "driver")
                .build();
        // Log.d(TAG,"In try "+body2);// Log is used to Display some text in Android Monitor-Logcat below in Android Studio(like printf for Checking)
        Request request2 = new Request.Builder()
                .url("http://192.168.1.13:8000/api/login")
                .post(body2)
                .build();
        Response responses = null;

        try {
//            Log.d(TAG,"Result:1 "+result2);
            Log.i("sending", "request");
            responses = client2.newCall(request2).execute();
            String jsonData = responses.body().string();
            JSONObject Jobject = new JSONObject(jsonData);
            Log.d("jsonobj", String.valueOf(Jobject));
            Log.d("status", Jobject.getString("status"));
            result2 = Jobject.getString("status");
            if(result2.equals("loggedIn")){
                sname = Jobject.getJSONObject("car").getJSONObject("user").getString("name");
                Log.d("driver name", sname);
                nDriverId = Jobject.getJSONObject("car").getJSONObject("user").getInt("id");
                Log.d("driver id ", String.valueOf(nDriverId));
                nContact = Jobject.getJSONObject("car").getJSONObject("user").getLong("contact");
                Log.d("driver contact", String.valueOf(nContact));
                sCarModel = Jobject.getJSONObject("car").getString("model");
                Log.d("car model ", sCarModel);
                sCarRegNum = Jobject.getJSONObject("car").getString("reg_num");
                Log.d("car ren num ", sCarRegNum);
                nCarID = Jobject.getJSONObject("car").getInt("id");
                Log.d("car id ", String.valueOf(nCarID));
            }
            else{
                response_error = Jobject.getString("error");
            }
            //Log.d(TAG,"In try")
        } catch (final IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

}


