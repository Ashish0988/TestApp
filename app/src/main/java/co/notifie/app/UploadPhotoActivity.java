package co.notifie.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UploadPhotoActivity extends ActionBarActivity {

    EditText userName;
    ProgressWheel progress;
    FancyButton signUpButton;
    FancyButton faceBookButton;
    ImageButton avatarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        signUpButton = (FancyButton) findViewById(R.id.final_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAction();
            }
        });

        faceBookButton = (FancyButton) findViewById(R.id.facebook_button);
        faceBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);
        userName = (EditText)  findViewById(R.id.full_name);
        avatarButton = (ImageButton) findViewById(R.id.avatar);

        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        searchForClients();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_upload_photo, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // your code.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveAction() {
        if (userName.getText().length() == 0) {
            userName.setError(getString(R.string.cant_be_blank));
        } else {
            Intent intent = new Intent(getBaseContext(), SwipeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void searchForClients() {
        RestClient.get().searchForClient(MainActivity.AUTH_TOKEN, new Callback<SearchClientsResponce>() {
            @Override
            public void success(SearchClientsResponce responce, Response response) {
                // success!
                List <NotifieCustomer> names = responce.getFound();
                Log.i("searchForClients", names.toString());

                if (names != null && names.size() > 0) {
                    NotifieCustomer first = names.get(0);
                    userName.setText(first.getFullname());
                }

                progress.setVisibility(View.GONE);
                signUpButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong

                Toast toast = Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public int filter_option;

    public void showFilterDialog() {

        String[] options = new String[] {
                getText(R.string.take_photo).toString(), getText(R.string.take_photo_from_facebook).toString()
        };


        final ListAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.select_dialog_singlechoice, android.R.id.text1, options);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(adapter,
                        filter_option,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                filter_option = which;
                            }
                        })
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                switch (filter_option) {
                                    case 0:
                                        //Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(takePhoto, 1);//one can be replaced with any action code
                                        break;
                                    case 1:
                                        openFacebookSession();
                                        break;

                                }

                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                .show();

        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(R.color.actionbar_background);
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(R.color.actionbar_background);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (Session.getActiveSession() != null) {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, imageReturnedIntent);
        }

        if (requestCode == 1 && resultCode != 0)
        {
            Uri imageUri = imageReturnedIntent.getData();

            String real_path = getPath(getBaseContext(), imageUri);
            File file = new File(real_path);

            TypedFile typed_file = new TypedFile("image/*", file);
            postAvatar(typed_file);

            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                Picasso.with(getBaseContext()) // getBaseContext()
                        .load(imageUri)
                        .transform(new CircleTransform())
                        .resize(avatarButton.getLayoutParams().width, avatarButton.getLayoutParams().height)
                        .centerCrop()
                        .into(avatarButton);

            } catch (Exception e) {

            }
        }
    }

    private void openFacebookSession(){

        Session.openActiveSession(this, true, new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (exception != null) {
                    Log.d("Facebook", exception.getMessage());
                }
                Log.d("Facebook", "Session State: " + session.getState());
                // you can make request to the /me API or do other stuff like post, etc. here
                if (session.isOpened()) {

                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser graphUser, com.facebook.Response response) {
                            try {
                                String imgUrl = "http://graph.facebook.com/" + graphUser.getId() + "/picture?type=large";


                                Picasso.with(getBaseContext())
                                        .load(imgUrl)
                                        .transform(new CircleTransform())
                                        .resize(avatarButton.getLayoutParams().width, avatarButton.getLayoutParams().height)
                                        .centerCrop()
                                        .into(avatarButton);

                                Target target = new Target() {

                                    @Override
                                    public void onPrepareLoad(Drawable arg0) {
                                        return;
                                    }

                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                                        try {
                                            File file = new File(Environment.getExternalStorageDirectory().getPath() +"/" + "user_avatar.jpg");

                                            file.createNewFile();
                                            FileOutputStream ostream = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                            ostream.close();

                                            TypedFile typed_file = new TypedFile("image/*", file);
                                            postAvatar(typed_file);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable arg0) {
                                        return;
                                    }
                                };

                                Picasso.with(getBaseContext())
                                        .load(imgUrl)
                                        .into(target);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    public void postAvatar(TypedFile avatar) {

        RestClient.get().postAvatar(MainActivity.AUTH_TOKEN, avatar, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                // success!

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong

                Log.e("RetrofitError.....", error.toString());
                Toast toast = Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
