package co.notifie.app;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import io.realm.RealmResults;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by thunder on 20.04.15.
 */
public class SettingsFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int page;
    private String title;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    ImageButton avatarButton;
    FancyButton photoButton;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    //private ListAdapter mAdapter;
    private ClientAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static SettingsFragment newInstance(int page) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, page);
        //args.putString(ARG_PARAM2, title);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            page = getArguments().getInt(ARG_PARAM1);
            //title = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        /*
        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
                */

        RealmResults<NotifieClient> clients = MainActivity.realm.where(NotifieClient.class)
                .findAll();

        clients.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);

        mAdapter = new ClientCompactAdapter(getActivity(), R.layout.message_cell, clients, true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_no_button, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.listview);

        final ListView listview = (ListView) view.findViewById(R.id.listview);

        final ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header_for_settings, listview, false);
        listview.addHeaderView(header, null, false);

        Notifie app = ((Notifie) getActivity().getApplicationContext());
        User currentUser = app.getCurrentUser();

        if (currentUser == null) {

            MainActivity.logOut(getActivity().getBaseContext());
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return view;
        }

        TextView userNameText = (TextView) header.findViewById(R.id.user_full_name_in_settings);
        TextView userPhoneText = (TextView) header.findViewById(R.id.user_global_phone);
        avatarButton = (ImageButton) header.findViewById(R.id.avatar);

        photoButton = (FancyButton) header.findViewById(R.id.change_photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        /*
        ImageButton hiddenSettings = (ImageButton) header.findViewById(R.id.hidden_settings);
        hiddenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(hiddenSettings);
            }
        });
        */

        if (userNameText != null) {
            userNameText.setText(currentUser.getFull_name());
        }

        if (userPhoneText != null) {
            userPhoneText.setText(currentUser.getGlobal_phone());
        }

        String image_url = MainActivity.NOTIFIE_HOST + currentUser.getAvatar_url();

        try {
            if (image_url != null && image_url.length() != 0) {
                Picasso.with(getActivity()) // getBaseContext()
                        .load(image_url)
                        .transform(new CircleTransform())
                        .resize(avatarButton.getLayoutParams().width, avatarButton.getLayoutParams().height)
                        .centerCrop()
                        .into(avatarButton);
            }
        } catch (IllegalArgumentException e) {
            Log.v("Path", image_url);
        }

        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFilterDialog();
            }
        });

        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == 1 && resultCode != 0)
        {
            Uri imageUri = imageReturnedIntent.getData();
            String real_path = getPath(getActivity(), imageUri);
            File file = new File(real_path);

            TypedFile typed_file = new TypedFile("image/*", file);
            postAvatar(typed_file, ((Notifie) getActivity().getApplicationContext()));

        }
    }

    public void prepareAvatar(final Uri img_url, final ImageButton avatar_button, final Activity activity) {

        final Notifie notifie_app = ((Notifie) activity.getApplicationContext());

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
                    ostream.getFD().sync();
                    ostream.close();

                    TypedFile typed_file = new TypedFile("image/*", file);

                    postAvatar(typed_file, notifie_app);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable arg0) {
                return;
            }
        };

        Picasso.with(activity)
                .load(img_url)
                .transform(new CircleTransform())
                .resize(avatar_button.getLayoutParams().width, avatar_button.getLayoutParams().height)
                .centerCrop()
                .into(target);
    }

    private void openFacebookSession(){

        Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {

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
                                String addr = "http://graph.facebook.com/" + graphUser.getId() + "/picture?type=large";
                                Uri image_url = Uri.parse(addr);

                                prepareAvatar(image_url, avatarButton, getActivity());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }


    public void postAvatar(TypedFile avatar, final Notifie notifie_app) {

            RestClient.get().postAvatar(MainActivity.AUTH_TOKEN, avatar, new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    // success!
                    notifie_app.setCurrentUser(user);

                    String image_url = MainActivity.NOTIFIE_HOST + user.getAvatar_url();
                    if (image_url != null && image_url.length() != 0) {
                        Picasso.with(getActivity())
                                .load(image_url)
                                .transform(new CircleTransform())
                                .resize(avatarButton.getLayoutParams().width, avatarButton.getLayoutParams().height)
                                .centerCrop()
                                .into(avatarButton);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    // something went wrong

                    Log.e("RetrofitError.....", error.toString());
                }
            });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.CLIENT";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);

            final NotifieClient item = mAdapter.getRealmResults().get(position);

            Intent intent = new Intent(getActivity(), DisplayMessageActivity.class);
            intent.putExtra(EXTRA_MESSAGE, item.getId());
            //startActivity(intent);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
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

    public int filter_option;

    public void showFilterDialog() {

        String[] options = new String[] {
                getText(R.string.take_photo).toString(), getText(R.string.take_photo_from_facebook).toString()
        };


        final ListAdapter adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.select_dialog_singlechoice, android.R.id.text1, options);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
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

}
