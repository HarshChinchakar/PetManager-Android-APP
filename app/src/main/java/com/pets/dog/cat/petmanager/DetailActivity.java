//package com.pets.dog.cat.petmanager;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Typeface;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Base64;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//// --- 1. UPDATED DATA MODEL (Matches your new requirements) ---
//class PetLogEntry {
//    String date;
//    String breed_emotion; // Breed + Emotional State
//    String care;          // Meds + Diet
//    String visual_desc;   // 30-word description
//
//    public PetLogEntry(String breed_emotion, String care, String visual_desc) {
//        this.date = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(new Date());
//        this.breed_emotion = breed_emotion;
//        this.care = care;
//        this.visual_desc = visual_desc;
//    }
//}
//
//public class DetailActivity extends AppCompatActivity {
//
//    private String name;
//    private String profilePictureUri;
//    private int weight;
//    private int gender;
//    private String breed;
//    private String dob;
//    private int age;
//
//    private LinearLayout logsContainer;
//    private static final int PICK_IMAGE_REQUEST = 101;
//
//    // --- API CONFIGURATION ---
//    private static final String GROQ_API_KEY = "Groq api key here";
//    private static final String GROQ_MODEL = "meta-llama/llama-4-maverick-17b-128e-instruct";
//
//    private final OkHttpClient client = new OkHttpClient.Builder()
//            .connectTimeout(60, TimeUnit.SECONDS)
//            .writeTimeout(60, TimeUnit.SECONDS)
//            .readTimeout(60, TimeUnit.SECONDS)
//            .build();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_details);
//
//        // --- EXISTING SETUP ---
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            name = extras.getString("name");
//            profilePictureUri = extras.getString("profilePictureUri");
//            weight = extras.getInt("weight");
//            gender = extras.getInt("gender");
//            breed = extras.getString("breed");
//            dob = extras.getString("dob");
//            age = extras.getInt("age");
//        }
//
//        setupUI();
//
//        logsContainer = findViewById(R.id.logs_container);
//        FloatingActionButton btnAddLog = findViewById(R.id.btnAddLog);
//
//        if (btnAddLog != null) {
//            btnAddLog.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType("image/*");
//                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
//                }
//            });
//        }
//
//        loadLogs();
//    }
//
//    private void setupUI() {
//        Toolbar toolbar = findViewById(R.id.p_toolbar);
//        ImageView profileImageView = findViewById(R.id.profile_imageview);
//        TextView weightTvw = findViewById(R.id.weight_tvw);
//        TextView genderTvw = findViewById(R.id.gender_tvw);
//        TextView breedTvw = findViewById(R.id.breed_tvw);
//        TextView dobTvw = findViewById(R.id.dob_tvw);
//        TextView ageTvw = findViewById(R.id.age_tvw);
//
//        if (weightTvw != null) weightTvw.setText(weight + getString(R.string.klgrms));
//        if (genderTvw != null) {
//            if (gender == 1) genderTvw.setText(R.string.fml);
//            else genderTvw.setText(R.string.mle);
//        }
//        if (breedTvw != null) breedTvw.setText(breed);
//
//        if (dobTvw != null && dob != null && dob.length() >= 10) {
//            try {
//                String day = dob.substring(0, 2);
//                String monthN = dob.substring(3, 5);
//                String monthIT = getMonthInText(monthN);
//                String year = dob.substring(6, 10);
//                dobTvw.setText(day + " " + monthIT + " " + year);
//            } catch (Exception e) {
//                dobTvw.setText(dob);
//            }
//        }
//
//        if (ageTvw != null) ageTvw.setText(age + getString(R.string.yrs));
//
//        toolbar.setTitle(name);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        try {
//            if (profilePictureUri != null) {
//                Bitmap profilePBitmap = decodeUri(this, Uri.parse(profilePictureUri), 300);
//                profileImageView.setImageBitmap(profilePBitmap);
//            }
//        } catch (IOException e) { e.printStackTrace(); }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri imageUri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                Toast.makeText(this, "Analyzing Pet...", Toast.LENGTH_SHORT).show();
//                analyzeImageWithGroq(bitmap);
//            } catch (IOException e) { e.printStackTrace(); }
//        }
//    }
//
//    // --- 2. UPDATED PROMPT & API LOGIC ---
//    private void analyzeImageWithGroq(Bitmap originalBitmap) {
//        Bitmap resizedBitmap = resizeBitmap(originalBitmap, 512);
//        String base64Image = encodeImage(resizedBitmap);
//
//        try {
//            JSONObject textContent = new JSONObject();
//            textContent.put("type", "text");
//            // STRICT PROMPT matching your new requirements
//            textContent.put("text", "Analyze this pet. Return a valid JSON object (no markdown) with these exact keys:\n" +
//                    "1. 'breed_emotion': The breed and visible emotional state.\n" +
//                    "2. 'care': Any usual medications and normal diet for this type of pet.\n" +
//                    "3. 'visual_desc': A natural language description of the pet (approx 30 words).");
//
//            JSONObject imageUrl = new JSONObject();
//            imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
//
//            JSONObject imageContent = new JSONObject();
//            imageContent.put("type", "image_url");
//            imageContent.put("image_url", imageUrl);
//
//            JSONArray contentArray = new JSONArray();
//            contentArray.put(textContent);
//            contentArray.put(imageContent);
//
//            JSONObject message = new JSONObject();
//            message.put("role", "user");
//            message.put("content", contentArray);
//
//            JSONArray messagesArray = new JSONArray();
//            messagesArray.put(message);
//
//            JSONObject finalBody = new JSONObject();
//            finalBody.put("model", GROQ_MODEL);
//            finalBody.put("messages", messagesArray);
//            finalBody.put("temperature", 0.1);
//
//            String jsonBodyString = finalBody.toString();
//            Log.d("GROQ_REQ", jsonBodyString);
//
//            RequestBody body = RequestBody.create(jsonBodyString, MediaType.get("application/json; charset=utf-8"));
//            Request request = new Request.Builder()
//                    .url("https://api.groq.com/openai/v1/chat/completions")
//                    .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
//                    .addHeader("Content-Type", "application/json")
//                    .post(body)
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    showErrorDialog("Network Error", e.getMessage());
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String responseData = response.body().string();
//
//                    if (response.isSuccessful()) {
//                        try {
//                            Log.d("GROQ_RES", responseData);
//
//                            JSONObject json = new JSONObject(responseData);
//                            String content = json.getJSONArray("choices")
//                                    .getJSONObject(0)
//                                    .getJSONObject("message")
//                                    .getString("content");
//
//                            // 3. SAFETY CLEANUP (Prevents Crashes)
//                            // Remove markdown code blocks if AI adds them
//                            if (content.contains("```")) {
//                                content = content.replace("```json", "").replace("```", "").trim();
//                            }
//
//                            // Strict Substring: Only take what is between the first { and last }
//                            int start = content.indexOf("{");
//                            int end = content.lastIndexOf("}");
//                            if (start >= 0 && end >= 0) {
//                                content = content.substring(start, end + 1);
//                                saveAndShowLog(content);
//                            } else {
//                                throw new Exception("No JSON found in response");
//                            }
//
//                        } catch (Exception e) {
//                            showErrorDialog("Parsing Error", "AI Response invalid:\n" + e.getMessage());
//                        }
//                    } else {
//                        showErrorDialog("Groq Error " + response.code(), responseData);
//                    }
//                }
//            });
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            showErrorDialog("Internal Error", "Failed to build JSON request.");
//        }
//    }
//
//    private void showErrorDialog(final String title, final String message) {
//        runOnUiThread(() -> {
//            new AlertDialog.Builder(DetailActivity.this)
//                    .setTitle(title)
//                    .setMessage(message)
//                    .setPositiveButton("OK", null)
//                    .show();
//        });
//    }
//
//    private Bitmap resizeBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//        float bitmapRatio = (float) width / (float) height;
//        if (bitmapRatio > 1) {
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//    }
//
//    private String encodeImage(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
//    }
//
//    // --- SAVE & DISPLAY ---
//    private void saveAndShowLog(String jsonString) {
//        try {
//            Gson gson = new Gson();
//            PetLogEntry newLog;
//            try {
//                newLog = gson.fromJson(jsonString, PetLogEntry.class);
//            } catch (Exception e) {
//                // If JSON fails, create a fallback entry so app doesn't crash
//                newLog = new PetLogEntry("Analysis Failed", "Please try again", "Could not parse AI response.");
//            }
//
//            if (newLog == null) return;
//
//            saveLogToFile(newLog);
//            PetLogEntry finalLog = newLog;
//            runOnUiThread(() -> addLogView(finalLog));
//
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//
//    private void saveLogToFile(PetLogEntry newLog) {
//        File file = new File(getFilesDir(), "logs_" + name + ".json");
//        Gson gson = new Gson();
//        List<PetLogEntry> logs = new ArrayList<>();
//
//        if (file.exists()) {
//            try {
//                byte[] bytes = new byte[(int) file.length()];
//                java.io.FileInputStream fis = new java.io.FileInputStream(file);
//                fis.read(bytes);
//                fis.close();
//                String content = new String(bytes);
//                Type listType = new TypeToken<ArrayList<PetLogEntry>>(){}.getType();
//                logs = gson.fromJson(content, listType);
//            } catch (Exception e) { e.printStackTrace(); }
//        }
//
//        if (logs == null) logs = new ArrayList<>();
//        logs.add(0, newLog);
//
//        try {
//            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
//            fos.write(gson.toJson(logs).getBytes());
//            fos.close();
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//
//    private void loadLogs() {
//        File file = new File(getFilesDir(), "logs_" + name + ".json");
//        if (!file.exists()) return;
//        try {
//            byte[] bytes = new byte[(int) file.length()];
//            java.io.FileInputStream fis = new java.io.FileInputStream(file);
//            fis.read(bytes);
//            fis.close();
//            String content = new String(bytes);
//            Type listType = new TypeToken<ArrayList<PetLogEntry>>(){}.getType();
//            List<PetLogEntry> logs = new Gson().fromJson(content, listType);
//            if (logs != null) {
//                for (PetLogEntry log : logs) {
//                    addLogView(log);
//                }
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//
//    // --- 4. IMPROVED UI FOR LOGS ---
//    private void addLogView(PetLogEntry log) {
//        if (logsContainer == null) return;
//
//        TextView tv = new TextView(this);
//
//        // Formatting the text nicely
//        String displayText =
//                "📅 " + log.date + "\n\n" +
//                        "🐶 BREED & MOOD:\n" + log.breed_emotion + "\n\n" +
//                        "💊 CARE & DIET:\n" + log.care + "\n\n" +
//                        "📝 DESCRIPTION:\n" + log.visual_desc + "\n" +
//                        "_____________________________";
//
//        tv.setText(displayText);
//        tv.setPadding(40, 40, 40, 40);
//        tv.setTextSize(14f);
//        tv.setTextColor(android.graphics.Color.BLACK);
//        tv.setTypeface(null, Typeface.NORMAL);
//
//        // Add to top
//        logsContainer.addView(tv, 0);
//    }
//
//    private String getMonthInText(String dString) {
//        switch (dString) {
//            case "01": return getString(R.string.jan);
//            case "02": return getString(R.string.feb);
//            case "03": return getString(R.string.mrch);
//            case "04": return getString(R.string.aprl);
//            case "05": return getString(R.string.my);
//            case "06": return getString(R.string.jne);
//            case "07": return getString(R.string.jly);
//            case "08": return getString(R.string.agst);
//            case "09": return getString(R.string.sptmb);
//            case "10": return getString(R.string.oct);
//            case "11": return getString(R.string.nov);
//            case "12": return getString(R.string.dec);
//            default: return getString(R.string.jan);
//        }
//    }
//
//    public Bitmap decodeUri(Context context, Uri uri, final int requiredSize) throws FileNotFoundException {
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o);
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
//        int scale = 1;
//        while (true) {
//            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize) break;
//            width_tmp /= 2;
//            height_tmp /= 2;
//            scale *= 2;
//        }
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o2);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return false;
//    }
//}

