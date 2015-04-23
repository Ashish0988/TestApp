package co.notifie.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.realm.RealmResults;

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

        TextView userNameText = (TextView) header.findViewById(R.id.user_full_name_in_settings);
        TextView userPhoneText = (TextView) header.findViewById(R.id.user_global_phone);
        avatarButton = (ImageButton) header.findViewById(R.id.avatar);

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

        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
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
}
