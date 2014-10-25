package org.gtugs.tokai.warusake;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;


public class MixActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mix, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private TextView firstTextView;
        private TextView secondTextView;
        private ImageButton tweetButton;

        private Handler handler = new Handler();

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mix, container, false);
            this.firstTextView = (TextView) rootView.findViewById(R.id.firstTextView);
            this.secondTextView = (TextView) rootView.findViewById(R.id.secondTextView);
            this.tweetButton = (ImageButton) rootView.findViewById(R.id.tweetButton);

            this.firstTextView.setText(randomText());
            this.secondTextView.setText("");
            this.tweetButton.setVisibility(View.INVISIBLE);

            this.tweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "http://twitter.com/share?text=" + tweetText();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });

            /* 時間が来たらSecondTaskを呼び出す */
            Timer timer = new Timer();
            timer.schedule(new SecondTask(), 3000);

            return rootView;
        }

        private String randomText() {
            /* TODO: ランダムで飲み物を返す処理を書く */
            return "ビール";
        }

        private class SecondTask extends TimerTask {
            @Override
            public void run() {
                PlaceholderFragment.this.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        PlaceholderFragment.this.secondTextView.setText(randomText());
                        PlaceholderFragment.this.tweetButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        private String tweetText() {
            String first = this.firstTextView.getText().toString();
            String second = this.secondTextView.getText().toString();
            String text = first + "を" + second + "で割って飲みま〜す #warusake";

            try {
                return URLEncoder.encode(text, "utf-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
    }
}
