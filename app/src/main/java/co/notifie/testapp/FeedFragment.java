package co.notifie.testapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FeedFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int page;
    private String title;

    private OnFragmentInteractionListener mListener;
    private static SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    //private ListAdapter mAdapter;
    private FeedAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static FeedFragment newInstance(int page) {
        FeedFragment fragment = new FeedFragment();
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
    public FeedFragment() {
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

        RealmResults<NotifeMessage> messages;

        if (page == 1) { // Favorites
            messages = MainActivity.realm.where(NotifeMessage.class)
                    .equalTo("favorited", "true")
                    .findAll();

        } else { // Feed
            messages = MainActivity.realm.where(NotifeMessage.class)
                    .findAll();
        }

        messages.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);

        mAdapter = new FeedAdapter(getActivity(), R.layout.message_cell, messages, true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        //
        // Add Header To View
        //
        /*
        ListView listview = (ListView) view.findViewById(R.id.listview);
        final ViewGroup header = (ViewGroup)inflater.inflate(R.layout.feed_segment, listview, false);
        listview.addHeaderView(header, null, false);
        */

        final FloatingActionsMenu actionsMenu = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);


        if (page == 1) { // Favorites
            actionsMenu.setVisibility(View.INVISIBLE);

        } else { // Feed

            FloatingActionButton filterButton = (FloatingActionButton) view.findViewById(R.id.action_filter);
            filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionsMenu.collapse();
                    showFilterDialog();
                }
            });

        }

        // Create Swipe Refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorScheme(R.color.actionbar_background);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadMessages();
                }
            });
        }

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.listview);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        View empty_view = view.findViewById(R.id.empty_list);
        mListView.setEmptyView(empty_view);

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

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);

            final NotifeMessage item = mAdapter.getRealmResults().get(position);

            Intent intent = new Intent(getActivity(), DisplayMessageActivity.class);
            intent.putExtra(EXTRA_MESSAGE, item.getId());
            startActivity(intent);
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

    //
    // Load Messages From Server
    //
    public void loadMessages() {

        RestClient.get().getMessages(MainActivity.AUTH_TOKEN, 1, 50, new Callback<MessagesResponce>() {
            @Override
            public void success(MessagesResponce messagesResponce, Response response) {
                // success!
                List<NotifeMessage> messages = messagesResponce.getMessages();

                //
                // Store At Database
                //

                MainActivity.realm.beginTransaction();
                MainActivity.realm.copyToRealmOrUpdate(messages);
                MainActivity.realm.commitTransaction();

                /*
                RealmResults<NotifeMessage> result2 =
                        realm.where(NotifeMessage.class)
                                .findAll();

                result2.sort("id", RealmResults.SORT_ORDER_DESCENDING);

                Log.i("RealmResults = ", result2.toString());
                */

                /*
                for (NotifeMessage message : messages) {
                    //list.add(message.getShort_title());
                    Log.i("App message = ", message.getId());
                }
                */

                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error body:" + error.getBody());
            }
        });

    }

    public void showFilterDialog() {
        //CharSequence[] array = {"Все сообщения", "Новые сообщения", "С новыми комментариями"};

        final ListAdapter adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.select_dialog_singlechoice, android.R.id.text1, new String[] {
                "Все сообщения (124)", "Новые сообщения (нет)", "С новыми комментариями (3)"
        });

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(adapter,
                        -1,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked Yes so do some stuff */
                            }
                        })
                .setNegativeButton("ОТМЕНА",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked No so do some stuff */
                            }
                        })
                .show();

        // Change the title divider
        /*
        final Resources res = getResources();
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = dialog.findViewById(titleDividerId);
        titleDivider.setBackgroundColor(res.getColor(R.color.actionbar_background));
        */
    }

}