package com.pets.dog.cat.petmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class PetLogEntry {
    String date;
    String breed_emotion;
    String care;
    String visual_desc;

    public PetLogEntry(String breed_emotion, String care, String visual_desc) {
        this.date = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(new Date());
        this.breed_emotion = breed_emotion;
        this.care = care;
        this.visual_desc = visual_desc;
    }
}

public class DetailActivity extends AppCompatActivity {

    private String name;
    private String profilePictureUri;
    private int weight;
    private int gender;
    private String breed;
    private String dob;
    private int age;

    private LinearLayout logsContainer;
    private static final int PICK_IMAGE_REQUEST = 101;

    private static final String GROQ_API_KEY = "groq api key here";
    // STABLE 2026 MODEL
    private static final String GROQ_MODEL = "meta-llama/llama-4-scout-17b-16e-instruct";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            profilePictureUri = extras.getString("profilePictureUri");
            weight = extras.getInt("weight");
            gender = extras.getInt("gender");
            breed = extras.getString("breed");
            dob = extras.getString("dob");
            age = extras.getInt("age");
        }

        setupUI();

        logsContainer = findViewById(R.id.logs_container);
        FloatingActionButton btnAddLog = findViewById(R.id.btnAddLog);

        if (btnAddLog != null) {
            btnAddLog.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            });
        }

        loadLogs();
    }

    private void setupUI() {
        Toolbar toolbar = findViewById(R.id.p_toolbar);
        ImageView profileImageView = findViewById(R.id.profile_imageview);
        TextView weightTvw = findViewById(R.id.weight_tvw);
        TextView genderTvw = findViewById(R.id.gender_tvw);
        TextView breedTvw = findViewById(R.id.breed_tvw);
        TextView dobTvw = findViewById(R.id.dob_tvw);
        TextView ageTvw = findViewById(R.id.age_tvw);

        if (weightTvw != null) weightTvw.setText("⚖️ Weight: " + weight + " Kgs");
        if (genderTvw != null) {
            String genderIcon = (gender == 1) ? "♀️ " : "♂️ ";
            genderTvw.setText(genderIcon + (gender == 1 ? "Female" : "Male"));
        }
        if (breedTvw != null) breedTvw.setText("🐩 Breed: " + breed);

        if (dobTvw != null && dob != null && dob.length() >= 10) {
            try {
                String day = dob.substring(0, 2);
                String monthN = dob.substring(3, 5);
                String monthIT = getMonthInText(monthN);
                String year = dob.substring(6, 10);
                dobTvw.setText("🎂 Birthday: " + day + " " + monthIT + " " + year);
            } catch (Exception e) {
                dobTvw.setText("🎂 Birthday: " + dob);
            }
        }

        if (ageTvw != null) ageTvw.setText("⏳ Age: " + age + " Yrs");

        if (toolbar != null) {
            toolbar.setTitle(name);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        try {
            if (profilePictureUri != null && profileImageView != null) {
                Bitmap profilePBitmap = decodeUri(this, Uri.parse(profilePictureUri), 500);
                profileImageView.setImageBitmap(profilePBitmap);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Toast.makeText(this, "AI Analysis in progress...", Toast.LENGTH_SHORT).show();
                analyzeImageWithGroq(bitmap);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void analyzeImageWithGroq(Bitmap originalBitmap) {
        Bitmap resizedBitmap = resizeBitmap(originalBitmap, 512);
        String base64Image = encodeImage(resizedBitmap);

        try {
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", "Analyze this pet. Return JSON with keys: 'breed_emotion', 'care', 'visual_desc'.");

            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", "data:image/jpeg;base64," + base64Image);

            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            imageContent.put("image_url", imageUrl);

            JSONArray contentArray = new JSONArray();
            contentArray.put(textContent);
            contentArray.put(imageContent);

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", contentArray);

            JSONArray messagesArray = new JSONArray();
            messagesArray.put(message);

            JSONObject finalBody = new JSONObject();
            finalBody.put("model", GROQ_MODEL);
            finalBody.put("messages", messagesArray);
            finalBody.put("temperature", 0.1);

            RequestBody body = RequestBody.create(finalBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    showErrorDialog("Network Error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseData = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(responseData);
                            String content = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

                            if (content.contains("{")) {
                                content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
                                saveAndShowLog(content);
                            }
                        } catch (Exception e) { showErrorDialog("AI Error", "Failed to parse AI output."); }
                    } else {
                        showErrorDialog("API Error", "Response code: " + response.code());
                    }
                }
            });

        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void showErrorDialog(final String title, final String message) {
        runOnUiThread(() -> new AlertDialog.Builder(DetailActivity.this)
                .setTitle(title).setMessage(message).setPositiveButton("OK", null).show());
    }

    private Bitmap resizeBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float ratio = (float) width / (float) height;
        if (ratio > 1) { width = maxSize; height = (int) (width / ratio); }
        else { height = maxSize; width = (int) (height * ratio); }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
    }

    private void saveAndShowLog(String jsonString) {
        try {
            PetLogEntry newLog = new Gson().fromJson(jsonString, PetLogEntry.class);
            if (newLog == null) return;
            saveLogToFile(newLog);
            runOnUiThread(() -> addLogView(newLog));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveLogToFile(PetLogEntry newLog) {
        File file = new File(getFilesDir(), "logs_" + name + ".json");
        List<PetLogEntry> logs = new ArrayList<>();
        if (file.exists()) {
            try {
                byte[] bytes = new byte[(int) file.length()];
                java.io.FileInputStream fis = new java.io.FileInputStream(file);
                fis.read(bytes); fis.close();
                logs = new Gson().fromJson(new String(bytes), new TypeToken<ArrayList<PetLogEntry>>(){}.getType());
            } catch (Exception e) { e.printStackTrace(); }
        }
        if (logs == null) logs = new ArrayList<>();
        logs.add(0, newLog);
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(new Gson().toJson(logs).getBytes());
            fos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadLogs() {
        File file = new File(getFilesDir(), "logs_" + name + ".json");
        if (!file.exists()) return;
        try {
            byte[] bytes = new byte[(int) file.length()];
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            fis.read(bytes); fis.close();
            List<PetLogEntry> logs = new Gson().fromJson(new String(bytes), new TypeToken<ArrayList<PetLogEntry>>(){}.getType());
            if (logs != null) { for (PetLogEntry log : logs) { addLogView(log); } }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- ENHANCED LOG VIEW (Safe & Gorgeous) ---
    private void addLogView(PetLogEntry log) {
        if (logsContainer == null) return;

        TextView tv = new TextView(this);

        // Use Spannable to make headers Bold for an elegant look
        String raw = "📅 " + log.date + "\n\n" +
                "✨ BREED & MOOD\n" + log.breed_emotion + "\n\n" +
                "💊 CARE & DIET\n" + log.care + "\n\n" +
                "📝 DESCRIPTION\n" + log.visual_desc;

        SpannableString span = new SpannableString(raw);
        // Bold the headers
        int breedStart = raw.indexOf("✨");
        int breedEnd = breedStart + 14;
        span.setSpan(new StyleSpan(Typeface.BOLD), breedStart, breedEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int careStart = raw.indexOf("💊");
        int careEnd = careStart + 13;
        span.setSpan(new StyleSpan(Typeface.BOLD), careStart, careEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(span);

        // Layout Params for "Card" spacing
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 32);
        tv.setLayoutParams(params);

        tv.setPadding(48, 48, 48, 48);
        tv.setBackgroundResource(R.drawable.log_card_bg); // Use the new drawable
        tv.setElevation(4f);
        tv.setTextColor(android.graphics.Color.parseColor("#2D3436"));
        tv.setLineSpacing(0f, 1.2f);

        logsContainer.addView(tv, 0); // Always add newest to top
    }

    private String getMonthInText(String dString) {
        switch (dString) {
            case "01": return "Jan"; case "02": return "Feb"; case "03": return "Mar";
            case "04": return "Apr"; case "05": return "May"; case "06": return "Jun";
            case "07": return "Jul"; case "08": return "Aug"; case "09": return "Sep";
            case "10": return "Oct"; case "11": return "Nov"; case "12": return "Dec";
            default: return "Jan";
        }
    }

    public Bitmap decodeUri(Context context, Uri uri, final int requiredSize) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o);
        int scale = 1;
        while (true) {
            if (o.outWidth / 2 < requiredSize || o.outHeight / 2 < requiredSize) break;
            o.outWidth /= 2; o.outHeight /= 2; scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return false;
    }
}