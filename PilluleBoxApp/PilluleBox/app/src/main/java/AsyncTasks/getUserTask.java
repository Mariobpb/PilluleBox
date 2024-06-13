package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.pillulebox.Functions;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class getUserTask extends AsyncTask<String, Void, Response> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = Functions.getURL();
    Context contextMain;
    public getUserTask(Context context){
        contextMain = context;
    }
    @Override
    protected Response doInBackground(String... params) {
        Request request = new Request.Builder()
                .url(BASE_URL + "users")
                .get()
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Response response) {
        if (response != null && response.isSuccessful()) {
            // Autenticación exitosa
            toastMessage(":)");
        } else {
            // Autenticación fallida
            toastMessage(":(");
        }
        toastMessage(response.toString());
    }

    private void toastMessage(String message) {
        Toast.makeText(contextMain, message, Toast.LENGTH_SHORT).show();
    }
}