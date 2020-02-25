package cabdriver.max.com.cabdriver;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by root on 3/5/17.
 */

public class CarstatusUpdateAsync extends AsyncTask<Object, Object, Void> {
    String sStatus;
    ProgressDialog mProgressDialog;
    public CarstatusUpdateAsync(String status){
        this.sStatus = status;
    }

    @Override
    protected Void doInBackground(Object... params) {
        try {
            updateStatus();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(MainActivity.aActivity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle("updating cab");
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.show();
        super.onPreExecute();
    }

    private void updateStatus() throws JSONException, IOException {
        SharedPreferences mSharedPreferences = MainActivity.aActivity.getSharedPreferences("Cab", Context.MODE_PRIVATE);
        OkHttpClient client = new OkHttpClient();
        Log.d("requesting _>", "done");
        RequestBody body = new FormBody.Builder()
                .add("availability", sStatus)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.13:8000/api/cars/" + String.valueOf(mSharedPreferences.getInt("carID", 0)) + "/update")
                .put(body)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.code() == 200) {
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mProgressDialog.cancel();
        super.onPostExecute(aVoid);
    }
}
