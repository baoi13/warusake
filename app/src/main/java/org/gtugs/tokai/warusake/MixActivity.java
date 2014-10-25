package org.gtugs.tokai.warusake;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
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
        private ImageButton shareButton;

        private Handler handler = new Handler();

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mix, container, false);
            this.firstTextView = (TextView) rootView.findViewById(R.id.firstTextView);
            this.secondTextView = (TextView) rootView.findViewById(R.id.secondTextView);
            this.shareButton = (ImageButton) rootView.findViewById(R.id.shareButton);

            this.firstTextView.setText(randomText());
            this.secondTextView.setText("");
            this.shareButton.setVisibility(View.INVISIBLE);

            this.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share();
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
                        PlaceholderFragment.this.shareButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        private String makeShareText() {
            String first = this.firstTextView.getText().toString();
            String second = this.secondTextView.getText().toString();
            String text = first + "を" + second + "で割って飲みま〜す #warusake";

            return text;
        }

        private void share() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
            if (resInfo.isEmpty()) {
                return;
            }

            String shareText = makeShareText();
            ArrayList<Intent> shareIntentList = new ArrayList<Intent>();

            for (ResolveInfo info : resInfo) {
                Intent shareIntent = (Intent) intent.clone();
                if (info.activityInfo.packageName.toLowerCase().equals("com.facebook.katana")) {
                    /* facebookは文字をシェアできないので、対象から外す */
                } else {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    shareIntent.setPackage(info.activityInfo.packageName);
                    shareIntentList.add(shareIntent);
                }
            }

            Intent chooserIntent = Intent.createChooser(shareIntentList.remove(0), "友だちに伝える");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentList.toArray(new Parcelable[]{}));
            startActivity(chooserIntent);
        }
    }
}
