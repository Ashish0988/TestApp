package co.notifie.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by thunder on 03.05.15.
 */
public class FavoritesFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int LOAD_PER_PAGE = 25;
    public static Realm realm;

    // TODO: Rename and change types of parameters
    private int page;
    private String title;

    private OnFragmentInteractionListener mListener;

    TextView filter_header;
    String[] options;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    //private ListAdapter mAdapter;
    private FeedAdapter favoriteAdapter;

    RealmResults<NotifeMessage> favorite_messages;

    public static FavoritesFragment newInstance(int page) {
        FavoritesFragment fragment = new FavoritesFragment();
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
    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            page = getArguments().getInt(ARG_PARAM1);
        }

        if (MainActivity.realm == null) {
            realm = Realm.getInstance(getActivity(), MainActivity.REALM_DATABASE);
        } else {
            realm = MainActivity.realm;
        }

        favorite_messages = realm.where(NotifeMessage.class)
                    .equalTo("favorited", "true")
                    .findAll();

        favorite_messages.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);

        favoriteAdapter = new FeedAdapter(getActivity(), R.layout.message_cell, favorite_messages, true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_fragment, container, false);


        filter_header = (TextView) view.findViewById(R.id.favorites_header_text_1);
        filter_header.setText(R.string.favorites_filter);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.favorites_listview);
        ((AdapterView<ListAdapter>) mListView).setAdapter(favoriteAdapter);

        View empty_view = view.findViewById(R.id.favorites_empty_list);
        mListView.setEmptyView(empty_view);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        //loadMessages(1, LOAD_PER_PAGE);

        return view;
    }

    private void showComposeActivity() {
        Intent intent = new Intent(getActivity(), AskActivity.class);
        startActivity(intent);
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

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);

            final NotifeMessage item = favoriteAdapter.getRealmResults().get(position);

            Intent intent = new Intent(getActivity(), DisplayMessageActivity.class);
            intent.putExtra(EXTRA_MESSAGE, item.getId());
            startActivity(intent);
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
