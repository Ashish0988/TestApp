package co.notifie.app;

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

    private static final int LOAD_PER_PAGE = 25;

    // TODO: Rename and change types of parameters
    private int page;
    private String title;

    private OnFragmentInteractionListener mListener;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private static RealmResults<NotifeMessage> new_messages;

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
    private FeedAdapter mAdapter;

    RealmResults<NotifeMessage> messages;

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


        if (page == 1) { // Favorites
            messages = MainActivity.realm.where(NotifeMessage.class)
                    .equalTo("favorited", "true")
                    .findAll();

        } else { // Feed

            /*
            RealmQuery<NotifieClient> query = MainActivity.realm.where(NotifieClient.class);
            query.equalTo("check_for_notifie", "1");
            //query.or().equalTo("id", "2");
            //query.or().equalTo("id", "3");
            RealmResults<NotifieClient> clients = query.findAll();

            RealmQuery<NotifeMessage> msg_query = MainActivity.realm.where(NotifeMessage.class);
            */

            messages = MainActivity.realm.where(NotifeMessage.class)
                    .equalTo("client.check_for_notifie", "1")
                    .findAll();

            /*
            if (clients.size() >= 1) {
                msg_query.equalTo("client_id", clients.get(0).getId());

                for (int i = 1; i < clients.size(); i++) {
                    msg_query.or().equalTo("client_id", clients.get(i).getId());
                }

                messages = msg_query.findAll();
            } else {
                messages = MainActivity.realm.where(NotifeMessage.class)
                        .equalTo("favorited", "never_found_any")
                        .findAll();
            }*/
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

        filter_header = (TextView) view.findViewById(R.id.feed_header_text_1);

        final FloatingActionsMenu actionsMenu = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);


        if (page == 1) { // Favorites
            actionsMenu.setVisibility(View.INVISIBLE);

            filter_header.setText(R.string.favorites_filter);

        } else { // Feed

            options = new String[] {getText(R.string.all_messages_filter) + " (" + messages.size() + ")",
                    getText(R.string.new_messages_filter) + "", getText(R.string.new_comments_filter) + ""};

            filter_header.setText(options[MainActivity.filter_option]);

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
                    loadMessages(1, LOAD_PER_PAGE);
                }
            });
        }

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.listview);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadMessages(page, LOAD_PER_PAGE);
                Log.v("Loading.......", "page = " + page + " totalItemsCount= " + totalItemsCount);
            }
        });

        View empty_view = view.findViewById(R.id.empty_list);
        mListView.setEmptyView(empty_view);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        loadMessages(1, LOAD_PER_PAGE);

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
    public void loadMessages(int page, int per_page) {

        RestClient.get().getMessages(MainActivity.AUTH_TOKEN, page, per_page, new Callback<MessagesResponce>() {
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

        long total_count = messages.size();
        long count = messages.where().greaterThan("unread_comments_sum", 0).findAll().size();
        long new_count = messages.where().equalTo("open_at", "").findAll().size();

        /*
        String[] options = new String[] {
                "Все сообщения (" + total_count + ")", "Новые сообщения (" + new_count + ")", "С новыми комментариями (" + count + ")"
        };*/

        options[0] = getText(R.string.all_messages_filter) + " (" + total_count + ")";
        options[1] = getText(R.string.new_messages_filter)  + " (" + new_count + ")";
        options[2] = getText(R.string.new_comments_filter) + " (" + count + ")";

        final ListAdapter adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.select_dialog_singlechoice, android.R.id.text1, options);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(adapter,
                        MainActivity.filter_option,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                MainActivity.filter_option = which;
                            }
                        })
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                filter_header.setText(options[MainActivity.filter_option]);

                                switch (MainActivity.filter_option) {
                                    case 0:
                                        new_messages = MainActivity.realm.where(NotifeMessage.class)
                                                    .equalTo("client.check_for_notifie", "1")
                                                    .findAll();
                                        new_messages.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);
                                        mAdapter.updateRealmResults(new_messages);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    case 1:
                                        new_messages = MainActivity.realm.where(NotifeMessage.class)
                                                .equalTo("client.check_for_notifie", "1")
                                                .equalTo("open_at", "")
                                                .findAll();
                                        new_messages.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);
                                        mAdapter.updateRealmResults(new_messages);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    case 2:
                                        new_messages = MainActivity.realm.where(NotifeMessage.class)
                                                .equalTo("client.check_for_notifie", "1")
                                                .greaterThan("unread_comments_sum", 0)
                                                .findAll();
                                        new_messages.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);
                                        mAdapter.updateRealmResults(new_messages);
                                        mAdapter.notifyDataSetChanged();
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

        // Change the title divider
        /*
        final Resources res = getResources();
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = dialog.findViewById(titleDividerId);
        titleDivider.setBackgroundColor(res.getColor(R.color.actionbar_background));
        */
    }

}
