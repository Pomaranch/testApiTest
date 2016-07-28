package com.example.piekie.myapplication;

import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    String userID = "";
    Gson gson;
    APIService service;
    List<Img> images;
    String fileName;
    GsonParce result;

    private static final String TAG = "MainActivity";

    public interface APIService {
        @POST("/api/login")
        Call<ResponseBody> loadId();

        @GET("/api/gallery/list")
        Call<GsonParce> loadJsonImage(@Header("user_id") String userId);

        @GET("/api/gallery/{id}")
        Call<ResponseBody> loadImages(@Path("id") Integer id, @Header("user_id") String userId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        gson = new Gson();
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient build = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(String.class, new JsonDeserializer<String>() {
//            @Override
//            public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                Log.e("string", json.toString());
//                String asString = json.getAsString();
//                return asString;
//            }
//        });

// GsonConverterFactory factory = GsonConverterFactory.create(gsonBuilder.create());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://testapi.us/")
// .client(build)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(APIService.class);

        getUserID();
        if (!userID.equals("")) {
            if (!getJsonImageList().equals(null)) {
                images = result.getImages();
                for (int i = 0; i < images.size(); i++) {
                }
            }
        }

    }

    private void getUserID() {
        service.loadId().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    userID = response.body().string();
                    Log.wtf("MYID", userID);

                    getJsonImageList();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.wtf("MYID", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private GsonParce getJsonImageList() {

        service.loadJsonImage(userID).enqueue(new Callback<GsonParce>() {
            @Override
            public void onResponse(Call<GsonParce> call, Response<GsonParce> response) {

                if (response.isSuccessful()) {
                    result = response.body();
                    Log.wtf("I'M SO COOL", result.getTimestamp());


                    images = result.getImages();

                    loadImage(0);
                } else Log.wtf("LOOOSER", response.message());

            }

            @Override
            public void onFailure(Call<GsonParce> call, Throwable t) {
                Log.wtf("BITCH", t.getMessage());
            }
        });
        return result;
    }

    private void loadImage(final int i) {
        if (i >= images.size())
            return;


        fileName = images.get(i).getTitle() + ".jpeg";
        Log.i(TAG, fileName);
        service.loadImages(images.get(i).getId(), userID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        File path = Environment.getExternalStorageDirectory();
                        File file = new File(path, fileName);
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        IOUtils.write(response.body().bytes(), fileOutputStream);

                        Log.wtf("DONE", fileName);

                        //TODO: remove this hotfix
                        loadImage(i + 1);
                    } catch (IOException e) {
                        Log.wtf("NO PHOTO NO RICH", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}