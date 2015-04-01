package co.notifie.testapp;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import 	android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listview = (ListView) findViewById(R.id.listview);

        String[] values = new String[] { "Android Съешь ещё этих мягких французских булок, да выпей чаю. — мем, порождённый русскоязычной версией Windows (вернее, программой fontview.exe, которая входит в дефолтную поставку, начиная аж с незапамятных времён Windows 95). Таким хитроумным способом Винда демонстрирует юзеру, каким образом выглядят буквы в кириллических шрифтах для пользователей перевода Windows для России", "iPhone", "WindowsMobile",
                "BlackberryСъешь ещё этих мягких французских булок, да выпей чаю. — мем, порождённый русскоязычной версией Windows (вернее, программой fontview.exe, которая входит в дефолтную поставку, начиная аж с незапамятных времён Windows 95). Таким хитроумным способом Винда демонстрирует юзеру, каким образом выглядят буквы в кириллических шрифтах для пользователей перевода Windows для России", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2 Самый православный. Залезть в папку Windows/Fonts и открыть любой находящийся там файл кириллического шрифта (Arial сойдёт).\n" +
                "Самый кулхацкерный. Открыть Microsoft Word, набрать =rand() и нажать ENTER. Вуаля, добрый Ворд угостит нас сразу несколькими порциями любимой нямки. Вообще, там можно задавать ещё два параметра, чтобы варьиро", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        //final StableArrayAdapter adapter = new StableArrayAdapter(this,
        //        R.layout.message_cell, R.id.label, list);

        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.message_cell, R.id.label, list);
        //setListAdapter(adapter);

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, values);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
                intent.putExtra(EXTRA_MESSAGE, item);
                startActivity(intent);

                /*view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });*/
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        /*
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        */
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, R.layout.message_cell, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.message_cell, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values[position]);

            TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
            messageText.setText(values[position]);

            // Change the icon for Windows and iPhone
            String s = values[position];
            if (s.startsWith("Windows7") || s.startsWith("iPhone")
                    || s.startsWith("Solaris")) {
                imageView.setImageResource(R.drawable.ic_action_call);
            } else {
                imageView.setImageResource(R.drawable.ic_action_refresh);
            }

            return rowView;
        }
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
